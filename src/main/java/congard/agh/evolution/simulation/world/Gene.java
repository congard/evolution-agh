package congard.agh.evolution.simulation.world;

public enum Gene {
    G0,
    G1,
    G2,
    G3,
    G4,
    G5,
    G6,
    G7;

    public int getDeltaOrientation() {
        return ordinal();
    }

    public static Gene fromIndex(int index) {
        return values()[index];
    }
}
