package congard.agh.evolution.simulation.world.plant;

import congard.agh.evolution.simulation.WeightedRandom;
import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.math.Rect;
import congard.agh.evolution.simulation.world.AbstractWorld;
import congard.agh.evolution.simulation.world.WorldCell;
import congard.agh.evolution.simulation.world.WorldElementType;

/**
 * zalesione równiki – preferowany przez rośliny jest poziomy
 * pas pól w centralnej części mapy (udający równik i okolice)
 */
public class ForestedEquatorsGenerator extends PlantGenerator {
    private Rect prefRect;

    public ForestedEquatorsGenerator(AbstractWorld world) {
        super(world);
    }

    @Override
    public void init() {
        // According to Pareto's principle, Sp / S = 0.2
        var mapSize = world.getParams().getMapSize();
        var heightPref = (int) (mapSize.getY() * 0.2f);

        prefRect = new Rect(
            new Point2d(0, mapSize.getY() / 2 - heightPref / 2),
            new Point2d(mapSize.getX(), mapSize.getY() / 2 + heightPref / 2)
        );

        gen(world.getParams().getStartPlantCount(), true);
    }

    @Override
    public void generate() {
        gen(world.getParams().getPlantsPerDay());
    }

    @Override
    protected float getProbability(WorldCell cell) {
        return prefRect.contains(cell.getPos()) ? 0.8f : 0.2f;
    }

    @Override
    protected Distribution createDistribution() {
        var dist = new Distribution();

        world.getElements().forEach(row -> row.forEach(cell -> {
            if (cell.get(WorldElementType.PLANT).isEmpty()) {
                dist.cells.add(cell);
                dist.weights.add(getProbability(cell));
            }
        }));

        WeightedRandom.normalizeWeights(dist.weights);

        return dist;
    }
}
