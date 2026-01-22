package com.example.canting.data.model

/** WebSocket 连接状态 */
sealed class ConnectionState {
    object Disconnected : ConnectionState() // 已断开
    object Connecting : ConnectionState() // 连接中
    object Connected : ConnectionState() // 已连接
    data class Error(val cause: Throwable) : ConnectionState() // 发生错误
}
