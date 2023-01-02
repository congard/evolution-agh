package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.math.Point2d;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorldCell {
    private final Map<WorldElementType, List<WorldAbstractElement>> elements = new HashMap<>();

    private final Point2d pos;

    private int numberOfDeaths = 0;

    public WorldCell(int x, int y) {
        pos = new Point2d(x, y);

        for (var type : WorldElementType.values()) {
            elements.put(type, new LinkedList<>());
        }
    }

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }

    public Point2d getPos() {
        return pos;
    }

    public int getNumberOfDeaths() {
        return numberOfDeaths;
    }

    public void setNumberOfDeaths(int numberOfDeaths) {
        this.numberOfDeaths = numberOfDeaths;
    }

    protected void changeNumberOfDeaths(int delta) {
        numberOfDeaths += delta;
    }

    public List<WorldAbstractElement> get(WorldElementType type) {
        return elements.get(type);
    }

    public void add(WorldAbstractElement element) {
        elements.get(element.getType()).add(element);
    }

    public void remove(WorldAbstractElement element) {
        elements.get(element.getType()).remove(element);
    }

    public boolean isEmpty() {
        for (var type : WorldElementType.values()) {
            if (!elements.get(type).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
