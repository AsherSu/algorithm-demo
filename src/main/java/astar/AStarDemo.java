package astar;

import java.util.List;

/**
 * A* 算法演示
 */
public class AStarDemo {
    public static void main(String[] args) {
        // 0=可通行, 1=障碍物
        int[][] grid = {
                {0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 1, 0},
                {0, 1, 1, 1, 0, 1, 0},
                {0, 0, 0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0}
        };

        AStar astar = new AStar(grid);
        List<Node> path = astar.findPath(0, 0, 6, 6);

        if (path.isEmpty()) {
            System.out.println("未找到路径");
        } else {
            System.out.println("找到路径，共 " + path.size() + " 步:");
            for (int i = 0; i < path.size(); i++) {
                Node node = path.get(i);
                System.out.print(node);
                if (i < path.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println();
            printGridWithPath(grid, path);
        }
    }

    private static void printGridWithPath(int[][] grid, List<Node> path) {
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] isPath = new boolean[rows][cols];
        for (Node node : path) {
            isPath[node.x][node.y] = true;
        }

        System.out.println("\n网格可视化 (S=起点, E=终点, *=路径, #=障碍):");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 && j == 0) {
                    System.out.print("S ");
                } else if (i == rows - 1 && j == cols - 1) {
                    System.out.print("E ");
                } else if (isPath[i][j]) {
                    System.out.print("* ");
                } else if (grid[i][j] == 1) {
                    System.out.print("# ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
}
