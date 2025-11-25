package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

public class IntakeCommand extends CommandBase {
    Intake intake;
    Gamepad gamepad1;

    public IntakeCommand (Gamepad gamepad1, Intake intake) { //()里传参
        this.intake = intake; //this. = instance variable(上面的), 右面的 = ()里的
        this.gamepad1 = gamepad1;
        // 声明独占权：告诉调度器（Scheduler），这个命令（IntakeCommand）需要使用 intake 这个子系统
        // 机器人上同一个部件（比如进气口 Intake）同一时间只能被一个命令控制。如果有新的命令启动：停止（cancel） 之前那个正在运行的命令，把控制权交给这个新的命令
        addRequirements(intake);
    }

    // 将操作手的输入（软件信号）与机器人的实际动作（硬件行为）绑定在一起
    public void execute() {
        if (gamepad1.right_trigger > 0.3 ){
        intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
    }else if (gamepad1.left_trigger > 0.3){
            intake.setIntakeState(Intake.IntakeTransferState.Split_Out);
    }else if (gamepad1.right_bumper) {
            intake.setIntakeState(Intake.IntakeTransferState.Send_It_Up);
    }else {
            intake.setIntakeState(Intake.IntakeTransferState.Intake_Steady);
    }
    }

    @Override
    public void end(boolean interrupted) {
        intake.setIntakePower(0);
    }
}
