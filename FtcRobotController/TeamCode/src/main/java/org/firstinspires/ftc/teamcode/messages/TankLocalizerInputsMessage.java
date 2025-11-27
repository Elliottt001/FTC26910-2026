package org.firstinspires.ftc.teamcode.messages;

import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;

import java.util.List;

// 这是一个用来记录坦克底盘定位算法输入数据的消息类
public final class TankLocalizerInputsMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 左侧所有轮子的位置和速度
    public PositionVelocityPair[] left;
    // 右侧所有轮子的位置和速度
    public PositionVelocityPair[] right;

    public TankLocalizerInputsMessage(List<PositionVelocityPair> left, List<PositionVelocityPair> right) {
        this.timestamp = System.nanoTime();
        this.left = left.toArray(new PositionVelocityPair[0]);
        this.right = right.toArray(new PositionVelocityPair[0]);
    }
}
