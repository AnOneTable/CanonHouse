package com.example.canting.di

import com.example.canting.data.manager.IWebSocketManager
import com.example.canting.data.manager.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .pingInterval(15, TimeUnit.SECONDS) // 设置心跳间隔 (15秒：平衡保活与电量，防 NAT 超时)
                .readTimeout(0, TimeUnit.MILLISECONDS) // WebSocket 不应设置读取超时
                .build()
    }

    @Provides
    @Singleton
    fun provideWebSocketManager(client: OkHttpClient): IWebSocketManager {
        return WebSocketManager(client)
    }
}
