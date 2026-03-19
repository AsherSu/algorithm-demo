package dijkstra;

import java.util.*;

/**
 * Dijkstra 算法 - 全局最短路径规划
 * 从起点向外扩散，计算到达每个点的最短距离
 */
public class Dijkstra {
    private final int[][] grid;
    private final int rows;
    private final int cols;
    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public Dijkstra(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    public List<DijkstraNode> findPath(int startX, int startY, int endX, int endY) {
        if (!valid(startX, startY) || !valid(endX, endY)) {
            return Collections.emptyList();
        }

        // 节点映射，key格式为 "x,y"
        Map<String, DijkstraNode> nodes = new HashMap<>();
        // 以距离为优先级的最小堆
        PriorityQueue<DijkstraNode> pq = new PriorityQueue<>();

        DijkstraNode start = new DijkstraNode(startX, startY);
        start.dist = 0;
        nodes.put(key(startX, startY), start);
        pq.add(start);

        // 已确定最短路径的节点集合
        Set<String> settled = new HashSet<>();
        while (!pq.isEmpty()) {
            DijkstraNode cur = pq.poll();
            if (settled.contains(key(cur.x, cur.y))) continue;
            settled.add(key(cur.x, cur.y));

            if (cur.x == endX && cur.y == endY) {
                return buildPath(cur);
            }

            for (int[] d : DIRS) {
                int nx = cur.x + d[0], ny = cur.y + d[1];
                if (!valid(nx, ny)) continue;

                DijkstraNode next = nodes.computeIfAbsent(key(nx, ny), k -> new DijkstraNode(nx, ny));
                double edge = 1.0;
                double nd = cur.dist + edge;
                if (nd < next.dist) {
                    next.dist = nd;
                    next.prev = cur;
                    pq.add(next);
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean valid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols && grid[x][y] == 0;
    }

    private String key(int x, int y) {
        return x + "," + y;
    }

    private List<DijkstraNode> buildPath(DijkstraNode end) {
        List<DijkstraNode> path = new ArrayList<>();
        for (DijkstraNode n = end; n != null; n = n.prev) {
            path.add(0, n);
        }
        return path;
    }
}
