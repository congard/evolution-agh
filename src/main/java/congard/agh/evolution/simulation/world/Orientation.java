package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.math.Point2d;

public enum Orientation {
    NORTH("N"),
    NORTH_EAST("NE"),
    EAST("E"),
    SOUTH_EAST("SE"),
    SOUTH("S"),
    SOUTH_WEST("SW"),
    WEST("W"),
    NORTH_WEST("NW");

    private final String str;

    Orientation(String str) {
        this.str = str;
    }

    /**
     * @return the next value in clock-wise order
     */
    public Orientation next() {
        return values()[(ordinal() + 1) % values().length];
    }

    /**
     * @return the previous value in clock-wise order
     */
    public Orientation previous() {
        return values()[Math.floorMod(ordinal() - 1, values().length)];
    }

    /**
     * @return value obtained from rotation clock hand `delta` times
     */
    public Orientation change(int delta) {
        return values()[Math.floorMod(ordinal() + delta, values().length)];
    }

    /**
     * @return inverse value;
     * e.g. <code>NORTH</code> -> <code>SOUTH</code>,
     * <code>NORTH_EAST</code> -> <code>SOUTH_WEST</code> etc
     */
    public Orientation invert() {
        return change(4);
    }

    /**
     * @return corresponding vector, e.g. for EAST (1, 0) will be returned
     */
    public Point2d toDeltaVector() {
        return switch (this) {
            case NORTH -> new Point2d(0, -1);
            case NORTH_EAST -> new Point2d(1, -1);
            case EAST -> new Point2d(1, 0);
            case SOUTH_EAST -> new Point2d(1, 1);
            case SOUTH -> new Point2d(0, 1);
            case SOUTH_WEST -> new Point2d(-1, 1);
            case WEST -> new Point2d(-1, 0);
            case NORTH_WEST -> new Point2d(-1, -1);
        };
    }

    @Override
    public String toString() {
        return str;
    }
}
