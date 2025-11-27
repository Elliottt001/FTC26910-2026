package org.firstinspires.ftc.teamcode.messages;

import com.acmerobotics.roadrunner.PoseVelocity2dDual;
import com.acmerobotics.roadrunner.Time;

// message 文件夹中通常不需要修改
// 因为：文件主要用于记录和传输数据（例如发送给 FTC Dashboard 进行调试或绘图）。
// 这是一个用来记录机器人行驶指令的消息类
public final class DriveCommandMessage {
    // 记录当前的时间戳
    public long timestamp;
    // 前进的速度
    public double forwardVelocity;
    // 前进的加速度
    public double forwardAcceleration;
    // 横向移动的速度（平移）
    public double lateralVelocity;
    // 横向移动的加速度
    public double lateralAcceleration;
    // 旋转的速度
    public double angularVelocity;
    // 旋转的加速度
    public double angularAcceleration;

    // 构造函数
    public DriveCommandMessage(PoseVelocity2dDual<Time> poseVelocity) {
        // 记录当前时间
        this.timestamp = System.nanoTime();
        // get(0) 是获取速度，get(1) 是获取加速度
        // 提取前进方向(X轴)的速度和加速度
        this.forwardVelocity = poseVelocity.linearVel.x.get(0);
        this.forwardAcceleration = poseVelocity.linearVel.x.get(1);
        // 提取横向(Y轴)的速度和加速度
        this.lateralVelocity = poseVelocity.linearVel.y.get(0);
        this.lateralAcceleration = poseVelocity.linearVel.y.get(1);
        // 提取旋转(角度)的速度和加速度
        this.angularVelocity = poseVelocity.angVel.get(0);
        this.angularAcceleration = poseVelocity.angVel.get(1);
    }
}
