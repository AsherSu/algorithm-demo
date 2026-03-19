package vrp;

import java.util.ArrayList;
import java.util.List;

/**
 * VRP 车辆路径问题 - 多机任务分配与路径规划
 * 贪心 + 最近插入
 */
public class VRP {
    private final VRPCustomer depot;
    private final List<VRPCustomer> customers;
    private final List<VRPVehicle> vehicles;

    public VRP(VRPCustomer depot, List<VRPCustomer> customers, List<VRPVehicle> vehicles) {
        this.depot = depot;
        this.customers = new ArrayList<>(customers);
        this.vehicles = new ArrayList<>(vehicles);
    }

    /**
     * 求解：分配客户到各车辆
     */
    public List<VRPVehicle> solve() {
        for (VRPVehicle v : vehicles) {
            v.route.clear();
            v.load = 0;
            v.distance = 0;
        }

        boolean[] assigned = new boolean[customers.size()];
        int remaining = customers.size();

        while (remaining > 0) {
            VRPVehicle bestV = null;
            VRPCustomer bestC = null;
            int bestIdx = -1;
            double bestCost = Double.MAX_VALUE;

            for (VRPVehicle v : vehicles) {
                for (int i = 0; i < customers.size(); i++) {
                    if (assigned[i]) continue;
                    VRPCustomer c = customers.get(i);
                    double distToDepot = c.distanceTo(depot);
                    if (!v.canAdd(c, depot, distToDepot * 2)) continue;

                    double cost;
                    if (v.route.isEmpty()) {
                        cost = depot.distanceTo(c) + distToDepot;
                    } else {
                        int lastId = v.route.get(v.route.size() - 1);
                        VRPCustomer last = getCustomer(lastId);
                        cost = last.distanceTo(c) + distToDepot - depot.distanceTo(last);
                    }
                    if (cost < bestCost) {
                        bestCost = cost;
                        bestV = v;
                        bestC = c;
                        bestIdx = i;
                    }
                }
            }

            if (bestV == null) break;

            bestV.route.add(bestC.id);
            bestV.load += bestC.demand;
            if (bestV.route.size() == 1) {
                bestV.distance = depot.distanceTo(bestC) + bestC.distanceTo(depot);
            } else {
                VRPCustomer prev = getCustomer(bestV.route.get(bestV.route.size() - 2));
                bestV.distance += prev.distanceTo(bestC) + bestC.distanceTo(depot) - prev.distanceTo(depot);
            }
            assigned[bestIdx] = true;
            remaining--;
        }

        return vehicles;
    }

    private VRPCustomer getCustomer(int id) {
        for (VRPCustomer c : customers) {
            if (c.id == id) return c;
        }
        return depot;
    }
}
