package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 遗传算法 - 个体（一条 TSP 路径编码）
 */
public class GeneticIndividual {
    public final List<Integer> genes;
    private double fitness;
    private final Random random = new Random();

    public GeneticIndividual(int n) {
        this.genes = new ArrayList<>();
        for (int i = 0; i < n; i++) genes.add(i);
        Collections.shuffle(genes);
    }

    public GeneticIndividual(List<Integer> genes) {
        this.genes = new ArrayList<>(genes);
    }

    public GeneticIndividual crossover(GeneticIndividual other, GeneticFitness fn) {
        int n = genes.size();
        int a = random.nextInt(n);
        int b = random.nextInt(n);
        if (a > b) { int t = a; a = b; b = t; }

        List<Integer> child = new ArrayList<>(Collections.nCopies(n, -1));
        for (int i = a; i <= b; i++) {
            child.set(i, genes.get(i));
        }
        int idx = (b + 1) % n;
        for (int i = 0; i < n; i++) {
            int g = other.genes.get((b + 1 + i) % n);
            if (!child.contains(g)) {
                child.set(idx, g);
                idx = (idx + 1) % n;
            }
        }
        return new GeneticIndividual(child);
    }

    public void mutate() {
        int i = random.nextInt(genes.size());
        int j = random.nextInt(genes.size());
        if (i != j) {
            int t = genes.get(i);
            genes.set(i, genes.get(j));
            genes.set(j, t);
        }
    }

    public void setFitness(double f) {
        this.fitness = f;
    }

    public double getFitness() {
        return fitness;
    }
}
