package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import static java.lang.Math.abs;

import android.health.connect.datatypes.units.Power;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Config
public class Shooter extends SubsystemBase {
    private final DcMotorEx shooterLeft;
    private final DcMotorEx shooterRight;
    private final PIDController pidController;

    // 可调 PID 参数 - 可以通过 FTC Dashboard 实时调整
    public static double Kp = 27;  // 比例增益 (Proportional)
    public static double Ki = 0.01; // 积分增益 (Integral)
    public static double Kd = -10;    // 微分增益 (Derivative)
    public static double pidThreshold = 1000.0; // PID 控制生效的 RPM 阈值（低于此差值用 PID，高于此差值全速）
    public static double tolerance = 0.3; // 判断“到达目标”的容差范围

    public static double aimRPM = 0;

    // 飞轮的目标转速 (RPM)
    private double targetRPM = 0.0;

    public double distance = 0;

    public boolean idelOn = false;

    public boolean focused = false;

    public boolean automode = false;

    public boolean autoLonger = true;

    public double PIDoutput;

    public static double RPMThresh = 110;

    public static double Autoshort = 3040;
    public static double Autolong = 3390;


    public enum ShooterStatus {
        Stop,Idling,Shooting
    }



    public ShooterStatus shooterStatus = ShooterStatus.Stop;



