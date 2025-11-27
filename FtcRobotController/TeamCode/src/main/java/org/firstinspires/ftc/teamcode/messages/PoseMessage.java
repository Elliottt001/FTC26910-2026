package org.firstinspires.ftc.teamcode.messages;

import com.acmerobotics.roadrunner.Pose2d;

// 这是一个用来记录机器人姿态（位置和朝向）的消息类
public final class PoseMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 机器人的X坐标
    public double x;
    // 机器人的Y坐标
    public double y;
    // 机器人的朝向角度
    public double heading;

    public PoseMessage(Pose2d pose) {
        this.timestamp = System.nanoTime();
        this.x = pose.position.x;
        this.y = pose.position.y;
        this.heading = pose.heading.toDouble();
    }
}

