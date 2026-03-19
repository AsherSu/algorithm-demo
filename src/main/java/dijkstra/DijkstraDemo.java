package dijkstra;

import java.util.List;

/**
 * Dijkstra 算法演示 - 全局最短路径
 */
public class DijkstraDemo {
    public static void main(String[] args) {
        int[][] grid = {
                {0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0},
                {0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0}
        };

        Dijkstra d = new Dijkstra(grid);
        List<DijkstraNode> path = d.findPath(0, 0, 4, 4);

        if (path.isEmpty()) {
            System.out.println("未找到路径");
        } else {
            System.out.println("最短路径 (" + path.size() + " 步):");
            for (int i = 0; i < path.size(); i++) {
                DijkstraNode n = path.get(i);
                System.out.print("(" + n.x + "," + n.y + ")");
                if (i < path.size() - 1) System.out.print(" -> ");
            }
            System.out.println("\n总距离: " + path.get(path.size() - 1).dist);
        }
    }
}
