package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

// 用于配置 Pedro Pathing 路径跟随库的常量
// 据你机器人的实际物理结构和测试结果来调整这里的数值

public class    Constants {
        // 跟随器常量
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(11.337)
            .forwardZeroPowerAcceleration(-26.465452204903155)
            .lateralZeroPowerAcceleration(-67.6584184654734)
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryHeadingPIDF(true)
            .useSecondaryDrivePIDF(true)
            .centripetalScaling(0.00058)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0.00008, 0.025, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(0.9, 0.0001, 0.1, 0.09))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.01, 0, 0.0005, 0.6, 0.1))
            .secondaryTranslationalPIDFCoefficients(
                    new PIDFCoefficients(0.1, 0.0001, 0.012, 0   )
            )
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(2, 0, 0.1, 0))
            .secondaryDrivePIDFCoefficients(
                    new FilteredPIDFCoefficients(0.02, 0, 0, 0.6, 0)
            );

            // 底盘常量
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .leftFrontMotorName("frontLeft")
            .leftRearMotorName("backLeft")
            .rightFrontMotorName("frontRight")
            .rightRearMotorName("backRight")
            .useBrakeModeInTeleOp(true)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(78.28555033526084)
            .yVelocity(58.00087083230808)
            .nominalVoltage(13.2)
            .useVoltageCompensation(true);

            // 定位器常量
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-84)
            .strafePodX(-168)
            .distanceUnit(DistanceUnit.MM)
            .hardwareMapName("pinpoint")
            .yawScalar(1.0)
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
//            .customEncoderResolution(13.26291192)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

            // 路径约束
    public static PathConstraints pathConstraints = new PathConstraints(
            0.997,
            50,
            1.125,
            1
    );

    // 辅助方法，用于根据上述配置和硬件映射（HardwareMap）直接构建并返回一个可用的 Follower 对象，简化了初始化过程
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}
