package org.firstinspires.ftc.teamcode.tuning;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.TankDrive;

// 这是一个用来测试样条曲线（Spline）路径跟随的程序
// 机器人会尝试走一条 S 形的曲线路径
// 如果机器人能走完这条路径并且最后停在正确的位置，说明路径跟随功能正常
//
// 测试后的调参指南：
// 1. 如果机器人偏离路径（Dashboard 上实际轨迹和绿色目标轨迹不重合）：
//    - 主要是跟随 PID (Follower PID) 的问题。
//    - 在 MecanumDrive 类中调整 axialGain (纵向), lateralGain (横向), headingGain (转向)。
//    - 增加这些值可以让机器人更紧地跟随路径，但太大会导致震荡。
//
// 2. 如果机器人在转弯时打滑、甩尾或动作太剧烈：
//    - 说明物理约束 (Constraints) 设置过高。
//    - 在 MecanumDrive 类中减小 MAX_WHEEL_VEL (最大轮速), MAX_PROFILE_ACCEL (最大加速度)。
//
// 3. 如果 Dashboard 上显示路径走得很完美，但机器人实际停的位置不对：
//    - 说明里程计 (Localization) 不准。
//    - 需要重新检查 Dead Wheel 的参数 (ticksPerInch) 或轮距 (trackWidth)。
public final class SplineTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // 设定机器人的初始位置为 (0, 0)，朝向为 0 弧度（通常是朝向场地右侧）
        Pose2d beginPose = new Pose2d(0, 0, 0);
        if (TuningOpModes.DRIVE_CLASS.equals(MecanumDrive.class)) {
            MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

            waitForStart();

            // 定义并执行一个样条曲线路径
            // 这是一个非常经典的测试路径，用来验证机器人的全向移动能力和路径跟随精度
            Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        // 第一段样条曲线：移动到 (30, 30)，结束时的切线方向为 90度 (Math.PI / 2)
                        // 这意味着机器人到达 (30, 30) 时，它的运动方向是垂直向上的
                        .splineTo(new Vector2d(30, 30), Math.PI / 2)
                        // 第二段样条曲线：移动到 (0, 60)，结束时的切线方向为 180度 (Math.PI)
                        // 这意味着机器人到达 (0, 60) 时，它的运动方向是向左的
                        // 整个路径形成一个类似 "S" 形或倒 "U" 形的曲线
                        .splineTo(new Vector2d(0, 60), Math.PI)
                        .build());
        } else if (TuningOpModes.DRIVE_CLASS.equals(TankDrive.class)) {
            TankDrive drive = new TankDrive(hardwareMap, beginPose);

            waitForStart();

            // 定义并执行一个样条曲线路径 (同上)
            Actions.runBlocking(
                    drive.actionBuilder(beginPose)
                            .splineTo(new Vector2d(30, 30), Math.PI / 2)
                            .splineTo(new Vector2d(0, 60), Math.PI)
                            .build());
        } else {
            throw new RuntimeException();
        }
    }
}

/*
你应该先用 ManualFeedbackTuner 把基础 PID 调好，然后再跑 SplineTest。如果 SplineTest 跑
*/