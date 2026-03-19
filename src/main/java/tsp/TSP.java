package tsp;

import java.util.ArrayList;
import java.util.List;

/**
 * TSP 旅行商问题 - 最近邻启发式 + 2-opt 优化
 * 单机访问所有点并返回起点，总路程最短
 */
public class TSP {
    private final List<TSPPoint> points;
    private final int n;

    public TSP(List<TSPPoint> points) {
        this.points = new ArrayList<>(points);
        this.n = points.size();
    }

    /**
     * 求解：返回访问顺序（含起点首尾）
     */
    public List<Integer> solve() {
        if (n == 0) return new ArrayList<>();
        if (n == 1) {
            List<Integer> r = new ArrayList<>();
            r.add(0);
            return r;
        }

        List<Integer> route = nearestNeighbor();
        route = twoOpt(route);
        return route;
    }

    private List<Integer> nearestNeighbor() {
        boolean[] used = new boolean[n];
        List<Integer> route = new ArrayList<>();
        route.add(0);
        used[0] = true;

        for (int i = 1; i < n; i++) {
            int last = route.get(route.size() - 1);
            int next = -1;
            double minD = Double.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (!used[j]) {
                    double d = points.get(last).distanceTo(points.get(j));
                    if (d < minD) {
                        minD = d;
                        next = j;
                    }
                }
            }
            route.add(next);
            used[next] = true;
        }
        return route;
    }

    private List<Integer> twoOpt(List<Integer> route) {
        List<Integer> r = new ArrayList<>(route);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 2; j < n; j++) {
                    int a = r.get(i), b = r.get((i + 1) % n);
                    int c = r.get(j), d = r.get((j + 1) % n);
                    double before = points.get(a).distanceTo(points.get(b))
                            + points.get(c).distanceTo(points.get(d));
                    double after = points.get(a).distanceTo(points.get(c))
                            + points.get(b).distanceTo(points.get(d));
                    if (after < before) {
                        reverse(r, i + 1, j);
                        improved = true;
                    }
                }
            }
        }
        return r;
    }

    private void reverse(List<Integer> list, int i, int j) {
        while (i < j) {
            int t = list.get(i);
            list.set(i, list.get(j));
            list.set(j, t);
            i++;
            j--;
        }
    }

    public double totalDistance(List<Integer> route) {
        double sum = 0;
        for (int i = 0; i < route.size(); i++) {
            int a = route.get(i);
            int b = route.get((i + 1) % route.size());
            sum += points.get(a).distanceTo(points.get(b));
        }
        return sum;
    }
}
