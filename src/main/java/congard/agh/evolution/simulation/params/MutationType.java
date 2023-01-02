package congard.agh.evolution.simulation.params;

public enum MutationType {
    RANDOM("Random"),
    CORRECTION("Correction");

    private final String s;

    MutationType(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
