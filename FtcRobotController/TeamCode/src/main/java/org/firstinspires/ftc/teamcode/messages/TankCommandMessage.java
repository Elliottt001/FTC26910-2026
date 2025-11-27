package org.firstinspires.ftc.teamcode.messages;

// 这是一个用来记录坦克底盘（两边驱动）电机指令的消息类
public final class TankCommandMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 电池电压
    public double voltage;
    // 左侧电机的动力
    public double leftPower;
    // 右侧电机的动力
    public double rightPower;

    public TankCommandMessage(double voltage, double leftPower, double rightPower) {
        this.timestamp = System.nanoTime();
        this.voltage = voltage;
        this.leftPower = leftPower;
        this.rightPower = rightPower;
    }
}
