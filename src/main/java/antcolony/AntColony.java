package antcolony;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 蚁群算法 (ACO) - 求解 TSP
 * 模拟蚂蚁觅食，通过信息素引导寻优
 */
public class AntColony {
    // 所有城市节点的列表
    private final List<AntColonyNode> nodes;
    // 信息素矩阵，pheromone[i][j] 表示从城市 i 到城市 j 的信息素浓度
    private final double[][] pheromone;
    // 距离矩阵，distance[i][j] 表示从城市 i 到城市 j 的距离
    private final double[][] distance;
    // 城市数量
    private final int n;
    // 信息素重要程度参数，alpha 越大越倾向于选择信息素浓度高的路径
    private final double alpha;
    // 启发函数重要程度参数，beta 越大越倾向于选择距离较短的路径
    private final double beta;
    // 信息素挥发率，rho 越大信息素挥发越快，避免过早收敛
    private final double rho;
    // 信息素总量常数，q 越大每条路径增加的信息素越多，促进更快收敛
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
        // 最优路径
        List<Integer> bestRoute = null;
        // 最优路径长度
        double bestLength = Double.MAX_VALUE;

        // 迭代指定次数
        for (int iter = 0; iter < iterations; iter++) {
            List<List<Integer>> routes = new ArrayList<>();
            
            // 让每只蚂蚁构建一条路径
            for (int a = 0; a < antCount; a++) {
                List<Integer> route = runAnt();
                routes.add(route);
                
                // 计算路径长度并更新至今找到的最优解
                double len = routeLength(route);
                if (len < bestLength) {
                    bestLength = len;
                    bestRoute = new ArrayList<>(route);
                }
            }

            // 信息素挥发：降低所有路径上的信息素浓度
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    pheromone[i][j] *= (1 - rho);
                }
            }

            // 信息素增强：在蚂蚁经过的路径上增加信息素
            for (List<Integer> route : routes) {
                double delta = q / routeLength(route);
                for (int i = 0; i < route.size(); i++) {
                    int a = route.get(i);
                    int b = route.get((i + 1) % route.size());
                    // 更新双向路径的信息素（假设是对称 TSP 问题）
                    pheromone[a][b] += delta;
                    pheromone[b][a] += delta;
                }
            }
        }

        return bestRoute != null ? bestRoute : new ArrayList<>();
    }

    /**
     * 模拟单只蚂蚁构建路径的过程
     * @return 蚂蚁走过的城市路径（城市索引列表）
     */
    private List<Integer> runAnt() {
        // 存储当前蚂蚁构建的路径
        List<Integer> route = new ArrayList<>();
        // 记录已访问的城市，避免重复访问
        boolean[] visited = new boolean[n];

        // 随机选择一个起始城市
        int start = random.nextInt(n);
        route.add(start);
        visited[start] = true;

        // 依次选择剩余的 n-1 个城市
        for (int step = 1; step < n; step++) {
            // 获取当前所在城市
            int cur = route.get(route.size() - 1);
            // 根据信息素和启发式信息选择下一个城市
            int next = selectNext(cur, visited);
            route.add(next);
            visited[next] = true;
        }
        return route;
    }

    /**
     * 根据信息素浓度和启发式信息选择下一个城市
     * @param cur 当前所在城市索引
     * @param visited 记录已访问城市的数组
     * @return 下一个城市的索引
     */
    private int selectNext(int cur, boolean[] visited) {
        // 存储每个未访问城市的转移概率
        double[] prob = new double[n];
        // 所有未访问城市的概率总和
        double sum = 0;
        
        // 计算每个未访问城市的转移概率
        for (int j = 0; j < n; j++) {
            if (!visited[j]) {
                // 启发式信息：距离的倒数，避免除零添加微小值
                double eta = 1.0 / (distance[cur][j] + 1e-6);
                // 转移概率 = (信息素^alpha) * (启发式信息^beta)
                prob[j] = Math.pow(pheromone[cur][j], alpha) * Math.pow(eta, beta);
                sum += prob[j];
            }
        }
        
        // 生成一个随机数用于轮盘赌选择
        double r = random.nextDouble() * sum;
        
        // 轮盘赌选择下一个城市
        for (int j = 0; j < n; j++) {
            if (!visited[j]) {
                r -= prob[j];
                if (r <= 0) return j;
            }
        }
        
        // 如果因浮点精度问题未选中，返回第一个未访问城市
        for (int j = 0; j < n; j++) {
            if (!visited[j]) return j;
        }
        
        // 理论上不会执行到这里
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
