package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.arcrobotics.ftclib.gamepad.ButtonReader;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.commands.DriveInTeleOpCommand;
import org.firstinspires.ftc.teamcode.commands.IntakeCommand;
import org.firstinspires.ftc.teamcode.commands.LimelightLockInCommand;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MyLimelight;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

import java.util.List;

// 手动操作：主遥控程序，包含底盘、射击、进气、视觉瞄准的完整控制逻辑。
@TeleOp
public class BohanTele extends CommandOpMode {
    private Drivetrain drivetrain;
    private Intake intake;
    private Shooter shooter;
    private MyLimelight limelight;

    private boolean xjustpressed = false;
    private boolean xholding = false;
    private boolean yjustpressed = false;
    private boolean yholding = false;



    @Override
    public void initialize() { //Init button on DriverHUB
        //Settings Stuff....Make sure to create a "xxx = new...." before using it to avoid nullPointerObject error
        // 遥测（定位）系统初始化
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // 两个游戏手柄
        GamepadEx gamepadEx1 = new GamepadEx(gamepad1);
        GamepadEx gamepadEx2 = new GamepadEx(gamepad2);
        //Subsystems
        // 这部分的具体实现，硬件映射在各自的子系统类里完成：FtcRobotController/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems
        // 很方便的就对照着看
        drivetrain = new Drivetrain(hardwareMap);
        // 底盘的默认操作
        // 控制权限给了 gamepad1，说明它是主控，gamepad是副控
        drivetrain.setDefaultCommand(new DriveInTeleOpCommand(gamepad1, drivetrain));
        intake = new Intake(hardwareMap);
        // 用默认值初始化：将进气口的摇臂（Swing Bar）舵机设置到 0.4 的位置（0.4：是一个经过调试测量的具体物理位置，应该是代表安全位置）
        intake.setSwingBarPos(0.4);
        intake.setDefaultCommand(new IntakeCommand(gamepad1, intake));
        shooter = new Shooter(hardwareMap);
        limelight = new MyLimelight(hardwareMap);
        shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
        intake.setIntakeState(Intake.IntakeTransferState.Intake_Steady);


        //Commands
        LimelightLockInCommand limelightLock = new LimelightLockInCommand(drivetrain, limelight, gamepad1);
        //Driver One - Button A toggles RPM (0→3000→4000→5000→0)

        // 按键绑定 (Button Binding)
        // 定义了当你在手柄上按下特定按钮时，机器人应该执行什么动作。

        // 动作：按下手柄 1 的 X 键。
        // 功能：切换 (Toggle) limelightLock 命令的开关状态。
        // 场景：当大概对准了目标，按下 X 键让机器人自动微调瞄准；瞄准好了或者想放弃瞄准，再按一下 X 键取消。
        gamepadEx1.getGamepadButton(GamepadKeys.Button.X).toggleWhenPressed(limelightLock);
        
        // 动作：按下手柄 1 的 A 键。
        // 功能：将进气摇臂 (Swing Bar) 放到位置 0。
        // 场景：位置 0 通常是“放下”或“工作”位置。当你想要吸地上的球时，按下 A 键，摇臂放下来贴近地面。
        gamepadEx1.getGamepadButton(GamepadKeys.Button.A).whenPressed(() -> intake.setSwingBarPos(0));
        
        // 动作：松开手柄 1 的 A 键。
        // 功能：将进气摇臂 (Swing Bar) 回到位置 0.4。
        // 场景：这结合上一句构成了一个“按住生效” (Hold-to-Action) 的逻辑。按住 A 键吸球，松开 A 键摇臂自动抬起，防止撞到东西。
        gamepadEx1.getGamepadButton(GamepadKeys.Button.A).whenReleased(() ->intake.setSwingBarPos(0.4));
        gamepadEx1.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).whenPressed(() -> limelight.initBluePipeline());
        
