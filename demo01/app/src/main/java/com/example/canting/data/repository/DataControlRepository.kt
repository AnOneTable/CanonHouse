package com.example.canting.data.repository

import android.util.Log
import com.example.canting.data.manager.IWebSocketManager
import com.example.canting.data.model.ConnectionState
import com.example.canting.domain.model.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull

class DataControlRepository @Inject constructor(private val webSocketManager: IWebSocketManager) :
        IDataControlRepository {

    // 应该来自常量或配置
    private val WEBSOCKET_URL = "ws://10.20.83.127:8080"

    // 创建 Gson 实例 (也可以通过 DI 注入)
    private val gson = Gson()

    override val connectionState: StateFlow<ConnectionState> = webSocketManager.connectionState

    // 拦截原始消息流并进行解析，只分发解析成功的 SocketMessage
    override val dataFlow: Flow<SocketMessage> =
            webSocketManager
                    .messageFlow
                    .mapNotNull { rawMessage -> parseMessage(rawMessage) }
                    .flowOn(Dispatchers.IO)

    override fun connectToDataSource() {
        webSocketManager.connect(WEBSOCKET_URL)
    }

    override fun disconnectFromDataSource() {
        webSocketManager.disconnect()
    }

    /** 解析接收到的数据 根据 topic 字段将 msg 内容转换为具体的 Domain Model */
    private fun parseMessage(json: String): SocketMessage? {
        return try {
            // 1. 解析外层结构，获取 topic 和 timestamp_ns
            val envelope = gson.fromJson(json, SocketEnvelope::class.java)

            // 2. 根据 topic 解析具体的 msg 内容
            val message: SocketMessage =
                    when (envelope.topic) {
                        "/eabot/vehicle_status" -> {
                            gson.fromJson(envelope.msg, VehicleStatus::class.java)
                                    .copy(timestampNs = envelope.timestampNs)
                        }
                        "/eabot/abnormal_reporter" -> {
                            gson.fromJson(envelope.msg, AbnormalState::class.java)
                                    .copy(timestampNs = envelope.timestampNs)
                        }
                        "/eabot/state_reporter" -> {
                            gson.fromJson(envelope.msg, RecorderState::class.java)
                                    .copy(timestampNs = envelope.timestampNs)
                        }
                        else -> {
                            Log.d("DataRepository", "收到未知 Topic: ${envelope.topic}")
                            UnknownMessage(json, envelope.topic, timestampNs = envelope.timestampNs)
                        }
                    }
            Log.d("DataRepository", "解析成功: $message")
            message
        } catch (e: Exception) {
            Log.e("DataRepository", "解析失败: ${e.message} \nRaw: $json")
            // 解析失败不中断流，直接忽略
            null
        }
    }

    /** 内部数据类，用于解析外层信封结构 */
    private data class SocketEnvelope(
        val topic: String,
        @SerializedName("timestamp_ns") val timestampNs: Long,
        val msg: JsonElement // 延迟解析，先拿到 JsonElement
    )
}
