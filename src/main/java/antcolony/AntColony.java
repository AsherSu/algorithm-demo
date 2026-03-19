package antcolony;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 蚁群算法 (ACO) - 求解 TSP
 * 模拟蚂蚁觅食，通过信息素引导寻优
 */
public class AntColony {
    private final List<AntColonyNode> nodes;
    private final double[][] pheromone;
    private final double[][] distance;
    private final int n;
    private final double alpha;
    private final double beta;
    private final double rho;
    private final double q;
    private final Random random = new Random();

    public AntColony(List<AntColonyNode> nodes) {
        this.nodes = nodes;
        this.n = nodes.size();
        this.pheromone = new double[n][n];
        this.distance = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    distance[i][j] = nodes.get(i).distanceTo(nodes.get(j));
                    pheromone[i][j] = 0.1;
                }
            }
        }
        this.alpha = 1.0;
        this.beta = 5.0;
        this.rho = 0.5;
        this.q = 100.0;
    }

    public List<Integer> solve(int antCount, int iterations) {
        List<Integer> bestRoute = null;
        double bestLength = Double.MAX_VALUE;

        for (int iter = 0; iter < iterations; iter++) {
            List<List<Integer>> routes = new ArrayList<>();
            for (int a = 0; a < antCount; a++) {
                List<Integer> route = runAnt();
                routes.add(route);
                double len = routeLength(route);
                if (len < bestLength) {
                    bestLength = len;
                    bestRoute = new ArrayList<>(route);
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    pheromone[i][j] *= (1 - rho);
                }
            }

            for (List<Integer> route : routes) {
                double delta = q / routeLength(route);
                for (int i = 0; i < route.size(); i++) {
                    int a = route.get(i);
                    int b = route.get((i + 1) % route.size());
                    pheromone[a][b] += delta;
                    pheromone[b][a] += delta;
                }
            }
        }

        return bestRoute != null ? bestRoute : new ArrayList<>();
    }

    private List<Integer> runAnt() {
        List<Integer> route = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int start = random.nextInt(n);
        route.add(start);
        visited[start] = true;

        for (int step = 1; step < n; step++) {
            int cur = route.get(route.size() - 1);
            int next = selectNext(cur, visited);
            route.add(next);
            visited[next] = true;
        }
        return route;
    }

    private int selectNext(int cur, boolean[] visited) {
        double[] prob = new double[n];
        double sum = 0;
        for (int j = 0; j < n; j++) {
            if (!visited[j]) {
                double eta = 1.0 / (distance[cur][j] + 1e-6);
                prob[j] = Math.pow(pheromone[cur][j], alpha) * Math.pow(eta, beta);
                sum += prob[j];
            }
        }
        double r = random.nextDouble() * sum;
        for (int j = 0; j < n; j++) {
            if (!visited[j]) {
                r -= prob[j];
                if (r <= 0) return j;
            }
        }
        for (int j = 0; j < n; j++) {
            if (!visited[j]) return j;
        }
        return -1;
    }

    private double routeLength(List<Integer> route) {
        double len = 0;
        for (int i = 0; i < route.size(); i++) {
            int a = route.get(i);
            int b = route.get((i + 1) % route.size());
            len += distance[a][b];
        }
        return len;
    }

    public double getRouteLength(List<Integer> route) {
        return routeLength(route);
    }
}
