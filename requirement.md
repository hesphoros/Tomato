Tomato 是一个基于Kotln语言编写的极简主义、数据导向型番茄钟.

我希望现在为这个软件添加一个功能,在设置-关于界面上  点击7次版本号+ 输入指定的字符串(hesphoros) 之后进入特定的界面(需符合Material Design规范) (这个界面展示显示`Hello RuanSiQi`的平滑过渡动画)

JetChat:
https://github.com/android/compose-samples


现在的需求是:
添加Chat模块:
展示完`Hello RuanSiQi`的平滑过渡动画之后,展示社交模块 ,此模块完全按照JetChat即可 ,目前对接编写UI,实际功能之后在进行完善.


触发：设置 → 关于 → 连续点击版本号（versionName (versionCode) 那一行）7 次。
口令：弹出 Material 3 风格的 BasicAlertDialog + OutlinedTextField（密码样式），输入 hesphoros（区分大小写）后点确认。




贴纸: 
WhatsApp Sample

# finshed
- 完善了 Jetchat 原生“查看资料”功能
    - 聊天消息头像点击进入；
    - 抽屉 Recent Profiles 点击进入
- 完善了 安卓添加桌面组件功能
- 联系人会话切换
- 加好友功能
- 初始会话策略
- 贴纸持久化
- 输入区bug
- 图片消息全屏查看
- 已从相册添加的贴纸持久化到贴纸区
- 新增本地贴纸持久化 key：jetchat_local_stickers_v1
- 导入本地贴纸后会：
- 发送到会话
- 写入本地持久化
- 贴纸面板新增“我的贴纸”分区，重进页面仍可看到并复用发送
- 图片类消息点击全屏查看
- 会话里的图片消息（包括内置贴纸图片 + 本地图片）可点击
- 点击后进入全屏预览（Dialog），再次点击关闭
- 需要登录功能 写个登录ui即可 预留接口 登录成功后才能使用聊天服务 默认保留登录状态

# todo:
- 聊天背景
- 电话铃声
- 电话功能


我希望使用c/c++ 来编写Jetchat的服务器后端 使用https json 通信 sqlite(存储账户信息 消息历历史记录)
代码需要跨平台; 尽量使用第三方库 避免自己编写大量代码 ,使用cmake管理代码

代码写在D:\codespace\Android\TomatoServer下

需要有 
- 加好友功能 
- 内置一些测试初始账户
- 可以发送图片 贴纸等 
- 支持消息同步

以下两个账户自动拥有好友
内置以下账户 格式为:[账户] [密码]
1. hesphoros hesphoros
2. ruansiqi ruansiqi
