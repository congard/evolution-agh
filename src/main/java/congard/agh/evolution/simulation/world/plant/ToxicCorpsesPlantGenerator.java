package congard.agh.evolution.simulation.world.plant;

import congard.agh.evolution.simulation.WeightedRandom;
import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.world.AbstractWorld;
import congard.agh.evolution.simulation.world.WorldCell;
import congard.agh.evolution.simulation.world.WorldElementType;

import java.util.*;
import java.util.stream.IntStream;

/**
 * toksyczne trupy – rośliny preferują te pola, na których zwierzęta umierają najrzadziej –
 * rosną na tych polach, na których najmniej zwierząt skończyło swój żywot w trakcie symulacji.
 */
public class ToxicCorpsesPlantGenerator extends PlantGenerator {
    // map to speed up the calculation of probability
    private HashMap<WorldCell, Float> mapDistribution;

    public ToxicCorpsesPlantGenerator(AbstractWorld world) {
        super(world);
    }

    @Override
    public void init() {
        // just select randomly
        var mapSize = world.getParams().getMapSize();

        WeightedRandom.getInstance().nextPermutation(
                IntStream.range(0, mapSize.getX() * mapSize.getY()).boxed().toList(),
                world.getParams().getStartPlantCount()
        ).forEach(index -> {
            int x = index % mapSize.getX();
            int y = index / mapSize.getX();

            world.add(new Plant(new Point2d(x, y), world));
        });
    }

    @Override
    public void generate() {
        gen(world.getParams().getPlantsPerDay());
    }

    @Override
    protected float getProbability(WorldCell cell) {
        return mapDistribution.get(cell);
    }

    @Override
    protected Distribution createDistribution() {
        var cells = new ArrayList<WorldCell>(); // ArrayList to decrease shuffle complexity
        var weights = new LinkedList<Float>();

        world.getElements().forEach(row -> row.forEach(cell -> {
            if (cell.get(WorldElementType.PLANT).isEmpty()) {
                cells.add(cell);
            }
        }));

        cells.sort(Comparator.comparingInt(WorldCell::getNumberOfDeaths));

        // shuffle cells with the same number of deaths

        var random = WeightedRandom.getInstance();
        int startIndex = 0;

        for (int i = 1; i < cells.size(); ++i) {
            int startNod = cells.get(startIndex).getNumberOfDeaths();
            int currentNod = cells.get(i).getNumberOfDeaths();

            if (startNod != currentNod) {
                random.shuffle(cells.subList(startIndex, i));
                startIndex = i;
            }
        }

        random.shuffle(cells.subList(startIndex, cells.size()));

        // According to Pareto's principle, only first 20% have 80% probability
        int prefCount = (int) (cells.size() * 0.2f);

        mapDistribution = new HashMap<>();

        for (int index = 0; index < cells.size(); ++index) {
            float probability = index < prefCount ? 0.8f : 0.2f;
            weights.add(probability);
            mapDistribution.put(cells.get(index), probability);
        }

        WeightedRandom.normalizeWeights(weights);

        return new Distribution(cells, weights);
    }
}
