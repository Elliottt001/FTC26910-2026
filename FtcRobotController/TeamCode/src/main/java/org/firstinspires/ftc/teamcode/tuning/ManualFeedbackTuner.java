package org.firstinspires.ftc.teamcode.tuning;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.TankDrive;
import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.TwoDeadWheelLocalizer;

// 这是一个用来手动调整反馈控制参数（PID）的程序
// 机器人会反复前后移动一段距离（默认64英寸）
// 你需要在 Dashboard 上调整参数，让机器人走得既快又准，不要震荡
public final class ManualFeedbackTuner extends LinearOpMode {
    // DISTANCE = 64: 定义了测试时机器人移动的距离，默认为 64 英寸。
    // 你可以在 FTC Dashboard 上动态修改这个距离
    public static double DISTANCE = 64;

    @Override
    public void runOpMode() throws InterruptedException {
        // 先判断你的机器人底盘类型（麦克纳姆轮还是坦克底盘）
        if (TuningOpModes.DRIVE_CLASS.equals(MecanumDrive.class)) {
            MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
            
            // 检查是否已经配置了里程计轮的位置参数
            // 如果你使用的是死轮定位（TwoDeadWheel 或 ThreeDeadWheel），但参数（Ticks）还是默认值 0，
            // 说明你还没有运行过 AngularRampLogger 来测量轮子的位置。
            // 程序会直接报错，防止在定位不准的情况下强行调参。
            if (drive.localizer instanceof TwoDeadWheelLocalizer) {
                if (TwoDeadWheelLocalizer.PARAMS.perpXTicks == 0 && TwoDeadWheelLocalizer.PARAMS.parYTicks == 0) {
                    throw new RuntimeException("Odometry wheel locations not set! Run AngularRampLogger to tune them.");
                }
            } else if (drive.localizer instanceof ThreeDeadWheelLocalizer) {
                if (ThreeDeadWheelLocalizer.PARAMS.perpXTicks == 0 && ThreeDeadWheelLocalizer.PARAMS.par0YTicks == 0 && ThreeDeadWheelLocalizer.PARAMS.par1YTicks == 1) {
                    throw new RuntimeException("Odometry wheel locations not set! Run AngularRampLogger to tune them.");
                }
            }
            waitForStart();

            // 核心测试循环：让机器人反复执行“前进 -> 后退”的直线运动
            // 你可以一边观察机器人的表现，一边在 FTC Dashboard 上实时修改 PID 参数
            while (opModeIsActive()) {
                // Actions.runBlocking: 阻塞当前线程，直到动作执行完毕
                // drive.actionBuilder(new Pose2d(0, 0, 0)): 每次循环都假设机器人从相对坐标 (0, 0, 0) 开始规划
                Actions.runBlocking(
                    drive.actionBuilder(new Pose2d(0, 0, 0))
                            .lineToX(DISTANCE) // 前进到目标距离
                            .lineToX(0)        // 返回原点
                            .build());
            }
        } else if (TuningOpModes.DRIVE_CLASS.equals(TankDrive.class)) {
            TankDrive drive = new TankDrive(hardwareMap, new Pose2d(0, 0, 0));

            // 检查是否已经配置了里程计轮的位置参数
            // (同上) 如果参数（Ticks）还是默认值 0，说明未校准，程序报错阻止运行。
            if (drive.localizer instanceof TwoDeadWheelLocalizer) {
                if (TwoDeadWheelLocalizer.PARAMS.perpXTicks == 0 && TwoDeadWheelLocalizer.PARAMS.parYTicks == 0) {
                    throw new RuntimeException("Odometry wheel locations not set! Run AngularRampLogger to tune them.");
                }
            } else if (drive.localizer instanceof ThreeDeadWheelLocalizer) {
                if (ThreeDeadWheelLocalizer.PARAMS.perpXTicks == 0 && ThreeDeadWheelLocalizer.PARAMS.par0YTicks == 0 && ThreeDeadWheelLocalizer.PARAMS.par1YTicks == 1) {
                    throw new RuntimeException("Odometry wheel locations not set! Run AngularRampLogger to tune them.");
                }
            }
            waitForStart();

            // 核心测试循环：让机器人反复执行“前进 -> 后退”的直线运动
            while (opModeIsActive()) {
                // Actions.runBlocking: 阻塞当前线程，直到动作执行完毕
                Actions.runBlocking(
                    drive.actionBuilder(new Pose2d(0, 0, 0))
                            .lineToX(DISTANCE)
                            .lineToX(0)
                            .build());
            }
        } else {
            throw new RuntimeException();
        }
    }
}

/*
使用方法：
连接 Dashboard: 电脑连接机器人 WiFi，浏览器打开 192.168.43.1:8080/dash。
运行程序: 在 Driver Station 上选择 ManualFeedbackTuner 并运行。
观察现象: 机器人会开始前后移动。
如果机器人动作迟缓，可能需要增大 kP。
如果机器人到达终点时剧烈晃动，说明 kP 太大或 kD 不够。
实时调参: 在 Dashboard 右侧找到 MecanumDrive (或 TankDrive) 的配置项，找到 axialGain, lateralGain, headingGain (或者对应的 PID 系数)，修改数值并保存。
验证: 机器人会在下一个循环中使用新的参数，观察运动是否变好了。
*/