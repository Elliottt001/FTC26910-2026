package org.firstinspires.ftc.teamcode.messages;

import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

// 这是一个用来记录麦克纳姆轮定位算法输入数据的消息类
public final class MecanumLocalizerInputsMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 左前轮的位置和速度
    public PositionVelocityPair leftFront;
    // 左后轮的位置和速度
    public PositionVelocityPair leftBack;
    // 右后轮的位置和速度
    public PositionVelocityPair rightBack;
    // 右前轮的位置和速度
    public PositionVelocityPair rightFront;
    // 偏航角（水平旋转角度）
    public double yaw;
    // 俯仰角（前后倾斜角度）
    public double pitch;
    // 翻滚角（左右倾斜角度）
    public double roll;

    public MecanumLocalizerInputsMessage(PositionVelocityPair leftFront, PositionVelocityPair leftBack, PositionVelocityPair rightBack, PositionVelocityPair rightFront, YawPitchRollAngles angles) {
        this.timestamp = System.nanoTime();
        this.leftFront = leftFront;
        this.leftBack = leftBack;
        this.rightBack = rightBack;
        this.rightFront = rightFront;
        {
            this.yaw = angles.getYaw(AngleUnit.RADIANS);
            this.pitch = angles.getPitch(AngleUnit.RADIANS);
            this.roll = angles.getRoll(AngleUnit.RADIANS);
        }
    }
}
