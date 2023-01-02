package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.math.Point2d;

public abstract class WorldAbstractElement {
    private final WorldElementType type;
    protected final Point2d pos;
    protected final AbstractWorld world;

    protected WorldAbstractElement(WorldElementType type, Point2d pos, AbstractWorld world) {
        this.pos = pos;
        this.type = type;
        this.world = world;
    }

    public void remove() {
        world.remove(this);
    }

    public WorldElementType getType() {
        return type;
    }

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }
}
