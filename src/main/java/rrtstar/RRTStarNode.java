package rrtstar;

/**
 * RRT* 树节点，包含从起点到该节点的代价
 */
public class RRTStarNode {
    public final double x;
    public final double y;
    public RRTStarNode parent;
    public double cost;  // 从起点到当前节点的累积代价

    public RRTStarNode(double x, double y, RRTStarNode parent, double cost) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.cost = cost;
    }

    public double distanceTo(double px, double py) {
        return Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
    }

    public double distanceTo(RRTStarNode other) {
        return distanceTo(other.x, other.y);
    }

    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}
