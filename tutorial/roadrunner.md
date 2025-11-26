github仓库：https://github.com/acmerobotics/road-runner-quickstart

使用文档：https://rr.brott.dev/docs/v1-0/tuning/

# RoadRunner 故障诊断指南

在 RoadRunner 调参过程中，不同的现象对应着不同的参数文件。以下是一个故障诊断指南，帮助你快速定位要修改的文件。

## 1. 现象：定位不准 (Localization Issues)

**症状**:
*   运行 `LocalizationTest`，你把机器人往前推 1 米，Dashboard 上显示走了 1.2 米（或 0.8 米）。
*   你把机器人旋转 90 度，Dashboard 上显示转了 85 度（或 95 度）。
*   机器人静止不动，但 Dashboard 上的坐标在缓慢漂移。

**去哪里改**:
*   **`MecanumDrive.java`** (如果你用的是 Pinpoint 或 DriveLocalizer)
*   **`OTOSLocalizer.java`** (如果你用的是 OTOS)
*   **`ThreeDeadWheelLocalizer.java`** (如果你用的是三轮里程计)

**改什么参数**:
*   `inPerTick` (每脉冲英寸数): 解决距离比例不对的问题。
*   `trackWidthTicks` (轮距): 解决旋转角度不对的问题（针对 DriveLocalizer）。
*   `offset` (安装偏移量): 解决旋转时圆心乱跑的问题。

## 2. 现象：动作执行不到位 (Control Issues)

**症状**:
*   运行 `SplineTest` 或 `StraightTest`。
*   机器人虽然在走，但总是**走不到终点**（差几厘米）。
*   机器人**走过头了**，然后又倒回来。
*   机器人走直线时**左右扭动**（画龙）。

**去哪里改**:
*   **`MecanumDrive.java`** -> `Params` 类

**改什么参数**:
*   `axialGain` (纵向增益): 解决前后走不到位的问题。
*   `lateralGain` (横向增益): 解决横移不到位或走直线跑偏的问题。
*   `headingGain` (航向增益): 解决转弯角度不对或车头乱摆的问题。
*   `kS`, `kV`, `kA` (前馈参数): 解决起步无力或高速时跟不上的问题。

## 3. 现象：物理极限设置错误 (Constraints Issues)

**症状**:
*   机器人加速太猛，轮子打滑。
*   机器人转弯太快，差点翻车。
*   机器人跑得太慢，像老太太散步。

**去哪里改**:
*   **`MecanumDrive.java`** -> `Params` 类

**改什么参数**:
*   `maxWheelVel` (最大轮速): 限制最高速度。
*   `minProfileAccel` / `maxProfileAccel` (最大加减速度): 限制起步和刹车的猛烈程度。
*   `maxAngVel` / `maxAngAccel` (最大角速度/角加速度): 限制旋转的快慢。

## 4. 现象：电机方向反了 (Motor Direction Issues)

**症状**:
*   运行 `LocalizationTest`，推左摇杆往前，机器人往后跑。
*   推左摇杆往左，机器人往右横移。
*   推右摇杆旋转，机器人原地乱转或方向反了。

**去哪里改**:
*   **`MecanumDrive.java`** -> 构造函数

**改什么参数**:
*   `leftFront.setDirection(...)` 等代码。把 `FORWARD` 改成 `REVERSE`，或者反过来。

## 总结表

| 现象 | 关键词 | 修改文件 | 关键参数 |
| :--- | :--- | :--- | :--- |
| 距离/角度读数不对 | **定位 (Localization)** | `MecanumDrive.java` (Params) <br> 或 `Localizer` 实现类 | `inPerTick`, `trackWidth` |
| 走不到位、走过头、抖动 | **PID 控制 (Control)** | `MecanumDrive.java` (Params) | `axialGain`, `lateralGain`, `headingGain` |
| 打滑、翻车、太慢 | **约束 (Constraints)** | `MecanumDrive.java` (Params) | `maxVel`, `maxAccel` |
| 遥控方向反了 | **电机方向 (Direction)** | `MecanumDrive.java` (Constructor) | `setDirection` |