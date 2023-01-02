package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.params.Params;

public class RoundWorld extends AbstractWorld {
    public RoundWorld(Params params) {
        super(params);
    }

    private int clamp(int x, int minVal, int maxVal) {
        return Math.min(Math.max(x, minVal), maxVal);
    }

    @Override
    void proposePos(Point2d pos, Animal animal) {
        var size = params.getMapSize();

        animal.pos.setX(Math.floorMod(pos.getX(), size.getX()));
        animal.pos.setY(clamp(pos.getY(), 0, size.getY() - 1));

        if (pos.getY() < 0 || pos.getY() >= size.getY()) {
            animal.orientation = animal.orientation.invert();
        }
    }
}
