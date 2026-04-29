# Tomato 内嵌聊天（JetChat / Hello RuanSiQi）说明

本文描述 **Android 客户端** 在收到新聊天消息时的 **本地通知** 行为与设计意图。

## 收到消息时的通知（隐私伪装）

- 当轮询同步到 **对方发送的新消息**（`senderId != 当前登录用户`）时，应用会发一条 **本地通知**。
- **通知标题与正文不包含**：发件人昵称、消息正文、图片/贴纸内容或会话名称，避免在锁屏、状态栏或穿戴设备上泄露聊天隐私。
- 文案与渠道刻意 **贴近 Tomato 番茄钟「专注计时」** 的观感（与计时器使用同一系统通知渠道 **`timer`**，小图标为应用图标，强调色与计时通知一致），便于在视觉上归类为「番茄钟状态」，而非典型 IM 推送。

### 文案资源

- 默认英文：`shared/src/androidMain/res/values/strings.xml`  
  - `jetchat_discreet_notif_title` — `Focus · Tomato`  
  - `jetchat_discreet_notif_text` — `Session in progress`
- 简体中文：`shared/src/androidMain/res/values-zh-rCN/strings.xml`  
  - `专注 · Tomato` / `当前番茄钟进行中`

可按需在其它 `values-*` 中补充翻译。

### 系统权限与节流

- **Android 13+**：需用户授予 **通知权限**（`POST_NOTIFICATIONS`）。未授予时静默跳过，不崩溃。
- **节流**：同一会话方向下，约 **5 秒内最多触发一次**，避免连续多条消息刷屏。

### 实现位置（便于维护）

| 作用 | 路径 |
|------|------|
| 多平台接口与 Composable `expect` | `shared/.../jetchat/ChatInboundDiscreetNotifier.kt` |
| Android 本地通知 | `shared/.../jetchat/ChatInboundDiscreetNotifier.android.kt` |
| 在消息增量同步处触发 | `shared/.../screens/HelloRuanSiQiScreen.kt`（`LaunchedEffect` 轮询 `syncMessages` 分支） |

### 限制说明

- 当前为 **前台轮询** 触发的本地通知，**不是** FCM/高可靠推送；应用进程被系统杀死后不会收到。
- 仅 **Android** 有 `actual` 实现；其它 Kotlin Multiplatform 目标若接入需自行补 `actual` 或空实现。

---

## 网络权限与「连接失败」（FOSS 分发）

### 应用是否已经声明「网络权限」？

- **`android.permission.INTERNET`** 已在 `androidApp/src/main/AndroidManifest.xml` 中声明。  
- 在 Android 上，**`INTERNET` 属于安装时授予的普通权限**：系统**不会**像相机/通知那样再弹一次「是否允许使用网络」——因此你会感觉「没有索要网络权限」，这是正常现象。  
- 另已声明 **`ACCESS_NETWORK_STATE`**（可选）：用于判断当前是否有网络连接等，多数设备同样**无单独弹窗**。

### 新设备安装 FOSS APK 后 `Failed to connect` 的常见原因

1. **服务器地址不可达**  
   默认或保存的 Base URL 若是 **`https://192.168.x.x:8443`**，只有手机与服务器在**同一局域网**且 IP 正确时才能连上。换 SIM 流量、换 Wi‑Fi 或服务器未开机时都会超时失败。  
   **处理**：在登录界面填写可从该手机访问到的地址（公网域名、或当前 Wi‑Fi 下电脑的局域网 IP），必要时在侧栏「服务器」里修改。

2. **使用 `http://` 被系统禁止明文流量**  
   Android 9 起默认禁止 HTTP 明文。  
   **处理**：FOSS 构建已通过 `androidApp/src/foss/AndroidManifest.xml` 合并 **`android:usesCleartextTraffic="true"`**，便于内网 `http://` 调试；生产环境仍建议 **HTTPS**。

3. **自签证书**  
   JetChat 客户端对 HTTPS 使用开发用信任逻辑；若仍失败，请核对端口、防火墙与服务器是否监听 `0.0.0.0`。

客户端 HTTP 超时已在 `JetchatServerGateway.android.kt` 中放宽（连接约 20s、读约 60s），弱网下可减少误报超时。

### FOSS 包名（与商店版区分）

`foss` 变体在 `androidApp/build.gradle.kts` 中配置了 **`applicationIdSuffix = ".foss"`**，安装后包名为 **`org.nsh07.pomodoro.foss`**，与 Play 上的 **`org.nsh07.pomodoro`** 不是同一个应用，可**同时安装**，也有利于减少部分机型把侧载包误判成「非官方商店包」而无法安装的情况。

### 「缺乏开发者证书」/ 无法安装（签名方案）

部分国产系统（尤其华为系）对 **仅 v2/v3 签名的 APK** 校验不友好，会提示类似 **「软件包缺乏开发者证书」**——通常不是没签名，而是 **缺少 JAR（v1）签名块**。

1. **用 Android Studio 导出 APK 时**  
   在签名向导最后一步，**务必同时勾选**：  
   - **V1 (Jar Signature)**  
   - **V2 (Full APK Signature)**（建议 v3 也勾选，若界面提供）

2. **用命令行打带混淆的 Release**  
   - 在仓库根目录复制 `keystore.properties.example` 为 **`keystore.properties`**（已加入 `.gitignore`），填入 `storeFile` / 密码 / `keyAlias`。  
   - `androidApp/build.gradle.kts` 已为 **`sideloadRelease`** 与 **`debug`** 显式打开 **v1 + v2 + v3**。  
   - 未提供 `keystore.properties` 时，**`assembleFossRelease` 会临时使用 debug 证书**（仅自用调试；对外分发请配置自己的 keystore）。

---

## 与 TomatoServer 的关系

聊天数据与账号仍由 **TomatoServer**（HTTPS + SQLite）提供；客户端不包含注册入口。服务端与 REST 约定见 TomatoServer 仓库内 `docs/API.md`。