        // 动作：按下手柄 1 的 十字键右 (D-Pad Right)。
        // 功能：初始化 Limelight 摄像头的红色管道 (Red Pipeline)。
        // 场景：告诉机器人“我现在是红队，你要去识别红色的球或红色的目标”。
        gamepadEx1.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT).whenPressed(() -> limelight.initRedPipeline());
        //DRIVER TWO
    }


    @Override
    public void run() {
        // 初始化和检测状态
        CommandScheduler.getInstance().run();
        shooter.periodic();

        // 自动射击协调机制：射击(shoot) + 进气(intake) + 视觉(Limelight)系统配合
        if(shooter.shooterStatus == Shooter.ShooterStatus.Shooting){
            // 在射击时，告诉进气系统：现在是自动射击模式
            // 进气系统的任务: 它需要把已经存在肚子里的球，自动地、源源不断地往上输送给飞轮。
            intake.updateAutoshoot(true);
            // 同时告诉进气系统：飞轮是否已经达到目标转速
            // 进气系统的任务: 它需要根据飞轮的转速情况，来决定输送球的节奏和力度。
            // 转速达标了，就可以加快输送，确保射击效率和准确性；转速没达标，就要放慢输送，避免浪费球或者堵塞。
            intake.updateautotranse(shooter.isAtTargetRPM());
            // 告诉射击系统：当前 Limelight 视觉系统测量到的目标距离
            // 射击系统的任务: 它需要根据这个距离，动态调整飞轮的转速，以确保射击的精准度。
            shooter.updateDis(limelight.getDis());
            // 告诉射击系统：Limelight 视觉系统当前是否已经成功对准目标
            // 对准了就可以开火
            shooter.updateFocused(limelight.isFocused());
        }
        else{
            intake.updateAutoshoot(false);
        }

        // 手动实现一个“单次触发” (Just Pressed) ，更新按键状态

        if(gamepad1.x){ // 1. 检测到物理按键被按住了
            if(!xholding){ // 2. 检查上一帧是不是没按住（如果是没按住，说明这是按下的第一帧）
                xjustpressed = true; // 3. 标记：这一帧是“刚刚按下”的瞬间！
                xholding = true;     // 4. 标记：现在处于“按住”状态了，下一帧别再触发了。
            }
        }
        else{ // 5. 物理按键松开了
            xholding = false;    // 6. 重置状态，为下一次按下做准备。
            xjustpressed = false;
        }

        // 同理
        if(gamepad1.y){
            if(!yholding){
                yjustpressed = true;
                yholding = true;
            }
        }
        else{
            yholding = false;
            yjustpressed = false;
        }


        // 射击系统状态机 (Shooter State Machine) 的核心控制逻辑：切换射击状态
        // 三种状态： 停止 (Stop)、怠速 (Idling)、射击 (Shooting)

        // Y键：切换 停止 ↔ 怠速
        if(yjustpressed&&shooter.shooterStatus != Shooter.ShooterStatus.Shooting){
            if(shooter.shooterStatus == Shooter.ShooterStatus.Idling) {
                shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
            }
            else{
                shooter.setShooterStatus(Shooter.ShooterStatus.Idling);
            }
            yjustpressed = false;
        }
        // X键：切换 射击 ↔ 怠速/停止（即 开火 ↔ 停火）
        if(xjustpressed){
            if(shooter.shooterStatus == Shooter.ShooterStatus.Shooting){
                shooter.setShooterStatus(Shooter.ShooterStatus.Idling);
            }
            else{
                shooter.setShooterStatus(Shooter.ShooterStatus.Shooting);
            }
            xjustpressed = false;

        }

        // 通过 shooter & limelight 检测各种状态数据，并输出到遥测面板，方便调试和监控机器人运行情况
        telemetry.addData("Shooter Target RPM", shooter.getTargetRPM());
        telemetry.addData("Shooter Current RPM", shooter.getFlyWheelRPM());
        telemetry.addData("PIDoutput", shooter.getCurrentPIDOutput());
        telemetry.addData("Shooter At Target", shooter.isAtTargetRPM() ? "YES" : "NO");
        telemetry.addData("Gamepad1 Right Stick X", gamepad1.right_stick_x);
        telemetry.addData("Gamepad2 Left Stick Y", gamepad2.left_stick_y);
        telemetry.addData("Gamepad2 Right Stick Y", gamepad2.right_stick_y);
        telemetry.addData("Apriltag dist", limelight.getDis());
        telemetry.addData("Apriltag X", limelight.getX());
        telemetry.addData("Apriltag(PoI) Tx", limelight.getTx());
        telemetry.addData("Apriltag ID", limelight.getAprilTagID());
        telemetry.addData("Pitch", limelight.getPitch());
        telemetry.addData("Shooterdis", shooter.distance);


//        telemetry.addData("FL Power", drivetrain.getFrontLeftPower());
//        telemetry.addData("FR Power", drivetrain.getFrontRightPower());
//        telemetry.addData("BL Power", drivetrain.getBackLeftPower());
//        telemetry.addData("BR Power", drivetrain.getBackRightPower());
        telemetry.update();
    }
}

/*
关于文件命名……？
一开始一直没看懂为什么这个文件要这样命名，还以为是遇到了不会的单词……
问了问AI：

这个文件名 BohanTele 很有可能是一个人名加上模式名的组合。
Bohan: 这极有可能是你们战队里某位队员的名字（比如叫 Bohan 的同学）。
在 FTC 战队中，经常会有不同的操作手（Driver）习惯不同的按键布局。
比如 Bohan 习惯按 A 键吸球，而另一位队员可能习惯按 Trigger 吸球。
为了互不干扰，大家会各自写一份 TeleOp 程序，比如 BohanTele.java, AliceTele.java。

笑

*/