package org.firstinspires.ftc.teamcode.messages;

import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

// 这是一个用来记录两个里程计轮（Dead Wheel）加上陀螺仪输入数据的消息类
public final class TwoDeadWheelInputsMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 平行方向里程计的数据
    public PositionVelocityPair par;
    // 垂直方向里程计的数据
    public PositionVelocityPair perp;
    // 偏航角（水平旋转角度）
    public double yaw;
    // 俯仰角（前后倾斜角度）
    public double pitch;
    // 翻滚角（左右倾斜角度）
    public double roll;
    // X轴旋转速率
    public double xRotationRate;
    // Y轴旋转速率
    public double yRotationRate;
    // Z轴旋转速率
    public double zRotationRate;

    public TwoDeadWheelInputsMessage(PositionVelocityPair par, PositionVelocityPair perp, YawPitchRollAngles angles, AngularVelocity angularVelocity) {
        this.timestamp = System.nanoTime();
        this.par = par;
        this.perp = perp;
        {
            this.yaw = angles.getYaw(AngleUnit.RADIANS);
            this.pitch = angles.getPitch(AngleUnit.RADIANS);
            this.roll = angles.getRoll(AngleUnit.RADIANS);
        }
        {
            this.xRotationRate = angularVelocity.xRotationRate;
            this.yRotationRate = angularVelocity.yRotationRate;
            this.zRotationRate = angularVelocity.zRotationRate;
        }
    }
}
