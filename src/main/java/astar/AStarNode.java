package astar;

/**
 * 网格节点，用于 A* 算法
 */
public class AStarNode implements Comparable<AStarNode> {
    public final int x;
    public final int y;
    public double g;  // 从起点到当前节点的实际代价
    public double h;  // 从当前节点到终点的启发式估计代价
    public double f;  // f = g + h
    public AStarNode parent;

    public AStarNode(int x, int y) {
        this.x = x;
        this.y = y;
        this.g = Double.MAX_VALUE;
        this.h = 0;
        this.f = Double.MAX_VALUE;
        this.parent = null;
    }

    public void setG(double g) {
        this.g = g;
        this.f = this.g + this.h;
    }

    public void setH(double h) {
        this.h = h;
        this.f = this.g + this.h;
    }

    @Override
    public int compareTo(AStarNode other) {
        return Double.compare(this.f, other.f);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AStarNode AStarNode = (AStarNode) obj;
        return x == AStarNode.x && y == AStarNode.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
