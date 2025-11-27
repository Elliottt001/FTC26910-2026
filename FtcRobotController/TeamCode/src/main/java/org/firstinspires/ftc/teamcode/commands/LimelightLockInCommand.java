package org.firstinspires.ftc.teamcode.commands;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandBase;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MyLimelight;

@Config
public class LimelightLockInCommand extends CommandBase {
    Drivetrain drivetrain;
    MyLimelight limelight;
    Gamepad gamepad1;
    public static double Kp = 0.018;

    public LimelightLockInCommand(Drivetrain drivetrain, MyLimelight limelight, Gamepad gamepad1) { //()里传参
        this.drivetrain = drivetrain; //this. = instance variable(上面的), 右面的 = ()里的
        this.limelight = limelight;  // <--- 拿到“眼睛”的控制权
        this.gamepad1 = gamepad1;
        addRequirements(drivetrain, limelight);
        // 从此以后，Command 就可以随时调用 Subsystem 的方法了。
    }

    // 当你按下手柄上的某个键触发这个命令时，initialize() 会被执行一次。
    // 调用关系：Command 调用 MyLimelight.startDetect()。
    // Subsystem 响应：MyLimelight 里的 llenable 变为 true，它的 periodic() 方法开始不断更新 aprilTagLatestResult 数据。
    @Override // Annotation, 重写super class的函数
    public void initialize() {
        gamepad1.rumble(200);
        limelight.startDetect();
    }

    @Override
    public void execute() { // scheduler periodically calls the function
        drivetrain.teleDrive(-0.9 * gamepad1.left_stick_y, 0.9 * gamepad1.left_stick_x,
                Kp * limelight.getTx());
                // 这里调用了 MyLimelight 里的 getTx() 方法，获取水平偏移角度

    }

    // 调用关系：Command 调用 MyLimelight.stopDetect()。
    // Subsystem 响应：MyLimelight 里的 llenable 变为 false，停止处理视觉数据，节省资源。
    @Override
    public void end(boolean interrupted) {
        gamepad1.rumble(200);
        limelight.stopDetect();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}