    public Shooter(HardwareMap hardwareMap) {
        shooterLeft = hardwareMap.get(DcMotorEx.class, "shooterLeft");
        shooterRight = hardwareMap.get(DcMotorEx.class, "shooterRight");

        // 初始化 PID 控制器
        pidController = new PIDController(Kp, Ki, Kd);

        // 配置电机
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // 设置电机方向：一正一反，确保两个飞轮向同一个方向（向外）旋转发射物体
        shooterLeft.setDirection(DcMotor.Direction.FORWARD);
        shooterRight.setDirection(DcMotor.Direction.REVERSE);

        // 配置电机模式 - 只有 shooterLeft 接了编码器线
        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);  // 有编码器
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER); // 无编码器（跟随）

        // 设置 PID 容差
        pidController.setTolerance(tolerance);

        focused = false;

        automode = false;

        autoLonger = true;
    }

    /**
     * 获取当前飞轮角速度 (rad/s)
     * 使用 shooterLeft (带编码器的电机) 作为反馈源
     */
    public void updateFocused(boolean focus){
        focused = focus;
    }
    public void setShooterStatus(ShooterStatus status){
        shooterStatus = status;
    }
    public double getFlyWheelVelocity() {
        return shooterLeft.getVelocity() * (2.0 * Math.PI) / 60.0; // 将 RPM 转换为 rad/s

    }

    public void updateDis(double dis){
        distance = dis;
    }
    /**
     * 获取当前飞轮转速 (RPM)
     * 使用 shooterLeft (带编码器的电机) 作为反馈源
     * shooterRight 在开环模式下运行 (无编码器)
     */
    public double getFlyWheelRPM() {
        // shooterLeft 有编码器，所以我们用它的速度代表整个飞轮系统的速度
        // (理论上两个电机应该转速一致)
        // getVelocity() 返回的是每秒编码器脉冲数 (ticks/sec)，需要转换为 RPM
        return shooterLeft.getVelocity() * 60.0 / 28.0; // 28 ticks per revolution (GoBilda 6000RPM motor?)
    }
    public void setTargetRPM(double targetRPM) {
        this.targetRPM = targetRPM;
        pidController.setSetPoint(0);


    }
    public double getTargetRPM() {
        return targetRPM;
    }
    
    // 判断是否达到目标转速
    public boolean isAtTargetRPM() {
        // 条件：(目标 < 当前+阈值) 且 (目标 > 当前-10) 且 (当前 > 2600) 且 (已对准或自动模式)
        return (getTargetRPM() < getFlyWheelRPM() + RPMThresh && getTargetRPM() > getFlyWheelRPM()-10)&&getFlyWheelRPM()>2600&&(focused||automode);
    }

    // 存储当前电机功率用于遥测/绘图
    private double currentMotorPower = 0.0;
    private double currentPIDOutput = 0.0;

    /**
     * 更新 PID 控制器并设置电机功率
     * 在主循环中持续调用此方法以实现闭环控制
     */
    public void settoShooting(){
        shooterStatus = ShooterStatus.Shooting;
    }
    public void settoStop(){
        shooterStatus = ShooterStatus.Stop;
    }


    public void settoIdle(){
        shooterStatus = ShooterStatus.Idling;
    }
    public void updateFlywheelPID() {
        // 使用电机内置的 PIDF 控制器 (RunMode.RUN_USING_ENCODER)
        // 注意：这里直接设置了内置控制器的系数，而不是使用上面的自定义 pidController 计算结果
        shooterLeft.setVelocityPIDFCoefficients(Kp,Ki,Kd,0);
        shooterRight.setVelocityPIDFCoefficients(Kp,Ki,Kd,0);

        // 将目标 RPM 转换为 ticks/sec 并设置给电机
        shooterLeft.setVelocity(targetRPM*28/60);
        shooterRight.setVelocity(targetRPM*28/60);
//        if (targetRPM > 0) {
//            // Update PID parameters and tolerance in case they were changed via dashboard
//            pidController.setPID(Kp, Ki, Kd);
//            pidController.setTolerance(tolerance);
//
//            double currentRPM = getFlyWheelRPM();
//            double rpmDifference = currentRPM - targetRPM;
//
//            double pidinput = rpmDifference/100.0;
//            double power;
//            double pidOutput = 0.0;
//
//            if (abs(rpmDifference) <= pidThreshold) {
//                // Use PID control for fine-tuning within ±pidThreshold RPM
//                pidOutput = pidController.calculate(pidinput)+0.5;
//                power = Math.max(0.0, Math.min(1.0, pidOutput)); //smart brahhh
//            } else if (rpmDifference < pidThreshold) {
//                // Large speed increase needed - use full power
//                power = 1.0;
//                pidOutput = 1.0; // PID would output 1.0 but we're overriding
//            } else {
//                // Large speed decrease needed - use no power (let inertia slow it down)
//                power = 0.0;
//                pidOutput = 0.0; // PID would output negative but we're overriding
//            }
//            PIDoutput = power;
//            // Store values for telemetry/graphing
//            currentMotorPower = power;
//            currentPIDOutput = pidOutput;
//
//            // Apply power to both motors
//            shooterLeft.setPower(power);
//            shooterRight.setPower(power);
//        } else {
//            // Stop motors if no target set
//            currentMotorPower = 0.0;
//            currentPIDOutput = 0.0;
//            shooterLeft.setPower(0);
//            shooterRight.setPower(0);
//        }
    }

    /**
     * 直接设置飞轮功率 (绕过 PID)
     */
    public void setFlywheelPower(double power) {
        shooterLeft.setPower(power);
        shooterRight.setPower(power);
        // 手动设置功率时重置目标 RPM
        targetRPM = 0;
    }

    public void completeStop() {
        setTargetRPM(0);
        setFlywheelPower(0);

        pidController.reset();
    }

    public void toggleRPM() {
        setTargetRPM(aimRPM);
        shooterStatus = ShooterStatus.Shooting;

    }

    // 根据距离自动计算目标转速
    public void updateAim() {
        distance = abs(distance);
        // 远距离
        if (distance > 3.25){
            setTargetRPM(3650);
        }
        // 近距离
        else if (distance < 1.4){
            setTargetRPM(100*distance+2750); // 线性插值
        }
        // 中距离
        else{
            setTargetRPM(200*distance+2750); // 线性插值
        }

        // 极近距离 (几乎贴脸)
        if (distance < 0.01){
            setTargetRPM(3600);
        }

        // 自动模式下的固定转速覆盖
        if(automode&&autoLonger){
            setTargetRPM(Autolong);
        }
        else if(automode&&!autoLonger){
            setTargetRPM(Autoshort);
        }
    }




    /**
     * Get current motor power (for graphing/telemetry)
     */
    public double getCurrentMotorPower() {
        return currentMotorPower;
    }

    /**
     * Get current PID output (for graphing/telemetry)
     */
    public double getCurrentPIDOutput() {
        return currentPIDOutput;
    }
    
    // 周期性任务：状态机逻辑
    @Override
    public void periodic(){
        updateFlywheelPID(); // 持续更新 PID 控制
        
        if(shooterStatus == ShooterStatus.Shooting){
            updateAim(); // 射击模式：根据距离计算转速
        }
        else if(shooterStatus == ShooterStatus.Stop){
            completeStop(); // 停止模式：切断电源
        }
        else if(shooterStatus == ShooterStatus.Idling) {
            setTargetRPM(2200); // 怠速模式：保持低速旋转
        }
    }
    public void updateTelemetry() {
        telemetry.addData("Target RPM", targetRPM);
        telemetry.addData("Current RPM", getFlyWheelRPM());
        telemetry.addData("At Target", isAtTargetRPM());
        telemetry.addData("Motor Power", currentMotorPower);
        telemetry.addData("PID Output", PIDoutput);
        telemetry.addData("Kp", Kp);
        telemetry.addData("Ki", Ki);
        telemetry.addData("Kd", Kd);
        telemetry.addData("PID Threshold", pidThreshold);
        telemetry.addData("Tolerance", tolerance);
    }
}
