package com.example.canting.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canting.data.model.ConnectionState
import com.example.canting.data.repository.IDataControlRepository
import com.example.canting.domain.usecase.GetAbnormalStateUseCase
import com.example.canting.domain.usecase.GetRecorderStateUseCase
import com.example.canting.domain.usecase.GetVehicleStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel
@Inject
constructor(
        private val repository: IDataControlRepository,
        getVehicleStatusUseCase: GetVehicleStatusUseCase,
        getAbnormalStateUseCase: GetAbnormalStateUseCase,
        getRecorderStateUseCase: GetRecorderStateUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        // 告警阈值 6秒 = 6 * 10^9 纳秒
        private const val THRESHOLD_NS = 6_000_000_000L
    }

    val connectionState: StateFlow<ConnectionState> = repository.connectionState

    // --- 数据展示流 ---

    val vehicleInfo: StateFlow<String> =
            getVehicleStatusUseCase()
                    .map { "速度: ${it.speed}/${it.limit}, 模式: ${it.drivingState}" }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "等待车辆数据...")

    val recorderInfo: StateFlow<String> =
            getRecorderStateUseCase()
                    .map { "节点: ${it.nodeName}, 状态码: ${it.state}" }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "等待记录仪状态...")

    // --- 智能告警逻辑 ---

    // 直接从 UseCase 获取处理好的"确诊故障"流
    // 使用 flatMapLatest 监听连接状态：
    // 1. 当状态变为 Connected 时，启动新的 UseCase 流 (全新的 activeErrors Map)
    // 2. 当状态变为 Disconnected/Connecting 时，发射空列表并取消旧的 UseCase 流 (清理状态)
    // 完美满足"不留缓存"和"每次连接取最新数据"的需求
    @OptIn(ExperimentalCoroutinesApi::class)
    val confirmedFaults: StateFlow<List<Long>> =
            connectionState
                    .flatMapLatest { state ->
                        if (state is ConnectionState.Connected) {
                            getAbnormalStateUseCase()
                        } else {
                            flowOf(emptyList())
                        }
                    }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 原始报警数据流 (UI展示用不到，如果需要调试可保留，但现在逻辑已移出)
    // 这里为了 keeping code clean，我们不再在 VM 里处理原始数据

    init {
        connect()
    }

    fun connect() {
        repository.connectToDataSource()
    }

    fun disconnect() {
        repository.disconnectFromDataSource()
    }
}
