package dwa;

import java.util.ArrayList;
import java.util.List;

/**
 * DWA (Dynamic Window Approach) 动态窗口法 - 局部避障
 * 在速度空间内采样，预测轨迹并评分，选择最优速度
 */
public class DWA {
    public double maxV = 1.0;
    public double maxOmega = Math.PI / 2;
    public double dt = 0.1;
    public double predictTime = 0.5;
    public double goalWeight = 1.0;
    public double obstacleWeight = 1.0;
    public double speedWeight = 0.1;
    public double robotRadius = 0.3;

    private final ObstacleChecker obstacleChecker;

    public DWA(ObstacleChecker obstacleChecker) {
        this.obstacleChecker = obstacleChecker;
    }

    @FunctionalInterface
    public interface ObstacleChecker {
        boolean isObstacle(double x, double y);
    }

    public static ObstacleChecker gridChecker(int[][] grid, double cellSize) {
        return (x, y) -> {
            int r = (int) (y / cellSize), c = (int) (x / cellSize);
            return r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] == 1;
        };
    }

    /**
     * 单步决策：返回最优 (v, omega)
     */
    public double[] plan(DWAState state, double goalX, double goalY) {
        double[] best = {0, 0};
        double bestScore = Double.NEGATIVE_INFINITY;

        int vSamples = 10;
        int omegaSamples = 10;
        for (int i = 0; i <= vSamples; i++) {
            for (int j = 0; j <= omegaSamples; j++) {
                double v = (double) i / vSamples * maxV;
                double omega = -maxOmega + (double) j / omegaSamples * 2 * maxOmega;

                DWAState end = simulate(state, v, omega);
                if (end == null) continue;

                double toGoal = 1.0 / (1.0 + distance(end.x, end.y, goalX, goalY));
                double toObst = minDistToObstacle(state, v, omega);
                double obstScore = toObst > robotRadius * 2 ? 1.0 : toObst / (robotRadius * 2);
                double speedScore = v / maxV;

                double score = goalWeight * toGoal + obstacleWeight * obstScore + speedWeight * speedScore;
                if (score > bestScore) {
                    bestScore = score;
                    best[0] = v;
                    best[1] = omega;
                }
            }
        }
        return best;
    }

    private DWAState simulate(DWAState s, double v, double omega) {
        DWAState cur = s.copy();
        int steps = (int) (predictTime / dt);
        for (int i = 0; i < steps; i++) {
            cur.x += v * Math.cos(cur.theta) * dt;
            cur.y += v * Math.sin(cur.theta) * dt;
            cur.theta += omega * dt;
            if (obstacleChecker.isObstacle(cur.x, cur.y)) return null;
        }
        return cur;
    }

    private double minDistToObstacle(DWAState s, double v, double omega) {
        DWAState cur = s.copy();
        double minD = Double.MAX_VALUE;
        int steps = (int) (predictTime / dt);
        for (int i = 0; i < steps; i++) {
            cur.x += v * Math.cos(cur.theta) * dt;
            cur.y += v * Math.sin(cur.theta) * dt;
            cur.theta += omega * dt;
            double d = nearestObstacleDist(cur.x, cur.y);
            if (d < minD) minD = d;
            if (d < robotRadius) return 0;
        }
        return minD;
    }

    private double nearestObstacleDist(double x, double y) {
        double step = 0.2;
        double minD = Double.MAX_VALUE;
        for (double dx = -2; dx <= 2; dx += step) {
            for (double dy = -2; dy <= 2; dy += step) {
                double px = x + dx, py = y + dy;
                if (obstacleChecker.isObstacle(px, py)) {
                    double d = distance(x, y, px, py);
                    if (d < minD) minD = d;
                }
            }
        }
        return minD == Double.MAX_VALUE ? 5.0 : minD;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * 执行多步，返回轨迹
     */
    public List<DWAState> run(DWAState start, double goalX, double goalY, int maxSteps) {
        List<DWAState> path = new ArrayList<>();
        DWAState cur = start.copy();
        path.add(cur.copy());
        for (int i = 0; i < maxSteps; i++) {
            if (distance(cur.x, cur.y, goalX, goalY) < 0.5) break;
            double[] cmd = plan(cur, goalX, goalY);
            cur.x += cmd[0] * Math.cos(cur.theta) * dt;
            cur.y += cmd[0] * Math.sin(cur.theta) * dt;
            cur.theta += cmd[1] * dt;
            path.add(cur.copy());
        }
        return path;
    }
}
