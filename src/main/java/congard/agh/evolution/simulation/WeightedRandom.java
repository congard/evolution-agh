package congard.agh.evolution.simulation;

import java.util.*;

public class WeightedRandom {
    private static WeightedRandom instance = null;

    private final Random random = new Random();

    /**
     * @param values array to choose from
     * @param weights probabilities array; note, that sum(weights) must be equal to 1
     * @return element from the `values` array
     * @param <T> element type
     */
    public <T> T next(T[] values, float[] weights) {
        assert values.length == weights.length;

        float val = random.nextFloat();
        float pos = 0;

        for (int i = 0; i < values.length; ++i) {
            pos += weights[i];

            if (val <= pos) {
                return values[i];
            }
        }

        throw new RuntimeException("Invalid data: sum(weights) is less than 1");
    }

    /**
     *
     * @param values array to choose from
     * @return element from the `values` array with uniformly distributed probability
     * @param <T> element type
     */
    @SafeVarargs
    public final <T> T next(T... values) {
        return values[random.nextInt(values.length)];
    }

    public <T> List<T> select(List<T> srcValues, List<Float> srcWeights, int n) {
        assert srcValues.size() == srcWeights.size();
        assert srcValues.size() <= n;

        List<T> values = new LinkedList<>(srcValues);
        List<Float> weights = new LinkedList<>(srcWeights);

        List<T> result = new LinkedList<>();

        for (int j = 0; j < n; ++j) {
            float val = random.nextFloat();
            float pos = 0;

            var valuesIt = values.listIterator();
            var weightsIt = weights.listIterator();

            while (valuesIt.hasNext()) {
                var value = valuesIt.next();
                var weight = weightsIt.next();

                pos += weight;

                if (val <= pos) {
                    result.add(value);
                    valuesIt.remove();
                    weightsIt.remove();
                    normalizeWeights(weights);
                    break;
                }
            }
        }

        return result;
    }

    public boolean nextBoolean(float trueProbability) {
        return next(new Boolean[] {true, false}, new float[] {trueProbability, 1 - trueProbability});
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public int nextInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    public <T> List<T> nextPermutation(List<T> values, int n) {
        if (n > values.size())
            throw new IllegalArgumentException("Size of permutation cannot be bigger than size of input values");

        var src = new ArrayList<>(values);
        Collections.shuffle(src, random);
        src.subList(n, values.size()).clear();

        return src;
    }

    public void shuffle(List<?> list) {
        Collections.shuffle(list, random);
    }

    public static void normalizeWeights(List<Float> weights) {
        var sum = weights.stream().reduce(0.0f, Float::sum);
        var it = weights.listIterator();

        while (it.hasNext()) {
            var f = it.next();
            it.set(f / sum);
        }
    }

    public static WeightedRandom getInstance() {
        if (instance == null)
            instance = new WeightedRandom();
        return instance;
    }
}
