package org.firstinspires.ftc.teamcode.opmodes; // make sure this aligns with class location

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.MyLimelight;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Scheduler;

@Autonomous(name = "BLUE_Far_12ball")
public class BLUE_Far_12ball extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer, timer;
    //private final ElapsedTime timer  = new ElapsedTime();

    // pose函数参数的数字需要和比赛场地/硬件对齐
    // pose函数定义在 com.pedropathing.geometry.Pose 库：（x坐标，y坐标，朝向角度（弧度））
    private int pathState =0;
    // final 常量
    private final Pose startPose = new Pose(0, 0, 0); // Start Pose of our robot.
    private final Pose ShootPose1 = new Pose(9.4033, -1.4877, 0.406764);

    private final Pose ShootPose2 = new Pose(71.9996,1.5216,0.7225);
    private final Pose PrepGather1 = new Pose(24.7910, 17.19286, 1.590508);

    private final Pose FinishGather1 = new Pose(24.7910, 37.2588, 1.590508);

    private final Pose PrepGather2 = new Pose(48.849, 17.19286, 1.590508);

    private final Pose FinishGather2 = new Pose(48.849, 37.2588, 1.590508);

    private final Pose PrepGather3 = new Pose(72.907, 17.19286, 1.590508);

    private final Pose FinishGather3 = new Pose(72.907, 37.2588, 1.590508);

    private final Pose endPose = new Pose(4.64556,44.66559,1.5623);

    private boolean firstshooting = false;
    private PathChain Shootpath1, Shootpath2, Shootpath3,Shootpath4, lastOutPath;
    private PathChain prepGatherPath1, prepGatherPath2, prepGatherPath3;

    public Intake intake;
    public Shooter shooter;
    public MyLimelight limelight;
    public Scheduler scheduler;

    public void buildPaths() {
        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */

        /* This is our grabPickup1 PathChain. We are using a single path with a BezierLine, which is a straight line. */

        // shootpose: 射击位置
        // prepGather: 准备拾取位置
        // finishGather: 完成拾取位置
        Shootpath1 = follower.pathBuilder()
                .addPath(new BezierLine(startPose, ShootPose1))
                .setLinearHeadingInterpolation(startPose.getHeading(), ShootPose1.getHeading())
                .build();

        prepGatherPath1 = follower.pathBuilder()  // 初始化一个路径构建器
                // 向路径链中添加一段直线路径（贝塞尔直线）
                .addPath(new BezierLine(ShootPose1, PrepGather1))
                // 设置这段路径上的角度（朝向）变化方式：线性插值（Linear Interpolation）。均匀地、平滑地从开始角度转到结束角度
                .setLinearHeadingInterpolation(ShootPose1.getHeading(), PrepGather1.getHeading())
                .addPath(new BezierLine(PrepGather1, FinishGather1))
                .setLinearHeadingInterpolation(PrepGather1.getHeading(), FinishGather1.getHeading())
                // 完成构建，生成最终的 PathChain 对象。
                .build();



        Shootpath2 = follower.pathBuilder()
                .addPath(new BezierLine(FinishGather1, ShootPose1))
                .setLinearHeadingInterpolation(FinishGather1.getHeading(), ShootPose1.getHeading())
                .build();

        prepGatherPath2 = follower.pathBuilder()
                .addPath(new BezierLine(ShootPose1, PrepGather2))
                .setLinearHeadingInterpolation(ShootPose1.getHeading(), PrepGather2.getHeading())
                .addPath(new BezierLine(PrepGather2, FinishGather2))
                .setLinearHeadingInterpolation(PrepGather2.getHeading(), FinishGather2.getHeading())
                .build();

        Shootpath3 = follower.pathBuilder()
                .addPath(new BezierLine(FinishGather2, PrepGather2))
                .setLinearHeadingInterpolation(FinishGather2.getHeading(), PrepGather2.getHeading())
                .addPath(new BezierLine(PrepGather2, ShootPose2))
                .setLinearHeadingInterpolation(PrepGather2.getHeading(), ShootPose2.getHeading())
                .build();

        prepGatherPath3 = follower.pathBuilder()
                .addPath(new BezierLine(ShootPose2, PrepGather3))
                .setLinearHeadingInterpolation(ShootPose2.getHeading(), PrepGather3.getHeading())
                .addPath(new BezierLine(PrepGather3, FinishGather3))
                .setLinearHeadingInterpolation(PrepGather3.getHeading(), FinishGather3.getHeading())
                .build();

        Shootpath4 = follower.pathBuilder()
                .addPath(new BezierLine(FinishGather3, PrepGather3))
                .setLinearHeadingInterpolation(FinishGather3.getHeading(), PrepGather3.getHeading())
                .addPath(new BezierLine(PrepGather3, ShootPose2))
                .setLinearHeadingInterpolation(PrepGather3.getHeading(), ShootPose2.getHeading())
                .build();

        lastOutPath = follower.pathBuilder()
                .addPath(new BezierLine(ShootPose2, endPose))
                .setLinearHeadingInterpolation(ShootPose2.getHeading(), endPose.getHeading())
                .build();

    }

    // auto阶段的核心逻辑（但不是最核心），用状态机（State Machine）来分步骤控制机器人的行为。
    // 好简朴的case结构（（（
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                shooter.autoLonger = true; // 开启远距离射击模式（可能是调整飞轮速度或角度）
                shooter.setShooterStatus(Shooter.ShooterStatus.Idling); // 射击机构进入怠速状态（预热飞轮）
                follower.followPath(Shootpath1); // 命令底盘开始沿 Shootpath1 路径移动（从起点去射击点1）
                setPathState(1); // 切换到状态 1，等待移动完成
                break;
            case 1:
                // 1. 检查底盘状态
                if(!follower.isBusy()) { 
                    
                    // 2. 判断是否是第一次进入射击逻辑
                    if (!firstshooting) { 
                        
                        // 3. 开启自动瞄准
                        shooter.updateFocused(true); 
                        
                        // 4. 设置射击状态为“正在射击”
                        shooter.setShooterStatus(Shooter.ShooterStatus.Shooting); 
                        
                        // 5. 重置计时器
                        timer.resetTimer(); 

                        // 6. 标记“已经开始射击了”
                        firstshooting = true; 
                    }
                    else{ 
                        // 7. 检查射击时间是否超过 4.2 秒
                        if(timer.getElapsedTimeSeconds()> 4.2){
                            
                            // 8. 停止射击
                            shooter.setShooterStatus(Shooter.ShooterStatus.Stop); 
                            
                            // 9. 放下进气口摇臂
                            intake.setSwingBarPos(0.4); 
                            
                            // 10. 开启吸球模式
                            intake.setIntakeState(Intake.IntakeTransferState.Suck_In); 
                            
                            // 11. 切换到下一个状态 (Case 2)
                            setPathState(2); 
                        }
                        
                        // 12. 在射击的中后段 (2.3秒 - 4.2秒) 调整摇臂
                        if(timer.getElapsedTimeSeconds()>2.3 && timer.getElapsedTimeSeconds()<4.2){
                            intake.setSwingBarPos(0); 
                        }

                    }
                    break;
                }

                /*
                1. 等待：直到跑到位置。
                2. 启动：一到位，立马开启瞄准，全速开火，并按下秒表。
                3. 过程：
                    前 2.3 秒：专心射击。
                    2.3秒 ~ 4.2秒：一边射击，一边动一下进气摇臂（防止卡弹）。
                4. 结束：到了 4.2 秒，停火，放下吸盘，开启吸气，切换到下一步（去捡球）。
                */

            case 2:
                // 1. 确认状态
                if(!follower.isBusy()) {

                    // 2. 彻底关闭射击
                    shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
                    
                    // 3. 开启吸球模式 (关键点)
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    
                    // 4. 刷新硬件状态
                    shooter.periodic();
                    
                    // 5. 命令出发去捡球
                    follower.followPath(prepGatherPath1);
                    
                    // 6. 进入下一阶段
                    setPathState(3);
                }

                break;
                // “捡球突击”的发令枪。它告诉机器人：“别射了，把吸盘开到最大，冲向球堆”

            case 3:
                // 1. 动态调整吸球状态 (位置触发)
                if(follower.getPose().getY()<-34){
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    intake.periodic();
                }
                else if (follower.getPose().getY()<-25){
                    intake.setIntakeState(Intake.IntakeTransferState.Send_It_Up);
                    intake.periodic();
                }

                // 2. 检查是否到达终点
                if(!follower.isBusy()) {
                    // 3. 切换到下一阶段
                    setPathState(4);
                    
                    // 4. 到达后的状态保持
                    shooter.setShooterStatus(Shooter.ShooterStatus.Idling);
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    shooter.periodic();
                }
                break;
                // 一边开车去捡球，一边根据离球堆的远近，熟练地调整吸尘器的档位，确保球能顺畅地进入机器肚子里。


            case 4:
                // 1. 命令返回
                follower.followPath(Shootpath2);
                
                // 2. 重置射击标志位
                firstshooting = false;
                
                // 3. 切换到下一阶段
                setPathState(5);

                break;
                // 是“满载而归”的指令。它告诉机器人：“球捡到了？重置标志位，回射击点准备开火”
            
            // case 1+2+3+4 是一个阶段，之后copy paste三次

            
            case 5:
                if(!follower.isBusy()) {
                    if (!firstshooting) {
                        shooter.updateFocused(true);
                        shooter.setShooterStatus(Shooter.ShooterStatus.Shooting);
                        timer.resetTimer();

                        firstshooting = true;
                    }
                    else{
                        if(timer.getElapsedTimeSeconds()> 4.2){
                            shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
                            intake.setSwingBarPos(0.4);
                            intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                            setPathState(6);
                        }
                        if(timer.getElapsedTimeSeconds()>2.3 && timer.getElapsedTimeSeconds()<4.2){
                            intake.setSwingBarPos(0);
                        }

                    }
                    break;
                }
            case 6:
                if(!follower.isBusy()) {

                    shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    shooter.periodic();
                    follower.followPath(prepGatherPath2);
                    setPathState(7);
                }
                break;
            case 7:
                if(follower.getPose().getY()<-34){
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    intake.periodic();
                }
                else if (follower.getPose().getY()< -25){
                    intake.setIntakeState(Intake.IntakeTransferState.Send_It_Up);
                    intake.periodic();
                }
                if(!follower.isBusy()) {
                    setPathState(8);
                    shooter.setShooterStatus(Shooter.ShooterStatus.Idling);
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    shooter.periodic();
                }
                break;
            case 8:
                shooter.autoLonger = false;
                follower.followPath(Shootpath3);
                firstshooting = false;
                setPathState(9);

                break;
            case 9:
                if(!follower.isBusy()) {
                    if (!firstshooting) {
                        shooter.updateFocused(true);
                        shooter.setShooterStatus(Shooter.ShooterStatus.Shooting);
                        timer.resetTimer();

                        firstshooting = true;
                    }
                    else{
                        if(timer.getElapsedTimeSeconds()> 4.2){
                            shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
                            intake.setSwingBarPos(0.4);
                            intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                            setPathState(10);
                        }
                        if(timer.getElapsedTimeSeconds()>2.3 && timer.getElapsedTimeSeconds()<4.2){
                            intake.setSwingBarPos(0);
                        }

                    }
                    break;
                }
            case 10:
                if(!follower.isBusy()) {

                    shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    shooter.periodic();
                    follower.followPath(prepGatherPath3);
                    setPathState(11);
                }
                break;
            case 11:
                if(follower.getPose().getY()<-34){
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    intake.periodic();
                }
                else if (follower.getPose().getY()< -25){
                    intake.setIntakeState(Intake.IntakeTransferState.Send_It_Up);
                    intake.periodic();
                }
                if(!follower.isBusy()) {
                    setPathState(12);
                    shooter.setShooterStatus(Shooter.ShooterStatus.Idling);
                    intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                    shooter.periodic();
                }
                break;
            case 12:

                follower.followPath(Shootpath4);
                firstshooting = false;
                setPathState(13);

                break;
            case 13:
                if(!follower.isBusy()) {
                    if (!firstshooting) {
                        shooter.updateFocused(true);
                        shooter.setShooterStatus(Shooter.ShooterStatus.Shooting);
                        timer.resetTimer();

                        firstshooting = true;
                    }
                    else{
                        if(timer.getElapsedTimeSeconds()> 4.2){
                            shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
                            intake.setSwingBarPos(0.4);
                            intake.setIntakeState(Intake.IntakeTransferState.Suck_In);
                            setPathState(14);
                        }
                        if(timer.getElapsedTimeSeconds()>2.3 && timer.getElapsedTimeSeconds()<4.2){
                            intake.setSwingBarPos(0);
                        }

                    }
                    break;
                }
            case 14:
                if(!follower.isBusy()) {
                    follower.followPath(lastOutPath);
                    setPathState(15);
                }
                break;
            case 15:
                if(!follower.isBusy()) {
                    setPathState(16);
                }

        }
    }

    // 两个函数：一个是睡眠函数，一个是设置路径状态的函数
    // 是上文和下文的辅助函数
    private void sleep(long ms){
        try{
            Thread.sleep(ms);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    /** These change the states of the paths and actions. It will also reset the timers of the individual switches **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }



    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    /*
    这段代码位于 loop() 方法中，它是整个自动程序（OpMode）的心脏。
    在 FTC 机器人控制系统中，loop() 方法会被系统反复调用（每秒几十次甚至上百次）。这里的每一行代码都是为了确保机器人的各个部分在每一瞬间都能保持更新和响应。
    */
    @Override
    public void loop() {

        // 第一步：感知与维持 (Sense & Maintain)
        // 读取传感器数据（编码器、陀螺仪、距离传感器等），根据上一帧设定的目标
        // These loop the movements of the robot, these must be called continuously in order to work
        // 是路径跟随算法的核心。它计算机器人当前位置（Pose）与目标路径的偏差，并计算出底盘四个电机此刻应该输出多少动力来修正偏差。
        follower.update();
        // 子系统内部有 PID 控制循环、传感器检测逻辑或状态机。必须不断调用它，子系统才能活着
        // 保持飞轮转速稳定（PID控制），或者检测进气口有没有卡球
        intake.periodic();
        shooter.periodic();

        // 第二步：协调与判断 (Coordinate & Judge)
        // 核心目的：确保只有在射击条件完全具备时，才允许进气系统把球送入飞轮。
        if(shooter.shooterStatus == Shooter.ShooterStatus.Shooting){
            // 如果射击系统处于“正在射击”状态（意味着飞轮正在加速或已经高速旋转）
            
            // 1. 告诉进气系统：“现在是自动射击模式”
            intake.updateAutoshoot(true);
            
            // 2. 关键判断：询问射击系统“飞轮转速达标了吗？”
            //    如果达标 (isAtTargetRPM 返回 true)，则告诉进气系统“可以输送了 (autotrans = true)”
            //    如果不达标，告诉进气系统“先别送 (autotrans = false)”
            intake.updateautotranse(shooter.isAtTargetRPM());
            
            // 3. 视觉辅助：把 Limelight 测到的距离传给射击系统
            //    射击系统会根据这个距离微调飞轮的目标转速（距离越远，转速越高）
            shooter.updateDis(limelight.getDis());
            
            // 4. 告诉射击系统：“现在要专注瞄准”
            shooter.updateFocused(true);
        }
        else{
            // 如果射击系统处于“停止”或“怠速”状态
            
            // 5. 告诉进气系统：“退出自动射击模式”
            //    此时进气系统会恢复到手动控制或默认状态，不再自动往上送球
            intake.updateAutoshoot(false);
            
            // 6. 告诉射击系统：“不用专注瞄准了”
            //    节省计算资源，或者恢复到低功耗待机状态
            shooter.updateFocused(false);
        }

        // 第三步：决策与推进 (Decide & Advance)
        // 状态机更新，执行前面的那个巨大的 switch (pathState) 状态机。
        autonomousPathUpdate();

        // Feedback to Driver Hub for debugging
        // 更新遥测系统（一般是手机上看到的界面吧），显示当前机器人的状态，方便调试和监控
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("timer", timer.getElapsedTimeSeconds());
        telemetry.addData("shooter state", shooter.shooterStatus);
        telemetry.addData("intake state", intake.intakeCurrentState);
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    // 初始化，变量定义，硬件映射，路径构建
    // 它在你在 Driver Station 上按下 "INIT" 按钮时执行一次
    @Override
    public void init() {
        pathTimer = new Timer();
        timer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        follower = Constants.createFollower(hardwareMap);
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        shooter.automode = true;
        limelight = new MyLimelight(hardwareMap);
        limelight.initBluePipeline();
        limelight.startDetect();
        intake.setIntakeState(Intake.IntakeTransferState.Intake_Steady);
        shooter.setShooterStatus(Shooter.ShooterStatus.Stop);
        intake.setSwingBarPos(0.4);
        buildPaths();
        follower.setStartingPose(startPose);

    }

    /** This method is called continuously after Init while waiting for "play". **/
    // 等待时的循环，系统反复调用，什么都不做
    @Override
    public void init_loop() {}

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        // 把比赛计时器归零。比赛正式开始，秒表开始走字。
        opmodeTimer.resetTimer();
        // 将状态机设置为 0，即把“开关”拨到了 0 的位置，但是这里不执行autonomousPathUpdate()
        setPathState(0);
    }

    public void setScheduler(Scheduler scheduler) {
        // autonomousPathUpdate() 这个大 switch 语句跑的，这个调度器可能没怎么用上，为了兼容某种框架结构。
        this.scheduler = scheduler;
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {}
}