package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.List;

public class MyLimelight extends SubsystemBase {
    // 硬件对象声明
    private final Limelight3A limelight;
    // 存储最新的检测结果（AprilTag 或其他视觉目标）
    private LLResult aprilTagLatestResult;
    private final ElapsedTime timer = new ElapsedTime();
    // 控制是否开启视觉检测的开关
    private boolean llenable = false;

    public MyLimelight(HardwareMap hardwareMap) {
        // 从硬件映射中获取 Limelight 设备，名称为 "limelight"
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        // 设置刷新率为 100Hz（快速更新）
        limelight.setPollRateHz(100); 

    }


    // 写好这些函数，在命令里直接调用：按需使用

    // 切换到蓝色联盟的视觉管道 (Pipeline 7) 并开始传输
    public void initBluePipeline(){
        limelight.pipelineSwitch(7);
        limelight.start();
    }
    
    // 切换到红色联盟的视觉管道 (Pipeline 8) 并开始传输
    public void initRedPipeline(){
        limelight.pipelineSwitch(8);
        limelight.start();
    }
    
    // 切换到图案检测管道 (Pipeline 9) 并开始传输
    public void initPatternPipeline(){
        limelight.pipelineSwitch(9);
        limelight.start();
    }

    // 获取目标的俯仰角 (Pitch)，单位：度：真实世界的物理量
    public double getPitch() {
        if (llenable && hasTarget()) {
            // 获取第一个识别到的 Fiducial (AprilTag) 在相机坐标系下的姿态
            return aprilTagLatestResult.getFiducialResults().get(0)
                    .getTargetPoseCameraSpace().getOrientation().getPitch(AngleUnit.DEGREES);
        }
        return 0;
    }

    // 获取目标在相机坐标系下的 X 轴坐标
    public double getX() {
        if (llenable && hasTarget()) {
            return aprilTagLatestResult.getFiducialResults().get(0)
                    .getTargetPoseCameraSpace().getPosition().x;
        }
        return 0;
    }

    // 获取目标在相机坐标系下的 Y 轴坐标
    public double getY() {
        if (llenable && hasTarget()) {
            return aprilTagLatestResult.getFiducialResults().get(0)
                    .getTargetPoseCameraSpace().getPosition().y;
        }
        return 0;
    }

    // 获取目标在相机坐标系下的 Z 轴坐标（通常代表深度/距离）
    public double getZ() {
        if (llenable && hasTarget()) {
            return aprilTagLatestResult.getFiducialResults().get(0)
                    .getTargetPoseCameraSpace().getPosition().z;
        }
        return 0;
    }

    // 获取水平偏移量 (tx)，用于对准目标
    public double getTx() {
        if (llenable && hasTarget()) {
            return aprilTagLatestResult.getTx();
        }
        return 0;
    }

    // 判断是否已对准目标（水平偏移量小于 0.1 度）
    public boolean isFocused(){
        return abs(getTx()) < 0.1;
    }

    // 计算到目标的直线距离（欧几里得距离）
    public double getDis() {
        // 每个调度周期获取最新的视觉结果
        if (llenable && hasTarget()){
            List<LLResultTypes.FiducialResult> fiducialResults = aprilTagLatestResult.getFiducialResults();
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                    // 使用勾股定理计算三维空间距离：sqrt(x^2 + y^2 + z^2)
                    return sqrt(fr.getTargetPoseCameraSpace().getPosition().y * fr.getTargetPoseCameraSpace().getPosition().y
                            + (fr.getTargetPoseCameraSpace().getPosition().x) * (fr.getTargetPoseCameraSpace().getPosition().x)
                            + (fr.getTargetPoseCameraSpace().getPosition().z) * (fr.getTargetPoseCameraSpace().getPosition().z)
                    );
            }
        }
        return 0;
    }

    // 开启检测
    public void startDetect(){
        llenable = true;
    }

    // 停止检测
    public void stopDetect(){
        llenable = false;
    }

    // 检查是否有有效目标
    // 必须是一个方法而不是变量，因为 Limelight 的数据在不断更新
    public boolean hasTarget() { 
        // 只有当数据不为空且有效时才返回 true
        return aprilTagLatestResult != null && aprilTagLatestResult.isValid(); 
    }

    // 获取原始结果对象
    public LLResult getAprilTagResult() {
        return aprilTagLatestResult;
    }

    // 获取当前识别到的 AprilTag ID
    public int getAprilTagID() {
        return llenable && hasTarget() && !aprilTagLatestResult.getFiducialResults().isEmpty() ?
                aprilTagLatestResult.getFiducialResults().get(0).getFiducialId() : -1;
    }

    // 周期性任务：由 FTCLib 调度器自动调用
    // 核心循环
    @Override
    public void periodic() {
        // 如果开启了检测，则不断获取最新结果
        if (llenable) {
            aprilTagLatestResult = limelight.getLatestResult();
        }
    }
}