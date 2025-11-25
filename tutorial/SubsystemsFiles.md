# Subsystems 文件夹详解

`FtcRobotController/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems` 文件夹下的代码是机器人的**“器官”**。每个文件代表机器人身体的一个独立部分，负责管理具体的硬件（电机、舵机、传感器）并提供操作接口。

以下是每个文件的简明讲解：

1.  **`Drivetrain.java` (底盘/动力传动系统)**
    *   **是什么**：机器人的“腿”。
    *   **管什么**：4 个麦克纳姆轮电机 (`frontLeft`, `frontRight`, `backLeft`, `backRight`)。
    *   **能干嘛**：
        *   `teleDrive(...)`：根据手柄的输入（前后、左右、旋转）计算每个轮子的速度，让机器人全向移动。
        *   提供获取电机功率的方法，用于调试。

2.  **`Intake.java` (进气系统)**
    *   **是什么**：机器人的“嘴”和“食道”。
    *   **管什么**：
        *   `intake` 电机：负责把球从地上吸进来。
        *   `transfer` 电机：负责把球从肚子里输送到飞轮口。
        *   `swingBar` 舵机：负责把进气口放下（贴地吸球）或收起。
        *   `transferBreakBeam` 传感器：检测球是不是已经到位了。
    *   **能干嘛**：
        *   `setIntakeState(...)`：切换状态（吸入、吐出、输送、静止）。
        *   `setSwingBarPos(...)`：控制进气口抬起/放下。
        *   配合射击系统自动输送弹药。

3.  **`Shooter.java` (射击系统)**
    *   **是什么**：机器人的“枪”。
    *   **管什么**：
        *   飞轮电机（通常是两个，或者一个带飞轮的大电机）：负责把球射出去。
        *   可能还有调节角度的舵机（虽然代码里没细看，但通常会有）。
    *   **能干嘛**：
        *   `setShooterStatus(...)`：切换状态（停止、怠速预热、全速射击）。
        *   `updateFlywheelPID()`：利用 PID 算法保持飞轮转速稳定，不受电池电压波动影响。
        *   `isAtTargetRPM()`：告诉别人“我现在转速够不够，能不能开火”。

4.  **`MyLimelight.java` (视觉系统)**
    *   **是什么**：机器人的“眼睛”。
    *   **管什么**：Limelight 智能摄像头。
    *   **能干嘛**：
        *   `initBluePipeline()` / `initRedPipeline()`：切换识别蓝球还是红球。
        *   `getDis()`, `getX()`, `getTx()`：告诉机器人目标（AprilTag 或球）在哪里、有多远。
        *   `isFocused()`：判断是否对准了目标。

5.  **`Scheduler.java` (调度器 - *非标准*)**
    *   **是什么**：这看起来是一个自定义的、简化的任务调度器（注意：FTC SDK 有自带的 `CommandScheduler`，这个可能是作者自己写的轻量级版本，或者用于特定用途）。
    *   **管什么**：管理一些需要周期性运行的任务。
    *   **能干嘛**：在 `BLUE_Far_12ball.java` 里看到被引用，可能用于在自动阶段协调某些非 Command-Based 的动作。

6.  **`ScheduledTask.java` (定时任务 - *非标准*)**
    *   **是什么**：配合上面的 `Scheduler` 使用的任务单元。
    *   **能干嘛**：定义一个在特定时间或条件下执行的动作。

7.  **`ExampleSubsystem.java` (示例子系统)**
    *   **是什么**：教学模版。
    *   **能干嘛**：展示如何写一个标准的 Subsystem（如何继承 `SubsystemBase`，如何写构造函数等），实际比赛中没用。

### 总结
*   **动**：`Drivetrain` (跑), `Intake` (吃), `Shooter` (吐)。
*   **看**：`MyLimelight`。
*   **管**：`Scheduler` (可能用于辅助自动流程)。
