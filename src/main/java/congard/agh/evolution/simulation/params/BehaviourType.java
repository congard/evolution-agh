package congard.agh.evolution.simulation.params;

public enum BehaviourType {
    PREDESTINATION("Predestination"),
    RANDOM20("Random20");

    private final String s;

    BehaviourType(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
