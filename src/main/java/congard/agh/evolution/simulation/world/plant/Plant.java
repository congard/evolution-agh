package congard.agh.evolution.simulation.world.plant;

import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.world.AbstractWorld;
import congard.agh.evolution.simulation.world.WorldAbstractElement;
import congard.agh.evolution.simulation.world.WorldElementType;

public class Plant extends WorldAbstractElement {
    public Plant(Point2d pos, AbstractWorld world) {
        super(WorldElementType.PLANT, pos, world);
    }

    public int getNutritionalValue() {
        return world.getParams().getPlantNutritionalValue();
    }
}
