package vrp;

import java.util.ArrayList;
import java.util.List;

/**
 * VRP 车辆路径问题 - 无人机/车辆及其路线
 */
public class VRPVehicle {
    public final int id;
    public final double capacity;
    public final double maxDistance;
    public final List<Integer> route;  // 客户 id 序列
    public double load;
    public double distance;

    public VRPVehicle(int id, double capacity, double maxDistance) {
        this.id = id;
        this.capacity = capacity;
        this.maxDistance = maxDistance;
        this.route = new ArrayList<>();
        this.load = 0;
        this.distance = 0;
    }

    public boolean canAdd(VRPCustomer c, VRPCustomer depot, double distToDepot) {
        return load + c.demand <= capacity
                && distance + distToDepot <= maxDistance;
    }
}
