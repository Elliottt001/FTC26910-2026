# FTC26910-2026 代码阅读顺序推荐

为了让你能够有逻辑、由浅入深地理解整个项目，我为你规划了一条阅读路线。我们将从最直观的**手动操作**开始，理解**命令式编程**架构，然后深入**底层驱动**（RoadRunner & Pedro），最后看**自动程序**和**调试工具**。

---

## 第一阶段：从手动操作入手 (TeleOp & Command-Based)
**目标**：理解机器人是如何动起来的，以及“子系统-命令-操作模式”的架构。

1.  **`opmodes/BohanTele.java`**
    *   **入口点**。这是主 TeleOp 程序。
    *   **看点**：`initialize()` 中如何实例化子系统，如何将手柄按键绑定到命令（`whenPressed`, `toggleWhenPressed`），以及 `run()` 循环中的状态机逻辑。
2.  **`subsystems/Drivetrain.java`**
    *   **基础硬件**。
    *   **看点**：`hardwareMap.get` 如何获取电机，`teleDrive` 方法如何通过数学公式实现麦克纳姆轮的全向移动。
3.  **`commands/DriveInTeleOpCommand.java`**
    *   **连接逻辑**。
    *   **看点**：`execute()` 方法。它不断地从手柄读取数据，传给 `Drivetrain` 子系统。这是 Command 模式的典型应用。
4.  **`subsystems/Intake.java`**
    *   **功能子系统**。
    *   **看点**：如何控制进气电机和舵机（Swing Bar）。
5.  **`commands/IntakeCommand.java`**
    *   **进气逻辑**。
    *   **看点**：进气的默认行为。
6.  **`subsystems/Shooter.java`**
    *   **高级子系统**。
    *   **看点**：比底盘复杂，包含了 PID 控制（保持飞轮转速）、状态机（Stop/Idling/Shooting）以及根据距离计算转速的逻辑。
7.  **`subsystems/MyLimelight.java`**
    *   **视觉传感器**。
    *   **看点**：如何封装 Limelight，获取目标距离和偏差。
8.  **`commands/LimelightLockInCommand.java`**
    *   **闭环控制**。
    *   **看点**：如何利用视觉反馈（Limelight）来自动调整底盘旋转（Drivetrain），实现自动对准。
9.  **`subsystems/Scheduler.java`** & **`subsystems/ScheduledTask.java`**
    *   **辅助工具**。
    *   **看点**：一个简单的自定义延时任务调度器。

---

## 第二阶段：深入底层驱动 (RoadRunner & Localization)
**目标**：理解自动阶段的核心——机器人如何知道自己在哪里（定位），以及如何走到目标点（路径规划）。这是最硬核的部分。

10. **`MecanumDrive.java`**
    *   **核心驱动**。RoadRunner v1.0 的灵魂。
    *   **看点**：`Params` 类里的物理参数（`inPerTick`, `trackWidthTicks`），`FollowTrajectoryAction`（路径跟随逻辑）。它是连接硬件和路径算法的桥梁。
11. **`Localizer.java`**
    *   **接口定义**。
    *   **看点**：定义了所有定位器都必须有的功能：`update()` 和 `getPose()`。
12. **`PinpointLocalizer.java`**
    *   **当前使用的定位器**。
    *   **看点**：如何读取 GoBilda Pinpoint 硬件的数据，并转换成 RoadRunner 需要的坐标格式。
13. **`OTOSLocalizer.java`**
    *   **备用定位器**。
    *   **看点**：SparkFun OTOS 光流传感器的实现，可以对比 Pinpoint 看看区别。
14. **`ThreeDeadWheelLocalizer.java`** & **`TwoDeadWheelLocalizer.java`**
    *   **传统定位器**。
    *   **看点**：了解传统的编码器里程计是怎么写的（作为参考）。
15. **`messages/` 文件夹下的所有文件**
    *   **数据传输**。
    *   **看点**：`PoseMessage.java` 等。这些是发给网页 Dashboard 看的数据包，扫一眼即可。
16. **`Drawing.java`**
    *   **可视化**。
    *   **看点**：如何在 Dashboard 上画机器人和路径。

---

## 第三阶段：另一种路径规划选择 (Pedro Pathing)
**目标**：了解项目中混用的另一套路径规划库。

17. **`pedroPathing/Constants.java`**
    *   **Pedro 配置**。
    *   **看点**：对比 `MecanumDrive.java`，这里也定义了电机名字、PID 参数和定位器偏移量。**注意：这里的参数需要和 RoadRunner 保持一致。**
18. **`pedroPathing/Tuning.java`**
    *   **Pedro 调试**。
    *   **看点**：Pedro 库的调试工具。

---

## 第四阶段：自动程序实现 (Autonomous)
**目标**：看懂如何利用底层驱动写出完整的自动比赛程序。

19. **`opmodes/BLUE_Far_12ball.java`** (以及其他 `RED_...`, `BLUE_...`)
    *   **实战代码**。
    *   **看点**：如何构建路径（`Trajectory` 或 `Path`），如何将路径动作（Drive）和机构动作（Intake/Shoot）串联起来。
20. **`opmodes/ExampleOpMode.java`**
    *   **模板**。

---

## 第五阶段：调试与校准 (Tuning)
**目标**：了解如何测试和校准机器人参数。

21. **`tuning/LocalizationTest.java`**
    *   **定位测试**。最常用的工具。
    *   **看点**：推着机器人走，看坐标对不对。
22. **`tuning/ManualFeedbackTuner.java`**
    *   **PID 调参**。
23. **`tuning/SplineTest.java`**
    *   **路径测试**。让机器人跑曲线。
24. **`tuning/PIDveltest.java`**
    *   **速度测试**。
25. **`tuning/TuningOpModes.java`**
    *   **注册入口**。
26. **`opmodes/PIDMotorsTuning.java`**
    *   **独立调参**。专门调节电机 PID 的程序。

---

## 第六阶段：其他
27. **`TankDrive.java`**
    *   **参考**。坦克底盘实现，除非换底盘否则不用看。
28. **`subsystems/ExampleSubsystem.java`** & **`commands/ExampleCommand.java`**
    *   **模板**。
29. **`readme.md`**
    *   **文档**。

---

**建议**：
*   先在 IDE 里打开 `BohanTele.java`，按住 `Ctrl` (或 `Cmd`) 点击类名跳转，顺藤摸瓜地看。
*   遇到不懂的 RoadRunner 概念（如 `Pose2d`, `TrajectoryAction`），再去查阅 `MecanumDrive.java`。
