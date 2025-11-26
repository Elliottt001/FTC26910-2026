package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;


// 在电脑端的 FTC Dashboard 网页上“画”出你的机器人，帮助你调试和观察机器人的位置。
// 接收一个画布 (Canvas) 和机器人的位姿 (Pose2d)。
// 画圆: c.strokeCircle(...) 画一个半径为 9 英寸（ROBOT_RADIUS = 9）的圆，代表机器人的身体。
// 画线: c.strokeLine(...) 从圆心向外画一条线，代表机器人的车头朝向。

/*
调用：MecanumDrive.java 中
Drawing.drawRobot(c, txWorldTarget.value()); // 画出目标位置（绿色）
Drawing.drawRobot(c, localizer.getPose());   // 画出实际位置（蓝色）
*/
public final class Drawing {
    private Drawing() {}


    public static void drawRobot(Canvas c, Pose2d t) {
        final double ROBOT_RADIUS = 9;

        c.setStrokeWidth(1);
        c.strokeCircle(t.position.x, t.position.y, ROBOT_RADIUS);

        Vector2d halfv = t.heading.vec().times(0.5 * ROBOT_RADIUS);
        Vector2d p1 = t.position.plus(halfv);
        Vector2d p2 = p1.plus(halfv);
        c.strokeLine(p1.x, p1.y, p2.x, p2.y);
    }
}
