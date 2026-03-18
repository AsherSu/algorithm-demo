package rrt;

/**
 * RRT 树节点
 */
public class RRTNode {
    public final double x;
    public final double y;
    public final RRTNode parent;

    public RRTNode(double x, double y, RRTNode parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    public double distanceTo(double px, double py) {
        return Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
    }

    public double distanceTo(RRTNode other) {
        return distanceTo(other.x, other.y);
    }

    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}
