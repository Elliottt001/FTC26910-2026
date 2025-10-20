package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.commands.ExampleCommand;
import org.firstinspires.ftc.teamcode.subsystems.ExampleSubsystem;

@TeleOp(name = "ExampleOpMode")
public class ExampleOpMode extends CommandOpMode {

  @Override
  public void initialize() {
    telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    ExampleSubsystem exampleSubsystem = new ExampleSubsystem(hardwareMap, telemetry);
    exampleSubsystem.setDefaultCommand(new ExampleCommand(exampleSubsystem));
  }
}
