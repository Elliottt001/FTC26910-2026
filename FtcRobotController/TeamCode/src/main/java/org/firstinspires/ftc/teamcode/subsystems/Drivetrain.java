package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drivetrain extends SubsystemBase {
    //declare motors.. 声明，赋值...

    // 4 个麦克纳姆轮电机：底盘/动力传动系统，也就是机器人的“腿”
    private final DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

    //servos

    public Drivetrain(HardwareMap hardwareMap) {      //Constructor,新建对象时需要
        // .get() 方法从硬件映射中获取具体的硬件设备
        // 即“认亲”——把代码里写的变量（软件对象）和机器人上实际插着的硬件（物理设备）对应起来。
        // parma0: “你要找什么类型的设备？”即类的类型 (Class Type)。parma1: “它在配置表里叫什么名字？”即设备在配置文件中的名字 (String Name)。
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRight");

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // 将左侧两个电机（前左、后左）的旋转方向设置为“反向” (REVERSE)。
        // 因为镜像对称，左侧电机需要反向旋转以确保机器人直线行驶时所有轮子朝同一方向转动。
        // 左边的电机可能需要顺时针转，右边的需要逆时针转。
        // 这样你就可以给两个电机都发送 1.0 的功率
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void teleDrive (double frontBackVelocity, double strafeVelocity, double turnVelocity){
        // y 代表前后方向的速度
        double y = frontBackVelocity;
        // x 代表左右方向的速度
        double x = strafeVelocity;
        // rx 代表旋转速度
        double rx = turnVelocity;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]

        // 麦克纳姆轮运动学解算
        // 麦克纳姆轮的滚轮：https://www.bilibili.com/video/BV19W411h7if/?spm_id_from=333.337.search-card.all.click&vd_source=b14909f255fe42946743657320d2f59a
        // 除以这个分母原因：保证电机的功率范围只能是 -1.0 到 1.0。
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);
    }
    // Drivetrain 的 getter 方法 --> 转到 tutorial/JavaBasic.md
    public double getFrontLeftPower() {
        return frontLeftMotor.getPower();
    }

    public double getFrontRightPower() {
        return frontRightMotor.getPower();
    }

    public double getBackLeftPower() {
        return backLeftMotor.getPower();
    }

    public double getBackRightPower() {
        return backRightMotor.getPower();
    }


}

