package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;

// 是一个优先级队列（堆）

/**
 * 调度器：用于保存并在指定时间执行任务
 * 这是一个简单的定时任务管理器，类似于闹钟系统
 */

/*
在机器人编程中，你经常需要做这种事：“按下 A 键，爪子闭合，等 0.5 秒，然后手臂抬起”。
如果你直接写 Thread.sleep(500)，整个机器人（包括底盘、摄像头）都会卡死 0.5 秒，这是非常危险的。

Scheduler 解决了这个问题：

它维护一个任务队列 (PriorityQueue)。
当你调用 addTaskAfter(500, ...) 时，它只是把任务记在小本本上：“在当前时间 + 500ms 的时候做这件事”，然后立即返回，不卡顿。
它的 elapse() 方法会在主循环里不断检查：“现在时间到了吗？到了就执行，没到就继续干别的”。
*/

/*  
但是，没有用到这个类：
搜一下 Scheduler
它在 RED_Far_12ball.java 和 BLUE_Far_12ball.java 这两个自动程序中被声明和引用了
在 init() 方法里，并没有 scheduler = new Scheduler(); 的初始化代码。
在 loop() 方法里，也没有调用 scheduler.elapse(); 来驱动它运行。
了基于状态机（switch (pathState)）和 Timer 的方式来实现（参考 RED_Far_12ball.java 里的 autonomousPathUpdate 方法），所以这个调度器就被搁置了。
*/
public class Scheduler {
    private final ElapsedTime timer;
    // 优先队列：自动按时间排序，最早要执行的任务排在最前面
    private final PriorityQueue<ScheduledTask> tasks;

    public Scheduler() {
        // 初始化计时器，精度为毫秒
        this.timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        
        // 初始化任务队列，定义排序规则：按任务的执行时间戳 (milliseconds) 从小到大排序
        this.tasks = new PriorityQueue<>(new Comparator<ScheduledTask>() {
            @Override
            public int compare(ScheduledTask t1, ScheduledTask t2) {
                return Double.compare(t1.milliseconds, t2.milliseconds);
            }
        });
    }

    /**
     * 添加一个延时任务
     * @param millis 延迟多少毫秒后执行
     * @param task 要执行的具体代码 (Runnable)
     */
    public void addTaskAfter(double millis, Runnable task) {
        // 计算绝对执行时间 = 当前时间 + 延迟时间
        ScheduledTask newTask = new ScheduledTask(this.timer.milliseconds() + millis, task);
        this.tasks.add(newTask);
    }

    /**
     * 检查并执行所有到期的任务
     * 这个方法需要在主循环 (loop) 中不断调用
     */
    // 只需要看队头那一个任务。如果队头任务的时间还没到（比如现在是 500ms，队头是 1000ms），那后面的任务肯定也没到，
    public void elapse() {
        // 只要队列不为空，就一直检查
        while (!this.tasks.isEmpty()) {
            // 如果队头任务的时间还没到，就退出循环（因为后面的任务肯定也没到）
            if (this.tasks.peek().milliseconds > this.timer.milliseconds()) break;
            
            // 取出并移除队头任务
            ScheduledTask task = this.tasks.poll();
            assert task != null;
            
            // 执行任务代码
            task.task.run();
        }
    }
}