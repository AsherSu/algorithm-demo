package antcolony;

import java.util.ArrayList;
import java.util.List;

/**
 * 蚁群算法演示 - 求解 TSP
 */
public class AntColonyDemo {
    public static void main(String[] args) {
        List<AntColonyNode> nodes = new ArrayList<>();
        nodes.add(new AntColonyNode(0, 0, 0));
        nodes.add(new AntColonyNode(1, 2, 1));
        nodes.add(new AntColonyNode(2, 4, 0));
        nodes.add(new AntColonyNode(3, 3, 3));
        nodes.add(new AntColonyNode(4, 1, 4));
        nodes.add(new AntColonyNode(5, 5, 2));

        AntColony aco = new AntColony(nodes);
        List<Integer> route = aco.solve(20, 100);

        System.out.println("蚁群算法 TSP 结果:");
        for (int i = 0; i < route.size(); i++) {
            AntColonyNode n = nodes.get(route.get(i));
            System.out.printf("  %d -> (%d, %.0f, %.0f)%n", i + 1, n.id, n.x, n.y);
        }
        System.out.printf("总路程: %.2f%n", aco.getRouteLength(route));
    }
}
