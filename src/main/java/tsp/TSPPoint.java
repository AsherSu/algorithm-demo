package tsp;

/**
 * TSP 旅行商问题 - 城市/巡检点
 */
public class TSPPoint {
    public final double x;
    public final double y;
    public final int id;

    public TSPPoint(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double distanceTo(TSPPoint other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }
}
