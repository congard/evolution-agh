package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.WeightedRandom;
import congard.agh.evolution.simulation.params.Params;

import java.util.Arrays;

public class Genotype {
    private final Gene[] genes;
    private int activeGeneIndex = 0;
    private final Params params;

    public Genotype(Params params) {
        this.params = params;
        genes = new Gene[params.getGenomeLength()];

        var random = WeightedRandom.getInstance();

        for (int i = 0; i < genes.length; ++i)
            genes[i] = random.next(Gene.values());

        activeGeneIndex = random.nextInt(genes.length);
    }

    private Genotype(Params params, Gene[] genes) {
        this.params = params;
        this.genes = genes;
    }

    public Gene getActive() {
        return genes[activeGeneIndex];
    }

    public int getActiveGeneIndex() {
        return activeGeneIndex;
    }

    private void activateNext() {
        ++activeGeneIndex;
        activeGeneIndex %= genes.length;
    }

    public void next() {
        var random = WeightedRandom.getInstance();

        switch (params.getBehaviourType()) {
            case PREDESTINATION -> activateNext();
            case RANDOM20 -> {
                var isRandomSelection = random.next(new Boolean[] {true, false}, new float[] {0.2f, 0.8f});

                if (isRandomSelection) {
                    activeGeneIndex = random.nextInt(genes.length);
                } else {
                    activateNext();
                }
            }
        }
    }

    public void set(int index, Gene value) {
        genes[index] = value;
    }

    public Gene get(int index) {
        return genes[index];
    }

    public int size() {
        return genes.length;
    }

    /**
     * @param left genotype
     * @param right genotype
     * @param pos position in range [0, 1]; 0 means `left`, 1 means `right`
     * @return mixed genotype created from `left` and `right`
     */
    public static Genotype mix(Genotype left, Genotype right, float pos) {
        assert left.genes.length == right.genes.length;

        var n = left.genes.length;
        var leftCount = (int) (n * pos);
        var rightCount = n - leftCount;
        var genes = new Gene[n];

        System.arraycopy(left.genes, 0, genes, 0, leftCount);
        System.arraycopy(right.genes, leftCount, genes, leftCount, rightCount);

        return new Genotype(left.params, genes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genotype genotype)) return false;
        return Arrays.equals(genes, genotype.genes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }

    @Override
    public String toString() {
        return "Genotype {" +
                "genes=" + Arrays.toString(genes) +
                ", activeGeneIndex=" + activeGeneIndex +
                '}';
    }
}
