package antcolony;

/**
 * 蚁群算法 - 城市节点
 */
public class AntColonyCity {
    public final int id;
    public final double x;
    public final double y;

    public AntColonyCity(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double distanceTo(AntColonyCity other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }
}
