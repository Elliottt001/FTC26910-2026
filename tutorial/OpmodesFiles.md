# Auto OpModes 代码文件解析

位于 `FtcRobotController/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmodes` 目录下的自动控制代码文件，主要根据**联盟颜色（红/蓝）**、**起始位置（远/近）**以及**得分策略（球数/路径）**进行了区分。

## 1. 命名规则与分类

文件名通常遵循 `颜色_位置_策略` 的命名格式：

*   **颜色**: `BLUE` (蓝盟) vs `RED` (红盟)
*   **位置**: `Far` (远端/观众席侧) vs `Near` (近端/后台侧)
*   **策略**: `12ball` (尝试更多次循环得分) vs `9ball` (较少次循环) vs `Test` (测试/简化版)

## 2. 具体文件功能对比

### A. 远端自动 (Far Side Auto)
这些程序适用于机器人从离得分区较远的位置（通常是观众席一侧）出发。

| 文件名 | 描述与区别 |
| :--- | :--- |
| **`BLUE_Far_12ball.java`** | **蓝盟远端主程序**。<br>• **逻辑**：执行 3 次“捡球-射击”循环（Gather1/2/3）。<br>• **路径**：从起点 -> 射击点1 -> 捡球点1 -> 射击点2 -> 捡球点2...<br>• **特点**：坐标系 Y 轴为正，Heading 角度适应蓝方。 |
| **`RED_Far_12ball.java`** | **红盟远端主程序**。<br>• **逻辑**：与 `BLUE_Far_12ball` 完全对应，但坐标镜像。<br>• **路径**：逻辑步骤一致，但 Y 轴坐标为负，Heading 角度取反。<br>• **目标**：尝试完成所有 3 堆球的拾取和射击。 |
| **`RED_Far_9ball.java`** | **红盟远端（保守版）**。<br>• **逻辑**：比 12ball 版本少一个循环。<br>• **区别**：它只执行到 `FinishGather2` 和随后的射击，没有去捡第 3 堆球（`PrepGather3`）。<br>• **用途**：当时间不够或为了求稳时使用。 |

### B. 近端自动 (Near Side Auto)
这些程序适用于机器人从离得分区较近的位置（后台一侧）出发。

| 文件名 | 描述与区别 |
| :--- | :--- |
| **`BLUE_Near_12ballPk.java`** | **蓝盟近端主程序**。<br>• **逻辑**：近端特有的路径规划，坐标 X 轴多为负值（如 `ShootPose1` x ≈ -40）。<br>• **特点**：虽然代码里保留了 `GatePose`（过门）的定义，但在 `buildPaths` 中被注释掉了，采用直接路径。 |
| **`RED_Near_12ball.java`** | **红盟近端（带过门逻辑）**。<br>• **OpMode Name**: `"RED_Near_12ball_gate"`<br>• **核心区别**：在路径规划中显式包含了 **Gate（门）** 的路径点。它会先走到 `GatePose`，再穿过 `GatePassby` 去射击。这通常是为了避障或符合特定的场地规则。 |
| **`RED_Near_12ballTest.java`** | **红盟近端（直连/测试版）**。<br>• **OpMode Name**: `"RED_Near_12ball"`<br>• **核心区别**：这是 `RED_Near_12ball.java` 的修改版。它**注释掉了**过门（Gate）的路径点，直接从 `FinishGather1` 连线到 `ShootPose1`。<br>• **用途**：可能是为了测试更快的路径，或者在不需要避障时节省时间。 |

## 3. 代码逻辑共性

所有这些文件都共享同一套底层逻辑架构：

1.  **状态机 (State Machine)**: 使用 `switch(pathState)` 来分步执行任务（移动 -> 射击 -> 捡球 -> 循环）。
2.  **子系统调用**:
    *   `Follower`: 使用 PedroPathing 库进行路径跟随。
    *   `Shooter`: 控制飞轮射击，包含自动瞄准 (`updateFocused`) 和转速检查。
    *   `Intake`: 控制进气和输送，与射击状态联动（射击时自动输送）。
    *   `Limelight`: 视觉辅助，用于测距 (`updateDis`)。

## 4. 比赛建议

*   **红盟远端**：
    *   追求高分：使用 `RED_Far_12ball`。
    *   求稳/时间不足：使用 `RED_Far_9ball`。
*   **红盟近端**：
    *   需要避障（过门）：使用 `RED_Near_12ball.java`。
    *   路径清空（直连）：使用 `RED_Near_12ballTest.java`。
*   **蓝盟**：
    *   对应选择 `BLUE_` 开头的文件。
