package org.firstinspires.ftc.teamcode.messages;

// 这是一个用来记录麦克纳姆轮底盘电机指令的消息类
public final class MecanumCommandMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 电池电压
    public double voltage;
    // 左前电机的动力
    public double leftFrontPower;
    // 左后电机的动力
    public double leftBackPower;
    // 右后电机的动力
    public double rightBackPower;
    // 右前电机的动力
    public double rightFrontPower;

    public MecanumCommandMessage(double voltage, double leftFrontPower, double leftBackPower, double rightBackPower, double rightFrontPower) {
        this.timestamp = System.nanoTime();
        this.voltage = voltage;
        this.leftFrontPower = leftFrontPower;
        this.leftBackPower = leftBackPower;
        this.rightBackPower = rightBackPower;
        this.rightFrontPower = rightFrontPower;
    }
}
