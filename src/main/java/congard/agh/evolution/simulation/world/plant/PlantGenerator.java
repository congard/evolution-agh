package congard.agh.evolution.simulation.world.plant;

import congard.agh.evolution.simulation.WeightedRandom;
import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.world.AbstractWorld;
import congard.agh.evolution.simulation.world.WorldCell;

import java.util.LinkedList;
import java.util.List;

public abstract class PlantGenerator {
    protected static class Distribution {
        List<WorldCell> cells;
        List<Float> weights;

        Distribution() {
            cells = new LinkedList<>();
            weights = new LinkedList<>();
        }

        Distribution(List<WorldCell> cells, List<Float> weights) {
            this.cells = cells;
            this.weights = weights;
        }
    }

    protected final AbstractWorld world;

    public PlantGenerator(AbstractWorld world) {
        this.world = world;
    }

    public abstract void init();
    public abstract void generate();

    /**
     * Note: must be called after `createDistribution`
     * @param cell to calculate probability for
     * @return probability in range [0, 1]
     */
    protected abstract float getProbability(WorldCell cell);

    /**
     * @return distribution based on generator's type;
     * weights are already normalized
     */
    protected abstract Distribution createDistribution();

    protected void gen(int count, boolean ignoreGrowProbability) {
        var random = WeightedRandom.getInstance();
        var dist = createDistribution();
        var n = Math.min(count, dist.cells.size());
        var cells = WeightedRandom.getInstance().select(dist.cells, dist.weights, n);

        for (var cell : cells) {
            if (ignoreGrowProbability || random.nextBoolean(getProbability(cell))) {
                cell.add(new Plant(new Point2d(cell.getPos()), world));
            }
        }
    }

    protected void gen(int count) {
        gen(count, false);
    }
}
