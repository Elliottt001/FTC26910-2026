package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class ExampleSubsystem extends SubsystemBase {

  // Hardware (motor servo...)
  private Telemetry telemetry;

  public ExampleSubsystem(final HardwareMap hardwareMap, final Telemetry telemetry) {
    this.telemetry = telemetry;
  }

  @Override
  public void periodic() { // FTC 0.001s cycle
    telemetry.addData("Example Data", "Running Now");
    telemetry.update();
  }
}
