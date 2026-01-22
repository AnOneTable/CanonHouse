package com.example.canting.data.manager

import android.util.Log
import com.example.canting.data.model.ConnectionState
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.pow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*

/**
 * WebSocket 管理器标准接口定义
 *
 * 架构设计说明： 定义接口是为了符合依赖倒置原则 (DIP)，上层业务模块 (如 Repository) 只依赖抽象接口 `IWebSocketManager`， 而不依赖具体实现
 * `WebSocketManager`。这样方便进行单元测试 (Mock Manager) 和后续替换底层实现 (比如换成 Netty)。
 */
interface IWebSocketManager {
    /** 连接状态流，UI 层监听此状态来展示 "连接中"、"已断开" 或 "错误" 提示 */
    val connectionState: StateFlow<ConnectionState>

    /** 消息数据流，业务层监听此流来处理服务器下发的实时数据 */
    val messageFlow: SharedFlow<String>

    /**
     * 发起连接
     * @param url WebSocket 服务器地址 (e.g., ws://192.168.1.1:8080/ws)
     */
    fun connect(url: String)

    /** 主动断开连接 通常在用户退出登录或退出应用时调用 */
    fun disconnect()

    /**
     * 发送文本消息
     * @return 发送请求是否成功入队 (注意：返回 true 不代表服务器已收到，只代表本地已发送)
     */
    fun sendMessage(message: String): Boolean
}

/**
 * WebSocket 管理器实现类 (模板代码)
 *
 * 核心功能：
 * 1. **全生命周期管理**：负责建立连接、保持连接、断开连接。
 * 2. **自动重连机制**：实现指数退避算法，避免在服务器宕机时发起风暴式重连。
 * 3. **心跳保活**：依赖 OkHttp 的 Ping/Pong 机制维持长连接稳定性。
 * 4. **状态分发**：使用 Kotlin Flow 向上传递连接状态和业务数据。
 *
 * @param client OkHttpClient 实例，应当在 DI 模块中配置好 Ping 间隔 (心跳)。
 */
