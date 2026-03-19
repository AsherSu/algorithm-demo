package dwa;

/**
 * DWA 动态窗口法 - 机器人/无人机状态
 */
public class DWAState {
    public double x;
    public double y;
    public double theta;   // 朝向角
    public double v;       // 线速度
    public double omega;   // 角速度

    public DWAState(double x, double y, double theta, double v, double omega) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.v = v;
        this.omega = omega;
    }

    public DWAState copy() {
        return new DWAState(x, y, theta, v, omega);
    }
}
