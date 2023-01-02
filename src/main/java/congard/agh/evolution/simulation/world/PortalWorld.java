package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.WeightedRandom;
import congard.agh.evolution.simulation.math.Rect;
import congard.agh.evolution.simulation.params.Params;

public class PortalWorld extends AbstractWorld {
    public PortalWorld(Params params) {
        super(params);
    }

    @Override
    void proposePos(Point2d pos, Animal animal) {
        var size = params.getMapSize();

        if (new Rect(new Point2d(0, 0), new Point2d(size.getX() - 1, size.getY() - 1)).contains(pos)) {
            animal.pos.setX(pos.getX());
            animal.pos.setY(pos.getY());
        } else {
            var random = WeightedRandom.getInstance();

            // teleport an animal to a random place
            animal.pos.setX(random.nextInt(size.getX()));
            animal.pos.setY(random.nextInt(size.getY()));

            // pay for teleportation
            animal.changeHealth(-params.getChildEnergyCost());
        }
    }
}
