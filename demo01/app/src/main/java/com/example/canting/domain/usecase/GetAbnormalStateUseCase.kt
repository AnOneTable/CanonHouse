package com.example.canting.domain.usecase

import android.util.Log
import com.example.canting.data.model.ConnectionState
import com.example.canting.data.repository.IDataControlRepository
import com.example.canting.domain.model.AbnormalState
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** 用例：获取异常报警流 (包含智能消抖和时长过滤逻辑) 现在是一个 Stateful 的用例，内部维护了故障计时状态 */
class GetAbnormalStateUseCase @Inject constructor(private val repository: IDataControlRepository) {

    companion object {
        private const val TAG = "GetAbnormalUseCase"
        // 告警阈值 6秒
        private const val THRESHOLD_NS = 6_000_000_000L
    }

    operator fun invoke(): Flow<List<Long>> =
            channelFlow {
                        // 活跃故障表: ErrorCode -> 首次上报时间戳 (纳秒)
                        // 移至 Flow 内部，确保每次收集都是全新的状态 (防Zombie)
                        val activeErrors = ConcurrentHashMap<Long, Long>()

                        launch {
                            repository.dataFlow.filterIsInstance<AbnormalState>().collect { msg ->
                                // 严防死守：只有在连接状态下才处理数据
                                // 防止 Disconnect 瞬间或之后有残留数据混入 map
                                if (repository.connectionState.value is ConnectionState.Connected) {
                                    // 关键修改：使用 System.nanoTime() 保证单调性，避免 System.currentTimeMillis()
                                    // 被网络校时导致的时间跳变
                                    val currentTime = System.nanoTime()
                                    val currentCodes = msg.errorCodes.map { it }.toSet()

                                    // 新增
                                    currentCodes.forEach { code ->
                                        if (!activeErrors.containsKey(code)) {
                                            activeErrors[code] = currentTime
                                            Log.d(TAG, "检测到新故障 Pending: $code, Time: $currentTime")
                                        }
                                    }
                                    // 移除已消失的故障
                                    activeErrors.keys.removeIf { !currentCodes.contains(it) }
                                }
                            }
                        }

                        // 2. 监听连接状态 (保持不变)
                        launch {
                            repository.connectionState.collect { state ->
                                if (state !is ConnectionState.Connected) {
                                    if (activeErrors.isNotEmpty()) {
                                        Log.d(TAG, "连接断开，重置故障状态")
                                        activeErrors.clear()
                                        send(emptyList())
                                    }
                                }
                            }
                        }

                        // 3. 定时监测 (Ticket Loop)
                        launch {
                            while (isActive) {
                                delay(50)

                                if (repository.connectionState.value is ConnectionState.Connected) {
                                    val now = System.nanoTime()

                                    // 筛选出满足 6秒 阈值的故障
                                    // 注意：这里不重置时间戳，因为 UI 需要持续显示(常亮)，而不是闪烁
                                    val confirmed =
                                            activeErrors
                                                    .filter { (_, startTime) ->
                                                        val duration = now - startTime
                                                        val triggers = duration >= THRESHOLD_NS
                                                        triggers
                                                    }
                                                    .keys
                                                    .sorted()
                                                    .toList()

                                    send(confirmed)
                                }
                            }
                        }
                    }
                    .distinctUntilChanged() // 防抖，只有列表内容变化时才发射
}
