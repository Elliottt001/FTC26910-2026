# FTC26910-2026 项目文件结构与功能详解

这个 FTC 机器人项目结构，结合了 **RoadRunner v1.0**（用于自动阶段路径规划）、**Pedro Pathing**（另一种路径规划库）以及 **FTCLib Command-Based**（基于命令的编程模式，用于 TeleOp 和整体架构）。

## 1. 根目录 (`org.firstinspires.ftc.teamcode`)
这个目录主要包含了 **RoadRunner v1.0** 的核心驱动类和定位器实现。

*   **`MecanumDrive.java`**:
    *   **核心文件**。这是 RoadRunner v1.0 的麦克纳姆轮底盘实现类。
    *   它定义了机器人的物理参数（如 `inPerTick`, `trackWidthTicks`, `kS`, `kV`, `kA` 等）。
    *   包含了路径跟随 (`FollowTrajectoryAction`) 和转向 (`TurnAction`) 的具体逻辑。
    *   它将电机、IMU 和定位器（Localizer）组合在一起，是自动阶段（Auto）控制底盘的核心。

*   **`TankDrive.java`**:
    *   类似于 `MecanumDrive.java`，但是用于 **坦克底盘**（Tank Drive / Differential Drive）。如果你的机器人是麦克纳姆轮，这个文件通常用不到。

*   **`Localizer.java`**:
    *   这是一个接口（Interface），定义了定位器必须具备的方法（如 `update()` 更新位置，`getPose()` 获取位置）。所有的定位方式（Pinpoint, OTOS, 3轮里程计等）都要实现这个接口。

*   **`PinpointLocalizer.java`**:
    *   **GoBilda Pinpoint 里程计电脑**的驱动实现。
    *   它实现了 `Localizer` 接口，负责从 Pinpoint 硬件读取 x, y 坐标和航向角，并转换成 RoadRunner 能理解的格式。

*   **`OTOSLocalizer.java`**:
    *   **SparkFun OTOS (Optical Tracking Odometry Sensor)** 的驱动实现。
    *   类似于 Pinpoint，它使用光流传感器进行定位。

*   **`ThreeDeadWheelLocalizer.java`**:
    *   标准的 **三轮里程计**（3-Wheel Odometry）实现。
    *   如果你使用的是传统的三个全向轮（左、右、横向）加编码器的方式，就用这个。

*   **`TwoDeadWheelLocalizer.java`**:
    *   标准的 **两轮里程计**（2-Wheel Odometry）实现。
    *   通常配合 IMU 使用（两个平行轮测距离，IMU 测角度）。

*   **`PinpointOdometryExample.java`**:
    *   一个官方或示例 OpMode，用于演示如何单独使用 Pinpoint 传感器，通常用于测试硬件是否正常工作。

*   **`Drawing.java`**:
    *   工具类。包含了一些在 FTC Dashboard（网页仪表盘）上绘制机器人图形、路径和点的辅助方法。

*   **`readme.md`**:
    *   项目的说明文档。

---

## 2. `subsystems/` (子系统文件夹)
这个文件夹采用了 **Command-Based（基于命令）** 的编程模式。每个类代表机器人上的一个物理子系统（硬件抽象层）。

*   **`Drivetrain.java`**:
    *   **底盘子系统**。
    *   它与根目录下的 `MecanumDrive.java` 不同，这个通常用于 **TeleOp（手动阶段）**。
    *   它封装了 4 个电机，提供了 `teleDrive()` 方法，用于根据手柄的 x, y, rx 输入来控制麦克纳姆轮的运动。
    *   继承自 FTCLib 的 `SubsystemBase`。

*   **`Intake.java`**:
    *   **进气子系统**。
    *   控制进气电机（吸球）和可能的舵机（如 `Swing Bar` 摇臂）。
    *   包含设置进气状态（`Intake_Steady`, `Intake_Transfer` 等）和舵机位置的方法。

*   **`Shooter.java`**:
    *   **射击子系统**。
    *   控制飞轮电机（Flywheel）。
    *   包含 PID 控制逻辑（保持转速稳定）、状态机（`Stop`, `Idling`, `Shooting`）以及根据距离调整转速的逻辑。

*   **`MyLimelight.java`**:
    *   **视觉子系统**。
    *   封装了 Limelight 摄像头的功能。
    *   提供获取目标距离 (`getDis`)、水平偏差 (`getTx`)、是否对准 (`isFocused`) 以及切换红/蓝管道的方法。

*   **`Scheduler.java`**:
    *   一个自定义的简单调度器。
    *   用于在指定时间后执行任务（`addTaskAfter`）。注意：`BohanTele` 中主要使用的是 FTCLib 的 `CommandScheduler`，这个可能是为了某些轻量级定时任务设计的。

