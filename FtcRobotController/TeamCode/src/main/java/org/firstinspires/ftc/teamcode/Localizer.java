package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;

/**
 * Interface for localization methods.
 */

/*
这是一个 Java 接口 (Interface)，规定了任何一个定位器都必须提供以下三个功能（方法）：

setPose(Pose2d pose):
功能: 强制设置机器人的当前位置。
场景: 比如比赛刚开始时，你需要告诉机器人它在场地的哪个角落；或者在比赛中途，你用摄像头看到了 AprilTag，确认了精确位置，就可以用这个方法修正累积误差。

getPose():
功能: 获取机器人当前估算的位置（x, y 坐标和车头朝向）。
注意: 它只返回最后一次计算的结果，不会触发新的测量。

update():
功能: 命令定位器去读取传感器数据，计算最新的位置，并返回当前的速度。
场景: 这个方法需要在主循环中不断被调用（每秒几十次），这样机器人的位置信息才是实时的。
*/

public interface Localizer {
    void setPose(Pose2d pose);

    /**
     * Returns the current pose estimate.
     * NOTE: Does not update the pose estimate;
     * you must call update() to update the pose estimate.
     * @return the Localizer's current pose
     */
    Pose2d getPose();

    /**
     * Updates the Localizer's pose estimate.
     * @return the Localizer's current velocity estimate
     */
    PoseVelocity2d update();
}
