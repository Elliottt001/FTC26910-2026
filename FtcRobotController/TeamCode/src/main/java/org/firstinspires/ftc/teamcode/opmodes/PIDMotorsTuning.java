package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

/**
 * Clean PID Tuning OpMode for Shooter
 * Dashboard-only: Manual parameter entry + Real-time graphing
 * No gamepad controls - just pure PID tuning
 */

// 是用于调试和优化机器人发射器（Shooter）性能的工具程序，而不是比赛时用的主程序。
/*
在以下几种情况使用这个程序：
- 新车刚装好：当你第一次组装好发射器，需要让它转起来，并且转得准的时候。
- 发射不稳定：如果你发现发射出去的物体忽远忽近，或者电机声音忽大忽小，说明转速控制不稳，需要重新调参。
- 更换了硬件：如果你换了不同型号的电机、换了更重的飞轮，或者改变了齿轮比，之前的参数就不适用了，必须重新运行这个程序来寻找新的最佳参数。
- 电池电压影响测试：有时候你想测试在不同电量下，PID 能否保持转速稳定。
*/

@Config
@Disabled
@TeleOp(name = "Shooter PID Tuning", group = "Tuning")
public class PIDMotorsTuning extends OpMode {
    
    // Dashboard tunable parameters - automatically detected by @Config on class
    public static double Kp = 0.001;
    // $K_p$ 是比例增益 (Proportional Gain)
    
    public static double Ki = 0.0001;
    // $K_i$ 是积分增益 (Integral Gain)
    
    public static double Kd = 0.0;
    // $K_d$ 是微分增益 (Derivative Gain)
    
    public static double pidThreshold = 200.0;
    // PID 控制的误差阈值/临界点 (Threshold)
    
    public static double tolerance = 50.0;
    // 允许的误差范围 (Tolerance)
    
    public static double targetRPM = 2000.0;
    // 目标转速 (Target RPM)
    
    private Shooter shooter;
    private Telemetry dashboardTelemetry;
    private FtcDashboard dashboard;
    
    @Override
    public void init() {
        shooter = new Shooter(hardwareMap);
        dashboard = FtcDashboard.getInstance();
        dashboardTelemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        
        // Update PID parameters
        Shooter.Kp = Kp;
        Shooter.Ki = Ki;
        Shooter.Kd = Kd;
        Shooter.pidThreshold = pidThreshold;
        Shooter.tolerance = tolerance;
        
        dashboardTelemetry.addData("Status", "Clean PID Tuning Ready - Use Dashboard to adjust parameters");
        dashboardTelemetry.update();
    }

    /*
    实时调整 PID 参数：它允许你在机器人运行时，通过电脑上的 FTC Dashboard 网页界面，实时修改 Kp（比例）、Ki（积分）、Kd（微分）这三个关键参数，而不需要每次改完代码都重新编译下载。
    数据可视化：它会将目标转速（Target RPM）和实际转速（Current RPM）实时发送到电脑上画成曲线图。你可以直观地看到电机是否稳定、响应是否够快。
    测试电机性能：它不依赖手柄控制，而是直接设定一个目标转速（targetRPM），让你专注于观察电机如何响应这个指令。
    */
    
    @Override
    public void loop() {
        // Update PID parameters from dashboard
        Shooter.Kp = Kp;
        Shooter.Ki = Ki;
        Shooter.Kd = Kd;
        Shooter.pidThreshold = pidThreshold;
        Shooter.tolerance = tolerance;
        
        // Set target and update PID
        shooter.setTargetRPM(targetRPM);
        shooter.updateFlywheelPID();
        
        // Create telemetry packet for graphing
        TelemetryPacket packet = new TelemetryPacket();
        
        // 给电脑网页（FTC Dashboard）画曲线图(Dashboard Graph)：为了看趋势（波形）。

        // Add data for graphing
        packet.put("Target RPM", targetRPM);
        packet.put("Current RPM", shooter.getFlyWheelRPM());
        packet.put("RPM Error", targetRPM - shooter.getFlyWheelRPM());
        packet.put("Motor Power", shooter.getCurrentMotorPower());
        packet.put("PID Output", shooter.getCurrentPIDOutput());
        
        // Add PID parameters
        packet.put("Kp", Kp);
        packet.put("Ki", Ki);
        packet.put("Kd", Kd);
        packet.put("Threshold", pidThreshold);
        packet.put("Tolerance", tolerance);
        packet.put("At Target", shooter.isAtTargetRPM() ? "YES" : "NO");
        
        // Send packet to dashboard for graphing
        dashboard.sendTelemetryPacket(packet);

        // 第二部分：为了给手机屏幕（Driver Station）看数字 (Dashboard Text)
        
        // Also update regular telemetry (minimal, clean display)
        dashboardTelemetry.addData("Target RPM", "%.1f", targetRPM);
        dashboardTelemetry.addData("Current RPM", "%.1f", shooter.getFlyWheelRPM());
        dashboardTelemetry.addData("RPM Error", "%.1f", targetRPM - shooter.getFlyWheelRPM());
        dashboardTelemetry.addData("Motor Power", "%.3f", shooter.getCurrentMotorPower());
        dashboardTelemetry.addData("PID Output", "%.3f", shooter.getCurrentPIDOutput());
        dashboardTelemetry.addData("At Target", shooter.isAtTargetRPM() ? "YES" : "NO");
        dashboardTelemetry.addData("", "");
        dashboardTelemetry.addData("Kp", "%.6f", Kp);
        dashboardTelemetry.addData("Ki", "%.6f", Ki);
        dashboardTelemetry.addData("Kd", "%.6f", Kd);
        dashboardTelemetry.addData("Threshold", "%.1f", pidThreshold);
        dashboardTelemetry.addData("Tolerance", "%.1f", tolerance);
        dashboardTelemetry.update();
    }
    
    @Override
    public void stop() {
        shooter.completeStop();
        dashboardTelemetry.addData("Status", "Stopped");
        dashboardTelemetry.update();
    }
}
/*

操作方式：

调试阶段（不用改代码）：
1. 连接电脑和机器人，打开 FTC Dashboard 网页界面。
2. 启动这个 PID Tuning OpMode。
3. 在网页上看到 Kp、Ki、Kd、Threshold、Tolerance 这些参数的输入框。
4. 输入初始参数值（可以参考默认值），然后观察图线变化。
5. 根据图线形态，调整 Kp、Ki、Kd 参数：
   - 如果响应慢，尝试增大 Kp。
   - 如果有持续偏差，尝试增大 Ki。
   - 如果震荡过大，尝试增大 Kd。
6. 反复调整，直到图线稳定且响应迅速为止。

比赛阶段（参数已调好）：
1. 在比赛前，将调好的 Kp、Ki、Kd、Threshold、Tolerance 参数写入代码中，编译并下载到机器人。
2. 启动比赛时的主程序，发射器将使用这些优化过的 PID 参数进行控制。
*/