*   **`ScheduledTask.java`**:
    *   配合 `Scheduler.java` 使用，定义了一个被调度的任务（包含执行时间和具体逻辑）。

*   **`ExampleSubsystem.java`**:
    *   子系统模板文件，用于复制粘贴创建新的子系统。

---

## 3. `commands/` (命令文件夹)
这里定义了机器人具体的“动作”。命令连接了“输入”（手柄）和“输出”（子系统）。

*   **`DriveInTeleOpCommand.java`**:
    *   **手动驾驶命令**。
    *   它将手柄的摇杆读数（`left_stick_y`, `left_stick_x`, `right_stick_x`）传递给 `Drivetrain` 子系统。
    *   这是 TeleOp 期间一直运行的默认命令。

*   **`IntakeCommand.java`**:
    *   **进气控制命令**。
    *   定义了进气系统的默认行为或特定操作逻辑。

*   **`LimelightLockInCommand.java`**:
    *   **视觉自动对准命令**。
    *   当按下某个键时，利用 Limelight 的视觉数据自动调整底盘旋转，使机器人对准目标（Lock In）。

*   **`ExampleCommand.java`**:
    *   命令模板文件。

---

## 4. `opmodes/` (运行模式文件夹)
这里是你在 Driver Station 手机上选择并运行的程序入口。

*   **`BohanTele.java`**:
    *   **主手动程序 (TeleOp)**。
    *   它初始化了所有子系统（Drivetrain, Intake, Shooter, Limelight）。
    *   它定义了按键绑定（例如：按 A 吸球，按 X 自动瞄准）。
    *   它包含了一个状态机逻辑，协调射击、进气和视觉系统的配合。

*   **`BLUE_Far_12ball.java`, `RED_Near_12ball.java` 等**:
    *   **自动程序 (Autonomous)**。
    *   文件名通常代表策略：`颜色_位置_任务`（例如：蓝队_远端_12个球）。
    *   这些文件通常会调用 RoadRunner 或 Pedro Pathing 来跑路径。

*   **`PIDMotorsTuning.java`**:
    *   用于调节电机 PID 参数的工具程序。

*   **`ExampleOpMode.java`**:
    *   OpMode 模板。

---

## 5. `pedroPathing/` (Pedro Pathing 路径规划)
这是除了 RoadRunner 之外的另一套路径规划库的配置。

*   **`Constants.java`**:
    *   **Pedro Pathing 的配置文件**。
    *   非常重要！里面定义了机器人的质量 (`mass`)、PID 参数 (`translationalPIDFCoefficients`, `drivePIDFCoefficients`)、电机名称、以及 Pinpoint 定位器的安装位置偏移量 (`forwardPodY`, `strafePodX`)。

*   **`Tuning.java`**:
    *   可能包含用于调试或自动调整 Pedro Pathing 参数的代码。

---

## 6. `messages/` (消息/日志文件夹)
这些文件主要用于 **RoadRunner 仪表盘 (Dashboard)** 的数据传输和日志记录。

*   **`PoseMessage.java`, `DriveCommandMessage.java`, `MecanumCommandMessage.java` 等**:
    *   这些是简单的数据类（POJO）。
    *   当 RoadRunner 运行时，它会把机器人的位置、速度、电机电压等信息打包成这些“消息”对象，发送给网页端进行绘图和分析。你一般不需要修改这些文件。

---

## 7. `tuning/` (调参文件夹)
这些是 **RoadRunner** 自带的调试程序，用于校准底盘参数。

*   **`LocalizationTest.java`**:
    *   **最常用的测试程序**。
    *   推着机器人走，看 Dashboard 上的坐标是否跟着动且准确。用于验证定位器（Pinpoint/里程计）是否配置正确。

*   **`SplineTest.java`**:
    *   让机器人跑一条曲线，测试路径跟随的准确性。

*   **`ManualFeedbackTuner.java`**:
    *   用于手动调节 PID 参数。

*   **`PIDveltest.java`**:
    *   测试速度 PID 控制。

*   **`TuningOpModes.java`**:
    *   用于注册上述调试 OpMode 的辅助类。

### 总结
*   **改自动路径参数**：去 `org.firstinspires.ftc.teamcode` 下的 `MecanumDrive.java` (RoadRunner) 或 `pedroPathing/Constants.java` (Pedro)。
*   **改手动操作按键**：去 `opmodes/BohanTele.java`。
*   **改硬件配置（电机名字/方向）**：
    *   自动阶段：`MecanumDrive.java`。
    *   手动阶段：`subsystems/Drivetrain.java`。
    *   Pedro Pathing：`pedroPathing/Constants.java`。
    *   **注意：这三个地方的配置需要保持一致！**