class WebSocketManager @Inject constructor(private val client: OkHttpClient) :
        IWebSocketManager, WebSocketListener() { // 继承 WebSocketListener 以监听底层事件

    companion object {
        private const val TAG = "WebSocketManager"

        // --- 配置常量 (架构师建议：提取到配置文件或 BuildConfig 中) ---

        // 最大重连间隔：60秒。避免等待时间无限拉长导致用户体验极差。
        private const val MAX_RETRY_DELAY = 60_000L
        // 初始重连间隔：1秒。首次重试要快，尽快恢复连接。
        private const val INITIAL_RETRY_DELAY = 1_000L
        // 正常关闭的状态码 (RFC 6455 标准)。表示用户主动离开，不需要重连。
        private const val NORMAL_CLOSE_CODE = 1000
        // 消息缓冲区大小。处理突发的高频消息，防止背压导致数据丢失。
        private const val FLOW_BUFFER_CAPACITY = 128
        // 指数退避基数。每次重试间隔 * 2 (1s -> 2s -> 4s ...)。
        private const val RECONNECT_BASE = 2.0
    }

    // --- 内部状态 ---

    // 持有当前活跃的 WebSocket 实例，为空表示当前无连接。
    private var webSocket: WebSocket? = null
    // 记录当前的连接地址，用于重连时使用。
    private var webSocketUrl: String? = null

    // --- 状态流定义 ---

    // [ConnectionState] 使用 StateFlow，因为它不仅分发事件，还持有当前最新的状态 (Sticky)。
    // 初始状态为 Disconnected。
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    // 暴露为不可变 StateFlow，防止外部遭到修改。
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // --- 消息流定义 ---

    // [MessageFlow] 使用 SharedFlow，适用于高频事件流。
    // replay = 0: 新订阅者不需要收到过期的历史消息 (实时性要求高)。
    // onBufferOverflow = DROP_OLDEST: 当消费速度跟不上生产速度时，丢弃最老的数据，保证展示最新数据。
    private val _messageFlow =
            MutableSharedFlow<String>(
                    replay = 0,
                    extraBufferCapacity = FLOW_BUFFER_CAPACITY,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
    override val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow()

    // --- 协程作用域 ---

    // 创建一个独立的协程作用域用于跑后台任务 (如重连倒计时)。
    // SupervisorJob(): 其中的一个子任务失败 (Crash) 不会连累整个 Scope 取消，保证稳定性。
    // Dispatchers.IO: 所有网络相关操作都在 IO 线程执行。
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 重连任务的 Handle。用于在连接成功或用户主动断开时取消正在进行的重连倒计时。
    private var reconnectJob: Job? = null
    // 当前重试次数计数器，用于计算指数退避时间。
    private var retryCount = 0
    // 标记是否是用户主动断开。如果是主动断开，则不触发自动重连逻辑。
    private var isUserDisconnect = false

    /** 发起 WebSocket 连接 (线程安全) */
    @Synchronized // 加锁防止多线程同时调用导致创建多个连接实例
    override fun connect(url: String) {
        Log.i(TAG, "业务层请求建立连接, URL: $url")

        // 状态检查：如果已经连接或正在连接，且 URL 没变，则忽略此次请求 (幂等性)。
        if (webSocket != null || isConnectingOrConnected()) {
            if (webSocketUrl != url) {
                // 如果 URL 变了 (比如切换了服务器环境)，先断开旧连接。
                Log.w(TAG, "检测到 URL 变更，正在断开旧连接...")
                disconnect()
            } else {
                Log.d(TAG, "当前已处于连接或连接中状态，忽略重复请求")
                return
            }
        }

        // 重置用户断开标记，表示这是一次新的意图。
        isUserDisconnect = false
        // 保存 URL 用于后续重连。
        webSocketUrl = url
        // 更新状态为 Connecting，UI 层可以展示 Loading 动画。
        _connectionState.value = ConnectionState.Connecting
        // 开始初始化底层连接。
        initWebSocket()
    }

    /** 初始化底层 WebSocket 实例 */
    private fun initWebSocket() {
        val url =
                webSocketUrl
                        ?: run {
                            Log.e(TAG, "初始化失败: URL 为空")
                            return
                        }

        Log.d(TAG, "正在初始化 OkHttp WebSocket 实例...")

        // 构建 Request 对象。可以根据需要添加 Header (如 Token 认证)。
        // request.header("Authorization", "Bearer $token")
        val request = Request.Builder().url(url).build()

        // 创建新的 WebSocket。
        // this: 传入当前类作为 Listener 接收回调。
        // 注意：newWebSocket 会立即发起网络连接。
        webSocket = client.newWebSocket(request, this)
    }

    /** 断开连接 (线程安全) */
    @Synchronized
    override fun disconnect() {
        Log.i(TAG, "业务层请求断开连接 (用户主动操作)")

        // 标记为用户主动断开，阻止自动重连逻辑。
        isUserDisconnect = true
        // 取消任何正在进行的重连任务。
        reconnectJob?.cancel()

        webSocket?.let {
            // 发送标准关闭帧 (Code 1000)。
            // 这是一个优雅关闭过程，服务器收到后也会回复关闭帧。
            val result = it.close(NORMAL_CLOSE_CODE, "Client disconnect")
            Log.d(TAG, "发送关闭帧结果: $result")
        }

        // 清理引用，防止内存泄漏。
        webSocket = null
        // 立即更新状态为 Disconnected。
        _connectionState.value = ConnectionState.Disconnected
        // 重置重试计数器，下次连接从头开始。
        retryCount = 0
    }

    /** 发送消息 */
    override fun sendMessage(message: String): Boolean {
        // 使用局部变量引用，避免多线程下的空指针隐患。
        val ws = webSocket
        return if (ws != null) {
            val result = ws.send(message) // 调用 OkHttp 发送接口
            if (!result) {
                Log.w(TAG, "消息发送失败 (底层返回 false): $message")
            }
            result
        } else {
            Log.e(TAG, "发送失败: 当前未连接")
            false
        }
    }

    // ===========================================
    // OkHttp WebSocketListener 回调 (核心事件处理)
    // ===========================================

    /** 连接成功回调 对应 HTTP 握手成功，连接正式建立 (101 Protocols Switching)。 */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.i(TAG, "WebSocket 连接建立成功 (onOpen)")

        // 1. 更新状态为 Connected。
        _connectionState.value = ConnectionState.Connected
        // 2. 连接成功，重置重试计数器。
        retryCount = 0
        // 3. 取消任何挂起的重连任务 (防御性编程)。
        reconnectJob?.cancel()
    }

    /** 收到文本消息回调 服务器下发数据时触发。 */
    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        // Log.v(TAG, "收到消息: $text") // 生产环境建议关闭详细日志，避免刷屏

        // 将消息发送到 SharedFlow 中供业务层消费。
        // tryEmit 是非阻塞的，适合在高频回调中使用。
        val emitted = _messageFlow.tryEmit(text)
        if (!emitted) {
            Log.w(TAG, "警告: 消息缓冲区已满，旧消息被丢弃!")
        }
    }

    /** 连接关闭中回调 服务器请求关闭连接 (收到关闭帧)。 */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.i(TAG, "收到服务器关闭请求 (onClosing): code=$code, reason=$reason")

        // 响应服务器的关闭请求，完成挥手过程。
        webSocket.close(NORMAL_CLOSE_CODE, "Ack closing")
        // 更新状态。
        _connectionState.value = ConnectionState.Disconnected
        // 移除引用。
        this.webSocket = null

        // 触发自动重连尝试 (除非是服务器明确拒绝且不再允许连接，这里简化处理统一尝试重连)。
        attemptReconnect()
    }

    /** 连接已彻底关闭回调 */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.i(TAG, "连接已彻底断开 (onClosed): code=$code, reason=$reason")

        _connectionState.value = ConnectionState.Disconnected
        this.webSocket = null

        attemptReconnect()
    }

    /** 连接失败回调 发生网络错误 (断网、超时) 或 协议错误 时触发。 这是处理 "心跳超时" 和 "异常断线" 的核心地方。 */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e(TAG, "连接发生异常 (onFailure): ${t.message}", t)

        // 更新状态为 Error，将异常信息暴露给上层 (UI 可展示错误提示)。
        _connectionState.value = ConnectionState.Error(t)
        this.webSocket = null

        // 核心：触发自动重连机制。
        attemptReconnect()
    }

    // ===========================================
    // 自动重连逻辑 (核心算法)
    // ===========================================

    /** 尝试重连 包含对 用户主动断开 的判断 和 重连去重逻辑。 */
    private fun attemptReconnect() {
        // 1. 如果是用户主动调用 disconnect()，则不进行重连。
        if (isUserDisconnect) {
            Log.i(TAG, "检测到用户主动断开，取消自动重连")
            return
        }

        // 2. 如果当前已经有重连任务在跑，不要重复创建。
        if (reconnectJob?.isActive == true) return

        // 3. 启动协程执行延时重连。
        reconnectJob =
                scope.launch {
                    // 计算本次退避时间
                    val delayTime = calculateReconnectDelay()

                    Log.d(TAG, "将在 ${delayTime}ms 后执行第 ${retryCount + 1} 次重连...")

                    // 更新状态为 Connecting，让 UI 保持 "连接中" 的状态感。
                    _connectionState.value = ConnectionState.Connecting

                    // 挂起等待 (非阻塞)。
                    delay(delayTime)

                    // 时间到，增加计数器。
                    retryCount++

                    // 发起重新初始化。
                    Log.i(TAG, "开始执行重连...")
                    initWebSocket()
                }
    }

    /** 计算指数退避延迟时间 算法：Delay = Initial * (Base ^ RetryCount) 结果会限制在 MAX_RETRY_DELAY 范围内。 */
    private fun calculateReconnectDelay(): Long {
        if (retryCount == 0) return 0L // 第一次失败立即重试 (可选)

        // 计算指数：2^0, 2^1, 2^2 ...
        val backoffMultiplier = RECONNECT_BASE.pow(retryCount.toDouble())
        val delay = (INITIAL_RETRY_DELAY * backoffMultiplier).toLong()

        // 取当前计算值和最大值的较小者，防止等待时间过长。
        return min(delay, MAX_RETRY_DELAY)
    }

    /** 辅助方法：判断当前是否处于 有效连接 状态 */
    private fun isConnectingOrConnected(): Boolean {
        val state = _connectionState.value
        return state == ConnectionState.Connecting || state == ConnectionState.Connected
    }
}
