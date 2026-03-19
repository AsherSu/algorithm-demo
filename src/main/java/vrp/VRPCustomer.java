package vrp;

/**
 * VRP 车辆路径问题 - 客户/配送点
 */
public class VRPCustomer {
    public final int id;
    public final double x;
    public final double y;
    public final double demand;

    public VRPCustomer(int id, double x, double y, double demand) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
    }

    public double distanceTo(VRPCustomer other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }
}
