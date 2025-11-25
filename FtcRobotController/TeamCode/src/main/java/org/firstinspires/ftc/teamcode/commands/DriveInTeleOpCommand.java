package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

public class DriveInTeleOpCommand extends CommandBase {
    Drivetrain drivetrain;
    Gamepad gamepad1;

    public DriveInTeleOpCommand (Gamepad gamepad1, Drivetrain drivetrain) { //()里传参
        this.drivetrain = drivetrain; //this. = instance variable(上面的), 右面的 = ()里的
        this.gamepad1 = gamepad1;
        addRequirements(drivetrain);
    }

    @Override //Annotation, 重写super class的函数
    public void initialize() {}

    // 机器人手动操作模式（TeleOp）的核心控制逻辑。
    // 它定义了当你推手柄摇杆时，机器人该怎么动
    @Override
    public void execute() { // 调度器 scheduler periodically calls the function
        // teleDrive 的三个参数：前后速度、左右速度、旋转速度
        // gamepad1.left_stick_y： 左摇杆的垂直推量；
        // gamepad1.left_stick_x：左摇杆的水平推量；
        // gamepad1.right_stick_x：右摇杆的水平推量
        // -0.9：前后速度取反，符合手柄推杆的正负方向；0。9是限速系数。表示最大只给 90% 的动力，防止机器人太冲或者保护电机。
        // 0.9：左右速度限速系数
        // 0.7：旋转速度限速系数，旋转的速度比直线跑的速度要慢一点。通常是为了让旋转更可控，不容易转过头。
        drivetrain.teleDrive(-0.9*gamepad1.left_stick_y, 0.9*gamepad1.left_stick_x, 0.7*gamepad1.right_stick_x);
    }

    @Override
    public void end(boolean interrupted) {}

    @Override
    public boolean isFinished() {

        return false;
    }
}
