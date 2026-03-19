package dijkstra;

/**
 * Dijkstra 算法网格节点
 */
public class DijkstraNode implements Comparable<DijkstraNode> {
    public final int x;
    public final int y;
    public double dist;
    public DijkstraNode prev;

    public DijkstraNode(int x, int y) {
        this.x = x;
        this.y = y;
        this.dist = Double.MAX_VALUE;
        this.prev = null;
    }

    @Override
    public int compareTo(DijkstraNode o) {
        return Double.compare(this.dist, o.dist);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DijkstraNode n = (DijkstraNode) obj;
        return x == n.x && y == n.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
