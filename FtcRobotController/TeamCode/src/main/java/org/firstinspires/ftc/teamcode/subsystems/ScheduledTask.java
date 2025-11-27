package org.firstinspires.ftc.teamcode.subsystems;

import java.util.concurrent.Callable;

/**
 * 调度任务类：把“什么时候做”和“做什么”打包在一起，方便 Scheduler（调度器）进行管理。
 */
public class ScheduledTask {
    // 任务计划执行的绝对时间戳（单位：毫秒）
    // 例如：如果现在是 1000ms，想延时 500ms 执行，这里存的就是 1500
    public double milliseconds;  
    
    // 要执行的具体代码块
    // Runnable 是一个接口，通常用 Lambda 表达式传入，例如 () -> { ... }
    public Runnable task;

    public ScheduledTask(double time, Runnable task) {
        this.milliseconds = time;
        this.task = task;
    }
}