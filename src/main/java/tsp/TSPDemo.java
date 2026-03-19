package tsp;

import java.util.ArrayList;
import java.util.List;

/**
 * TSP 旅行商问题演示 - 电力巡检无人机访问铁塔顺序
 */
public class TSPDemo {
    public static void main(String[] args) {
        List<TSPPoint> towers = new ArrayList<>();
        towers.add(new TSPPoint(0, 0, 0));   // 起点/机库
        towers.add(new TSPPoint(1, 2, 1));
        towers.add(new TSPPoint(2, 4, 0));
        towers.add(new TSPPoint(3, 3, 3));
        towers.add(new TSPPoint(4, 1, 4));
        towers.add(new TSPPoint(5, 5, 2));

        TSP tsp = new TSP(towers);
        List<Integer> route = tsp.solve();

        System.out.println("TSP 巡检顺序 (最近邻 + 2-opt):");
        for (int i = 0; i < route.size(); i++) {
            int idx = route.get(i);
            TSPPoint p = towers.get(idx);
            System.out.printf("  %d -> (%d, %.0f, %.0f)%n", i + 1, p.id, p.x, p.y);
        }
        System.out.printf("总路程: %.2f%n", tsp.totalDistance(route));
    }
}
