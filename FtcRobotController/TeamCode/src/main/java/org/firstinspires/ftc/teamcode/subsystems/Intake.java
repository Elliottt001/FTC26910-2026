package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake extends SubsystemBase {
    private final DcMotor intake, transfer;
    private final Servo swingBar;
    private final DistanceSensor transferBreakBeam;
    public IntakeTransferState intakeCurrentState = IntakeTransferState.Intake_Steady;

    public boolean shooterauto = false;
    public boolean autotrans = false;

    public boolean autoforce = false;
    public Intake(HardwareMap hardwareMap) {      //Constructor,新建对象时需要、
        // 找到对应硬件设备
        intake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        swingBar = hardwareMap.get(Servo.class, "swingBar");
        transferBreakBeam = hardwareMap.get(DistanceSensor.class, "transferBreakBeam");

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        transfer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // 物理安装方向与代码逻辑方向不一致。
        // 实际需求：你的进气装置（Intake）可能通过齿轮、链条传动，或者电机本身就是反着装的（轴朝内或朝外）。
        // 结果：当你给正功率想让它“吸入”时，它实际上在往外“吐”。
        // 解决：为了让代码逻辑符合直觉（即 setPower(1) = 吸入），我们需要在初始化时把电机方向设为 REVERSE。这样代码里的“正”就对应了物理上的“吸入”。
        // 它确保了当你调用 intake.setPower(1) 时，机器人是真的在吸入物体，而不是把它吐出去。
        // 这是为了让写逻辑代码的人更舒服，不需要每次写代码时都去想“哦，这个电机是反的，我要给负数如 intake.setPower(-1) 才能吸进来”。
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        // autoforce 强制手动覆盖开关
        // 它用于在“自动射击模式”下，强行夺回手动控制权。
        autoforce = false;
        autotrans = false;
        shooterauto = false;
    }

    // getter and setter 方法
    public void setSwingBarPos(double pos) {
        swingBar.setPosition(pos);
    }

    public double getDist(){
        return transferBreakBeam.getDistance(DistanceUnit.MM);
    }

    public void setIntakePower(double power) {

        intake.setPower(power);

    }

    public void setTransferPower(double power) {
        transfer.setPower(power);
    }

    // 状态定义：intakepower / transferpower
    //  Java 枚举（Enum）的一个高级用法：枚举不仅可以是简单的常量列表，
    // 还可以拥有自己的属性（变量）、构造函数和方法。
    public enum IntakeTransferState {
        // 1. 定义枚举常量（实际上是在调用构造函数）
        Suck_In(1, 0),       // 相当于：new IntakeTransferState(1, 0)
        Split_Out(-0.7, -1), // 相当于：new IntakeTransferState(-0.7, -1)
        Send_It_Up(1, 1),    // ...
        Intake_Steady(0, 0); // 注意这里的封号 ; 表示常量列表结束

        // 后面的这些：是为了支撑前面的常量定义
        
        // 2. 定义成员变量（属性）
        private final double intakePower;   // 用来存第一个参数
        private final double transferPower; // 用来存第二个参数

        // 3. 定义构造函数（Constructor）
        // 这个构造函数是私有的，专门给上面那四个常量用的
        IntakeTransferState(double InPower, double TrPower) {
            this.intakePower = InPower;
            this.transferPower = TrPower;
        }
    }


    // 设置进气系统的目标状态：按下手柄时候及时响应
    public void setIntakeState(IntakeTransferState intakeTransferState) {
        intakeCurrentState = intakeTransferState;

        // 情况 A：手动模式
        if(!shooterauto || autoforce) {
            intake.setPower(intakeCurrentState.intakePower);
            transfer.setPower(intakeCurrentState.transferPower);
        }
        // 情况 B：自动射击模式
        else{
            if(autotrans){
                intakeCurrentState = IntakeTransferState.Send_It_Up;
                // 如果飞轮准备好了 (autotrans = true) -> 强制切换到 Send_It_Up（输送模式），把球送上去。
            }
            else{
                intakeCurrentState = IntakeTransferState.Intake_Steady;
                // 如果飞轮没准备好 (autotrans = false) -> 强制切换到 Intake_Steady（静止模式），等待飞轮加速。
            }
        }

    }

    // 自动射击模式判断
    public void updateAutoshoot(boolean auto){
        shooterauto = auto;
    }

    // 飞轮转速是否达标/逆否输送弹药
    public void updateautotranse(boolean auto){
        autotrans = auto;
    }

    // 周期性任务：每时每刻持续调用
    @Override
    public void periodic() { // FTC 0.001s cycle
        if(!shooterauto || autoforce) {
            intake.setPower(intakeCurrentState.intakePower);
            transfer.setPower(intakeCurrentState.transferPower);
        }
        else{
            if(autotrans){
                intakeCurrentState = IntakeTransferState.Send_It_Up;
            }
            else{
                intakeCurrentState = IntakeTransferState.Intake_Steady;
            }
            intake.setPower(intakeCurrentState.intakePower);
            transfer.setPower(intakeCurrentState.transferPower);
        }
    }
}

/*
实现了以下三个功能：

1. 硬件管理与初始化 (Hardware Management)
认领硬件：在构造函数中，它通过 hardwareMap 获取了两个电机（intake, transfer）、一个舵机（swingBar）和一个距离传感器（transferBreakBeam）的控制权。
配置参数：设置了电机的零功率行为（FLOAT 浮动 / BRAKE 刹车）和旋转方向（REVERSE），确保硬件按预期工作。
2. 动作控制 (Action Control)
进气与输送：通过 IntakeTransferState 枚举，定义了四种标准工作模式：
吸入 (Suck_In)：只吸不送。
吐出 (Split_Out)：全线反转吐球。
输送 (Send_It_Up)：吸进来并送上去（给飞轮）。
静止 (Intake_Steady)：全停。
摇臂控制：通过 setSwingBarPos 控制进气口的抬起和放下。
传感器读取：通过 getDist 读取距离传感器数值，用于检测球是否到位（虽然在这个文件里没看到具体的逻辑使用，但提供了接口）。
3. 智能协调与安全互锁 (Coordination & Interlock)
实现了手动控制与自动射击的无缝切换：
模式切换：通过 updateAutoshoot 接收外部指令，在“手动模式”和“自动射击模式”之间切换。
自动配合：在自动射击模式下，它会忽略手柄指令，完全听从 updateautotranse（飞轮状态）的指挥。
飞轮没转好 -> 强制停止输送（防止卡弹）。
飞轮转好了 -> 自动开始输送。
4. 持续监控：利用 periodic 周期性函数，确保即使在没有按键操作的情况下，进气系统也能实时响应飞轮状态的变化（例如飞轮刚加速到位，进气就立刻开始送球）。

*/