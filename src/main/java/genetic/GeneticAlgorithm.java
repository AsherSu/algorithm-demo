package genetic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 遗传算法 - 用于 TSP/VRP 等组合优化
 */
public class GeneticAlgorithm {
    private final int popSize;
    private final int generations;
    private final double mutateRate;
    private final GeneticFitness fitnessFn;
    private final Random random = new Random();

    public GeneticAlgorithm(int popSize, int generations, double mutateRate, GeneticFitness fitnessFn) {
        this.popSize = popSize;
        this.generations = generations;
        this.mutateRate = mutateRate;
        this.fitnessFn = fitnessFn;
    }

    public GeneticIndividual solve(int geneLength) {
        List<GeneticIndividual> pop = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            GeneticIndividual ind = new GeneticIndividual(geneLength);
            ind.setFitness(fitnessFn.evaluate(ind));
            pop.add(ind);
        }

        for (int g = 0; g < generations; g++) {
            pop.sort(Comparator.comparingDouble(GeneticIndividual::getFitness).reversed());

            List<GeneticIndividual> next = new ArrayList<>();
            int elite = popSize / 5;
            for (int i = 0; i < elite; i++) {
                next.add(pop.get(i));
            }

            while (next.size() < popSize) {
                GeneticIndividual p1 = select(pop);
                GeneticIndividual p2 = select(pop);
                GeneticIndividual child = p1.crossover(p2, fitnessFn);
                if (random.nextDouble() < mutateRate) {
                    child.mutate();
                }
                child.setFitness(fitnessFn.evaluate(child));
                next.add(child);
            }
            pop = next;
        }

        pop.sort(Comparator.comparingDouble(GeneticIndividual::getFitness).reversed());
        return pop.get(0);
    }

    private GeneticIndividual select(List<GeneticIndividual> pop) {
        double total = 0;
        for (GeneticIndividual ind : pop) {
            total += Math.max(0, ind.getFitness()) + 1;
        }
        double r = random.nextDouble() * total;
        for (GeneticIndividual ind : pop) {
            r -= Math.max(0, ind.getFitness()) + 1;
            if (r <= 0) return ind;
        }
        return pop.get(pop.size() - 1);
    }
}
