package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import static com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

import java.util.Objects;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Pinpoint Odometry Example", group = "Examples")
public class PinpointOdometryExample extends LinearOpMode {

    // Declare hardware
    public PinpointLocalizer pin;
    @Override
    public void runOpMode() {
        // Initialize hardware
        pin = new PinpointLocalizer(hardwareMap,71.0 / 50781, new Pose2d(0, 0, 0));


        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Read encoder values
            pin.update();

            // Display telemetry
            telemetry.addData("x:", pin.getPose().position.x);
            telemetry.addData("y:", pin.getPose().position.y);
            telemetry.addData("heading:", pin.getPose().heading);

            telemetry.update();
        }
    }
}