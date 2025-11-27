package org.firstinspires.ftc.teamcode.messages;

import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;

// 这是一个用来记录三个里程计轮（Dead Wheel）输入数据的消息类
public final class ThreeDeadWheelInputsMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 第一个平行方向里程计的数据
    public PositionVelocityPair par0;
    // 第二个平行方向里程计的数据
    public PositionVelocityPair par1;
    // 垂直方向里程计的数据
    public PositionVelocityPair perp;

    public ThreeDeadWheelInputsMessage(PositionVelocityPair par0, PositionVelocityPair par1, PositionVelocityPair perp) {
        this.timestamp = System.nanoTime();
        this.par0 = par0;
        this.par1 = par1;
        this.perp = perp;
    }
}