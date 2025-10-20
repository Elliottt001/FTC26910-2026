package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import org.firstinspires.ftc.teamcode.subsystems.ExampleSubsystem;

public class ExampleCommand extends CommandBase {
  private final ExampleSubsystem exampleSubsystem;

  public ExampleCommand(ExampleSubsystem subsystem) {
    exampleSubsystem = subsystem;
    addRequirements(exampleSubsystem); // 加command scheduler or no
  }

  @Override // Annotation, 重写super class的函数
  public void initialize() {}

  @Override
  public void execute() { // scheduler periodically calls the function
  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {

    return false;
  }
}
