package rrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * RRT (Rapidly-exploring Random Tree) 快速探索随机树算法
 * 在二维连续空间中寻找从起点到终点的可行路径
 */
public class RRT {
    // 边界范围
    private final double width;
    private final double height;
    // 树向目标方向扩展的最大步长
    private final double stepSize;
    // 判断已到达目标的距离阈值，当新节点距离目标小于该值时认为已找到路径
    private final double goalThreshold;
    // 目标偏置概率，以该概率直接采样目标位置
    private final double goalBias;
    private final Random random;
    // 障碍检测
    private final ObstacleChecker obstacleChecker;

    public RRT(double width, double height, ObstacleChecker obstacleChecker) {
        this(width, height, obstacleChecker, 1.0, 0.5, 0.1);
    }

    public RRT(double width, double height, ObstacleChecker obstacleChecker,
                double stepSize, double goalThreshold, double goalBias) {
        this.width = width;
        this.height = height;
        this.obstacleChecker = obstacleChecker;
        this.stepSize = stepSize;
        this.goalThreshold = goalThreshold;
        this.goalBias = goalBias;
        this.random = new Random();
    }

    /**
     * 障碍物检测接口
     */
    @FunctionalInterface
    public interface ObstacleChecker {
        boolean isObstacle(double x, double y);
    }

    /**
     * 基于网格的障碍物检测
     */
    public static ObstacleChecker gridObstacleChecker(int[][] grid, double cellWidth, double cellHeight) {
        return (x, y) -> {
            int row = (int) (y / cellHeight);
            int col = (int) (x / cellWidth);
            if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
                return true;
            }
            return grid[row][col] == 1;
        };
    }

    /**
     * 执行 RRT 搜索，返回从起点到终点的路径
     * 1、随机采样一个点，小于goalBias则直接采样目标位置
     * 2、在树中找到距离采样点最近的节点
     * 3、从最近节点朝采样点方向生成一个新节点，距离不超过stepSize
     * 4、如果 新节点不在障碍物中 且 节点之间的线段不穿过障碍物，则将新节点加入树中
     * 5、如果新节点距离目标小于goalThreshold，则认为找到路径
     */
    public List<RRTNode> findPath(double startX, double startY, double goalX, double goalY) {
        return findPath(startX, startY, goalX, goalY, 10000);
    }

    public List<RRTNode> findPath(double startX, double startY, double goalX, double goalY, int maxIterations) {
        if (obstacleChecker.isObstacle(startX, startY) || obstacleChecker.isObstacle(goalX, goalY)) {
            return new ArrayList<>();
        }

        // 正在构建的随机搜索树
        List<RRTNode> tree = new ArrayList<>();
        tree.add(new RRTNode(startX, startY, null));

        // 目标节点
        RRTNode goalNode = new RRTNode(goalX, goalY, null);

        for (int i = 0; i < maxIterations; i++) {
            double sampleX;
            double sampleY;

            // 以 goalBias 的概率直接采样目标位置，否则随机采样
            if (random.nextDouble() < goalBias) {
                sampleX = goalX;
                sampleY = goalY;
            } else {
                sampleX = random.nextDouble() * width;
                sampleY = random.nextDouble() * height;
            }

            // 在树中找到距离采样点最近的节点
            RRTNode nearest = findNearest(tree, sampleX, sampleY);
            // 从最近节点朝采样点方向扩展一个新节点
            RRTNode newNode = steer(nearest, sampleX, sampleY);

            if (newNode != null && !obstacleChecker.isObstacle(newNode.x, newNode.y)
                    && !lineIntersectsObstacle(nearest.x, nearest.y, newNode.x, newNode.y)) {
                tree.add(newNode);

                if (newNode.distanceTo(goalNode) <= goalThreshold) {
                    return reconstructPath(newNode, goalX, goalY);
                }
            }
        }

        return new ArrayList<>();
    }

    private RRTNode findNearest(List<RRTNode> tree, double x, double y) {
        RRTNode nearest = tree.get(0);
        double minDist = nearest.distanceTo(x, y);

        for (int i = 1; i < tree.size(); i++) {
            double d = tree.get(i).distanceTo(x, y);
            if (d < minDist) {
                minDist = d;
                nearest = tree.get(i);
            }
        }
        return nearest;
    }

    private RRTNode steer(RRTNode from, double toX, double toY) {
        double dist = from.distanceTo(toX, toY);
        if (dist <= stepSize) {
            return new RRTNode(toX, toY, from);
        }
        double ratio = stepSize / dist;
        double newX = from.x + (toX - from.x) * ratio;
        double newY = from.y + (toY - from.y) * ratio;
        return new RRTNode(newX, newY, from);
    }

    private boolean lineIntersectsObstacle(double x1, double y1, double x2, double y2) {
        int steps = Math.max(10, (int) (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) * 2));
        for (int i = 1; i < steps; i++) {
            double t = (double) i / steps;
            double x = x1 + (x2 - x1) * t;
            double y = y1 + (y2 - y1) * t;
            if (obstacleChecker.isObstacle(x, y)) {
                return true;
            }
        }
        return false;
    }

    private List<RRTNode> reconstructPath(RRTNode lastNode, double goalX, double goalY) {
        List<RRTNode> path = new ArrayList<>();
        RRTNode current = lastNode;
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        path.add(new RRTNode(goalX, goalY, lastNode));
        return path;
    }
}
