package congard.agh.evolution.simulation.params;

public enum PlantGrowType {
    FORESTED_EQUATORS("Forested equators"),
    TOXIC_CORPSES("Toxis corpses");

    private final String s;

    PlantGrowType(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
