# 架构日志分析 (Architecture Log Analysis)

## 1. 项目概览 (Project Overview)

**应用名称**: `CantingDemo01` (WebSocket 监控客户端)
**架构模式**: Clean Architecture (整洁架构) + MVVM (Model-View-ViewModel)
**技术栈**: Kotlin, Jetpack Compose, Hilt (依赖注入), Coroutines (协程 Flow), OkHttp (WebSocket), Gson.

本项目实现了一个健壮的实时监控客户端，它从 WebSocket 服务器消费数据，通过分层架构进行处理，并在基于 Compose 的 UI 上显示关键状态和警报。

## 2. 包结构分析 (Package Structure Analysis)

本项目采用典型的分层架构结构：

```
com.example.canting
├── data                 // [数据层] 纯数据处理，独立于 UI
│   ├── manager          // 底层网络管理
│   ├── model            // 数据层模型 (ConnectionState 等)
│   └── repository       // 数据抽象网关
├── di                   // [依赖注入] Hilt 模块
├── domain               // [领域层] 业务逻辑 & 纯定义
│   ├── model            // 业务模型 (实体)
│   └── usecase          // 业务行为 (交互器)
└── ui                   // [界面层] 展示层
    ├── activity         // Android 组件
    └── viewmodel        // 状态持有者
```

---

## 3. 详细组件分析 (Detailed Component Analysis)

### A. 数据层 (`com.example.canting.data`)

**目标**: 管理数据源 (WebSocket) 并为领域层提供干净的 API。

#### 1. `manager.WebSocketManager` (实现: `IWebSocketManager`)

- **角色**: 网络连接的“驱动器”。
- **为什么将 Manager 与 Repository 分离?**: 为了遵守 **单一职责原则 (SRP)**。Manager 只关心 _连接的维护_ (Socket 生命周期、心跳、原始字节)，而不关心 _数据的含义_。
- **关键机制**:
  - **自动重连**: 实现指数退避算法 (等待 1s, 2s, 4s...) 以防止服务器崩溃时产生风暴式请求。
  - **SharedFlow**: 使用 `MutableSharedFlow`，配置 `onBufferOverflow = DROP_OLDEST` 和 `extraBufferCapacity`。这对于高频实时数据至关重要；如果 UI 处理缓慢，我们丢弃旧帧而不是耗尽内存导致崩溃 (背压处理)。
  - **生命周期感知**: 处理 `onFailure` 和 `onClosed` 以自动触发恢复循环。

#### 2. `repository.DataControlRepository` (实现: `IDataControlRepository`)

- **角色**: “翻译官”和“守门员”。
- **为什么我们需要它?**:
  - **抽象**: 领域层不应该知道 "WebSockets" 的存在。它只请求 `dataFlow`。
  - **转换**: 使用 Gson 将原始 JSON 字符串转换为类型安全的 `SocketMessage` 密封类。
  - **错误隔离**: 在此处捕获解析错误，以防有效应用因错误的服务器数据而崩溃。

#### 3. `model.ConnectionState`

- **角色**: 描述链路状态 (`Connected`, `Connecting`, `Disconnected`, `Error`)。
- **为什么使用 Sealed Class?**: 允许 UI 在 `when` 表达式中详尽地处理所有状态，确保没有状态被忽略 (例如，在 `Connecting` 期间显示加载转圈)。

---

### B. 领域层 (`com.example.canting.domain`)

**目标**: 封装业务规则。此层是纯 Kotlin (理想情况下无 Android 依赖)。

#### 1. `model.SocketMessage` (Sealed Class)

- **角色**: 定义应用的“语言”。
- **子类**: `VehicleStatus`, `AbnormalState`, `RecorderState`, `UnknownMessage`.
- **为什么使用它?**: 类型安全。当 ViewModel 观察 `SocketMessage` 时，它可以不同地处理每个子类型，而无需不安全的强制转换。

#### 2. `usecase.GetAbnormalStateUseCase`

- **角色**: 故障检测的“大脑”。
- **为什么简单的过滤器不够?**: 此类实现了关键的 **时序逻辑 (Temporal Logic)**。
  - **消抖 (Debouncing)**: 只有当故障持续 > 6 秒 (`THRESHOLD_NS`) 时，才会被“确认”。
  - **有状态 (Stateful)**: 维护 `activeErrors` 映射表。
  - **单调时间**: 使用 `System.nanoTime()` 测量持续时间，不受用户更改系统时钟的影响。
  - **安全性**: 断开连接时立即清除状态，以防止“僵尸警报” (重连后显示陈旧错误)。

#### 3. `usecase.GetVehicleStatusUseCase` & `GetRecorderStateUseCase`

- **角色**: 过滤器。
- **为什么分开类?**: 为了保持逻辑解耦。如果车辆逻辑发生变化 (例如单位转换)，我们编辑此类而不触及记录仪逻辑。

---

### C. 界面层 (`com.example.canting.ui`)

**目标**: 渲染状态并捕获事件。

#### 1. `viewmodel.MainViewModel`

- **角色**: 状态持有者。
- **为什么依赖注入?**: 通过构造函数注入接收 UseCases，使测试变容易 (可以传递伪造的 UseCases)。
- **响应式设计**:
  - `confirmedFaults` 使用 `flatMapLatest`。这是一个复杂的模式：
    - _输入_: 连接状态。
    - _逻辑_: 如果已连接 -> 订阅故障逻辑；如果断开连接 -> 发射空列表。
    - _结果_: 当网络断开时，UI 自动清除故障，无需任何手动状态操作代码。

#### 2. `activity.MainActivity`

- **角色**: 屏幕。
- **技术**: Jetpack Compose.
- **为什么使用 Compose?**: 声明式 UI 完美匹配响应式数据流。
  - `collectAsState()`: 当 Flow 发射新数据时自动重组 UI。
  - 没有手动的 `textView.setText()` 调用 (命令式风格)，减少了 Bug。

---

### D. 依赖注入层 (`com.example.canting.di`)

**目标**: 组装。

#### 1. `NetworkModule` & `RepositoryModule`

- **角色**: 提供实例。
- **为什么使用 Hilt?**: 自动化复杂对象图的创建。
  - `@Singleton`: 确保应用范围内只有一个 WebSocket 连接打开。

## 4. 关键架构决策回顾 (Key Architectural Decisions Review)

1.  **为什么使用 Flow/Coroutines?**
    - 该应用从根本上是异步的 (网络事件、计时器)。协程廉价地处理并发，而 Flow 自然地模拟连续的数据流。

2.  **为什么使用 Sealed Classes 处理数据?**
    - 服务器协议是固定的但多样化的。密封类强制在编译时处理所有已知的消息类型。

3.  **为什么使用 "UseCase" 模式?**
    - 它防止 ViewModel 变成“上帝类”。复杂的逻辑 (如 6 秒计时器) 被提取出来，保持 UI 层愚蠢并专注于显示。

## 5. 总结 (Summary)

该架构优先考虑 **健壮性 (Robustness)** 和 **可维护性 (Maintainability)**。

- **健壮性**: 通过显式处理重连、解析错误和陈旧状态。
- **可维护性**: 通过严格分离关注点 (UI 不知道 JSON；Data 不知道故障规则)。
