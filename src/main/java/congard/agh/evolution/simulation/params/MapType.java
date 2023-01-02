package congard.agh.evolution.simulation.params;

public enum MapType {
    ROUND("Round"),
    PORTAL("Portal");

    private final String s;

    MapType(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
