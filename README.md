# 桌面电量 BatteryDeskWidget

极简实时电量桌面组件，按红米 K50 Pro / HyperOS 使用场景设计。

## 样式
- 左侧：大号电池方块
- 右侧：细圆形电量环
- 圆环中央：系统电量百分比
- 黑白高对比

## 实时刷新
打开 App 后启动前台服务，动态监听系统 ACTION_BATTERY_CHANGED。
电量变化后立即刷新桌面 Widget。电量值直接读取 Android 系统，不自行估算。

## 红米设置
1. 允许应用自启动
2. 电池策略设为「无限制」
3. 允许通知
4. 不要强制停止应用
5. 每次重启手机后建议打开一次应用

## 添加组件
桌面长按空白处 → 小组件 → 桌面电量。

## GitHub Actions
Actions → Build Battery Widget APK → Run workflow
成功后从 Artifacts 下载 BatteryDeskWidget-v1.0.0-apk。
