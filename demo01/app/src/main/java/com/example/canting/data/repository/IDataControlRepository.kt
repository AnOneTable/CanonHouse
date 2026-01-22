package com.example.canting.data.repository

import com.example.canting.data.model.ConnectionState
import com.example.canting.domain.model.SocketMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IDataControlRepository {

    val connectionState: StateFlow<ConnectionState>

    val dataFlow: Flow<SocketMessage>

    // 启动连接
    fun connectToDataSource()

    // 断开连接
    fun disconnectFromDataSource()
}
