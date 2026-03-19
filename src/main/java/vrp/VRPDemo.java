package vrp;

import java.util.ArrayList;
import java.util.List;

/**
 * VRP 车辆路径问题演示 - 多架无人机配送调度
 */
public class VRPDemo {
    public static void main(String[] args) {
        VRPCustomer depot = new VRPCustomer(0, 0, 0, 0);

        List<VRPCustomer> customers = new ArrayList<>();
        customers.add(new VRPCustomer(1, 2, 1, 1));
        customers.add(new VRPCustomer(2, 4, 0, 2));
        customers.add(new VRPCustomer(3, 3, 3, 1));
        customers.add(new VRPCustomer(4, 1, 4, 2));
        customers.add(new VRPCustomer(5, 5, 2, 1));

        List<VRPVehicle> vehicles = new ArrayList<>();
        vehicles.add(new VRPVehicle(1, 5, 20));
        vehicles.add(new VRPVehicle(2, 5, 20));

        VRP vrp = new VRP(depot, customers, vehicles);
        List<VRPVehicle> result = vrp.solve();

        System.out.println("VRP 多机配送方案:");
        for (VRPVehicle v : result) {
            System.out.printf("  无人机 %d: 仓库", v.id);
            for (int id : v.route) {
                VRPCustomer c = getById(customers, id);
                System.out.printf(" -> (%d,%.0f,%.0f)", c.id, c.x, c.y);
            }
            System.out.printf(" -> 仓库 | 载重%.0f/%.0f 路程%.2f%n", v.load, v.capacity, v.distance);
        }
    }

    private static VRPCustomer getById(List<VRPCustomer> list, int id) {
        for (VRPCustomer c : list) if (c.id == id) return c;
        return null;
    }
}
