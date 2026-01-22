package com.example.canting.domain.model

import com.google.gson.annotations.SerializedName

/** Socket 消息基类 (Sealed Class) 使用密封类可以方便下游 (UseCase/ViewModel) 进行 when 表达式处理，保证类型安全。 */
sealed class SocketMessage {
    abstract val timestamp: Long // 本地接收时间 (保留)
    abstract val timestampNs: Long // 业务发生时间戳 (纳秒)
}

/** 车辆状态数据 Topic: /eabot/vehicle_status */
data class VehicleStatus(
        @SerializedName("speed") val speed: Int,
        @SerializedName("limit") val limit: Int,
        @SerializedName("driving_state") val drivingState: String,
        @SerializedName("max_speed") val maxSpeed: Int,
        override val timestamp: Long = System.currentTimeMillis(),
        override val timestampNs: Long = 0L // 默认为0，由 Repository 填充
) : SocketMessage()

/** 异常报警数据 Topic: /eabot/abnormal_reporter */
data class AbnormalState(
    @SerializedName("error_code") val errorCodes: List<Long>,
    override val timestamp: Long = System.currentTimeMillis(),
    override val timestampNs: Long = 0L
) : SocketMessage()

/** 数据记录仪状态 Topic: /eabot/state_reporter */
data class RecorderState(
        @SerializedName("node_name") val nodeName: String,
        @SerializedName("state") val state: Int,
        // "optional" 字段可能是个 JSON 字符串，根据日志看是 "{}"，暂定义为 String
        @SerializedName("optional") val optional: String,
        override val timestamp: Long = System.currentTimeMillis(),
        override val timestampNs: Long = 0L
) : SocketMessage()

/** 未知或不可解析的消息 用于保底，防止新协议导致 crash */
data class UnknownMessage(
        val rawJson: String,
        val topic: String,
        override val timestamp: Long = System.currentTimeMillis(),
        override val timestampNs: Long = 0L
) : SocketMessage()
