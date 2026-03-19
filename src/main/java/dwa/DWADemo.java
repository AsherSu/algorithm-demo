package dwa;

import java.util.List;

/**
 * DWA 动态窗口法演示 - 局部避障
 */
public class DWADemo {
    public static void main(String[] args) {
        int[][] grid = {
                {0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 0, 0},
                {0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0}
        };

        DWA.ObstacleChecker checker = DWA.gridChecker(grid, 1.0);
        DWA dwa = new DWA(checker);

        DWAState start = new DWAState(0.5, 1.5, 0, 0, 0);
        double goalX = 5.5, goalY = 2.5;

        List<DWAState> path = dwa.run(start, goalX, goalY, 80);

        System.out.println("DWA 轨迹 (" + path.size() + " 步):");
        for (int i = 0; i < path.size(); i += 5) {
            DWAState s = path.get(i);
            System.out.printf("  (%.1f, %.1f) theta=%.2f%n", s.x, s.y, s.theta);
        }
        DWAState last = path.get(path.size() - 1);
        System.out.printf("终点: (%.1f, %.1f)%n", last.x, last.y);
    }
}
