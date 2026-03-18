package rrtstar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * RRT* (Rapidly-exploring Random Tree Star) 算法
 * RRT 的渐进最优变体，通过 Choose Parent 和 Rewire 得到更短路径
 */
public class RRTStar {
    private final double width;
    private final double height;
    private final double stepSize;
    private final double goalThreshold;
    private final double goalBias;
    private final double searchRadius;
    private final Random random;
    private final ObstacleChecker obstacleChecker;

    public RRTStar(double width, double height, ObstacleChecker obstacleChecker) {
        this(width, height, obstacleChecker, 1.0, 0.5, 0.1, 2.0);
    }

    public RRTStar(double width, double height, ObstacleChecker obstacleChecker,
                   double stepSize, double goalThreshold, double goalBias, double searchRadius) {
        this.width = width;
        this.height = height;
        this.obstacleChecker = obstacleChecker;
        this.stepSize = stepSize;
        this.goalThreshold = goalThreshold;
        this.goalBias = goalBias;
        this.searchRadius = searchRadius;
        this.random = new Random();
    }

    @FunctionalInterface
    public interface ObstacleChecker {
        boolean isObstacle(double x, double y);
    }

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

    public List<RRTStarNode> findPath(double startX, double startY, double goalX, double goalY) {
        return findPath(startX, startY, goalX, goalY, 10000);
    }

    public List<RRTStarNode> findPath(double startX, double startY, double goalX, double goalY, int maxIterations) {
        if (obstacleChecker.isObstacle(startX, startY) || obstacleChecker.isObstacle(goalX, goalY)) {
            return new ArrayList<>();
        }

        List<RRTStarNode> tree = new ArrayList<>();
        RRTStarNode start = new RRTStarNode(startX, startY, null, 0);
        tree.add(start);

        RRTStarNode bestGoalNode = null;
        double bestGoalCost = Double.MAX_VALUE;

        for (int i = 0; i < maxIterations; i++) {
            double sampleX;
            double sampleY;

            if (random.nextDouble() < goalBias) {
                sampleX = goalX;
                sampleY = goalY;
            } else {
                sampleX = random.nextDouble() * width;
                sampleY = random.nextDouble() * height;
            }

            RRTStarNode nearest = findNearest(tree, sampleX, sampleY);
            RRTStarNode newNode = steer(nearest, sampleX, sampleY);

            if (newNode == null || obstacleChecker.isObstacle(newNode.x, newNode.y)) {
                continue;
            }

            double r = Math.min(searchRadius, gammaRRTStar(tree.size()));
            List<RRTStarNode> nearNodes = findNear(tree, newNode.x, newNode.y, r);

            RRTStarNode bestParent = null;
            double bestCost = Double.MAX_VALUE;

            for (RRTStarNode near : nearNodes) {
                double cost = near.cost + near.distanceTo(newNode);
                if (cost < bestCost && !lineIntersectsObstacle(near.x, near.y, newNode.x, newNode.y)) {
                    bestParent = near;
                    bestCost = cost;
                }
            }

            if (bestParent == null) {
                continue;
            }

            newNode = new RRTStarNode(newNode.x, newNode.y, bestParent, bestCost);
            tree.add(newNode);

            for (RRTStarNode near : nearNodes) {
                double costThroughNew = newNode.cost + newNode.distanceTo(near);
                if (costThroughNew < near.cost && !lineIntersectsObstacle(newNode.x, newNode.y, near.x, near.y)) {
                    near.parent = newNode;
                    near.cost = costThroughNew;
                    propagateCostToDescendants(near, tree);
                }
            }

            if (newNode.distanceTo(goalX, goalY) <= goalThreshold) {
                double goalCost = newNode.cost + newNode.distanceTo(goalX, goalY);
                if (goalCost < bestGoalCost) {
                    bestGoalCost = goalCost;
                    bestGoalNode = newNode;
                }
            }
        }

        if (bestGoalNode != null) {
            return reconstructPath(bestGoalNode, goalX, goalY);
        }
        return new ArrayList<>();
    }

    private double gammaRRTStar(int n) {
        return 2.0 * Math.pow(1.0 + 1.0 / 2, 1.0 / 2) * Math.pow(width * height / Math.PI, 0.5);
    }

    private RRTStarNode findNearest(List<RRTStarNode> tree, double x, double y) {
        RRTStarNode nearest = tree.get(0);
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

    private List<RRTStarNode> findNear(List<RRTStarNode> tree, double x, double y, double radius) {
        List<RRTStarNode> near = new ArrayList<>();
        for (RRTStarNode node : tree) {
            if (node.distanceTo(x, y) <= radius) {
                near.add(node);
            }
        }
        return near;
    }

    private RRTStarNode steer(RRTStarNode from, double toX, double toY) {
        double dist = from.distanceTo(toX, toY);
        if (dist <= stepSize) {
            return new RRTStarNode(toX, toY, from, from.cost + dist);
        }
        double ratio = stepSize / dist;
        double newX = from.x + (toX - from.x) * ratio;
        double newY = from.y + (toY - from.y) * ratio;
        double edgeCost = from.distanceTo(newX, newY);
        return new RRTStarNode(newX, newY, from, from.cost + edgeCost);
    }

    private void propagateCostToDescendants(RRTStarNode node, List<RRTStarNode> tree) {
        for (RRTStarNode child : tree) {
            if (child.parent == node) {
                double newCost = node.cost + node.distanceTo(child);
                child.cost = newCost;
                propagateCostToDescendants(child, tree);
            }
        }
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

    private List<RRTStarNode> reconstructPath(RRTStarNode lastNode, double goalX, double goalY) {
        List<RRTStarNode> path = new ArrayList<>();
        RRTStarNode current = lastNode;
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        if (lastNode.distanceTo(goalX, goalY) > 1e-6) {
            path.add(new RRTStarNode(goalX, goalY, lastNode, lastNode.cost + lastNode.distanceTo(goalX, goalY)));
        }
        return path;
    }
}
