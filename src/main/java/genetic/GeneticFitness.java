package genetic;

/**
 * 遗传算法 - 适应度函数接口
 */
@FunctionalInterface
public interface GeneticFitness {
    double evaluate(GeneticIndividual ind);
}
