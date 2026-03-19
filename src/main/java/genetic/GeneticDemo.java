package genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * 遗传算法演示 - 求解 TSP
 */
public class GeneticDemo {
    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
        double dist(Point o) {
            return Math.sqrt((x - o.x) * (x - o.x) + (y - o.y) * (y - o.y));
        }
    }

    public static void main(String[] args) {
        List<Point> pts = new ArrayList<>();
        pts.add(new Point(0, 0));
        pts.add(new Point(2, 1));
        pts.add(new Point(4, 0));
        pts.add(new Point(3, 3));
        pts.add(new Point(1, 4));
        pts.add(new Point(5, 2));

        GeneticFitness fn = ind -> {
            double sum = 0;
            for (int i = 0; i < ind.genes.size(); i++) {
                int a = ind.genes.get(i);
                int b = ind.genes.get((i + 1) % ind.genes.size());
                sum += pts.get(a).dist(pts.get(b));
            }
            return 100.0 / (1.0 + sum);
        };

        GeneticAlgorithm ga = new GeneticAlgorithm(100, 200, 0.2, fn);
        GeneticIndividual best = ga.solve(pts.size());

        System.out.println("遗传算法 TSP 结果:");
        double total = 0;
        for (int i = 0; i < best.genes.size(); i++) {
            int a = best.genes.get(i);
            int b = best.genes.get((i + 1) % best.genes.size());
            total += pts.get(a).dist(pts.get(b));
            System.out.printf("  %d -> (%.0f,%.0f)%n", i + 1, pts.get(a).x, pts.get(a).y);
        }
        System.out.printf("总路程: %.2f%n", total);
    }
}
