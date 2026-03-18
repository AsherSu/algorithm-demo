package astar;

import java.util.*;

/**
 * A* 路径搜索算法
 * 在二维网格中寻找从起点到终点的最短路径
 */
public class AStar {
    private final int[][] grid;  // 0=可通行, 1=障碍物
    private final int rows;
    private final int cols;

    // 八方向移动 (上下左右 + 斜向)
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // 上下左右
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // 斜向
    };

    // 仅四方向移动时使用
    private static final int[][] DIRECTIONS_4 = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    public AStar(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    /**
     * 使用欧几里得距离作为启发式函数
     */
    private double heuristic(Node a, Node b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * 使用曼哈顿距离作为启发式函数（适用于仅四方向移动）
     */
    private double manhattanHeuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * 检查坐标是否在网格内且可通行
     */
    private boolean isValid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols && grid[x][y] == 0;
    }

    /**
     * 执行 A* 搜索，返回从起点到终点的路径，若无路径则返回空列表
     */
    public List<Node> findPath(int startX, int startY, int endX, int endY) {
        return findPath(startX, startY, endX, endY, true);
    }

    /**
     * @param allowDiagonal 是否允许斜向移动
     */
    public List<Node> findPath(int startX, int startY, int endX, int endY, boolean allowDiagonal) {
        if (!isValid(startX, startY) || !isValid(endX, endY)) {
            return Collections.emptyList();
        }

        Node start = new Node(startX, startY);
        Node end = new Node(endX, endY);
        start.setG(0);
        start.setH(heuristic(start, end));

        // 按照优先级存放待处理的节点，优先级由 f 值决定
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        // 用于查询
        Set<Node> openSetLookup = new HashSet<>();
        // 存放计算过的节点
        Map<String, Node> allNodes = new HashMap<>();
        allNodes.put(key(startX, startY), start);

        openSet.add(start);
        openSetLookup.add(start);

        int[][] directions = allowDiagonal ? DIRECTIONS : DIRECTIONS_4;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            openSetLookup.remove(current);

            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (!isValid(nx, ny)) continue;

                Node neighbor = allNodes.computeIfAbsent(key(nx, ny), k -> new Node(nx, ny));

                // 斜向移动代价为 sqrt(2)，直线移动代价为 1
                double moveCost = (dir[0] != 0 && dir[1] != 0) ? Math.sqrt(2) : 1.0;
                double tentativeG = current.g + moveCost;

                if (tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.setG(tentativeG);
                    neighbor.setH(heuristic(neighbor, end));

                    if (!openSetLookup.contains(neighbor)) {
                        openSet.add(neighbor);
                        openSetLookup.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private String key(int x, int y) {
        return x + "," + y;
    }

    private List<Node> reconstructPath(Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        return path;
    }
}
