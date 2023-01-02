package congard.agh.evolution.simulation.math;

import org.json.JSONObject;

import java.util.Objects;

public class Point2d {
    private int x;
    private int y;

    public Point2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2d(Point2d point2d) {
        this(point2d.getX(), point2d.getY());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return vector created from <code>this</code> and <code>other</code> by adding their coords
     */
    public Point2d add(Point2d other) {
        return new Point2d(x + other.x, y + other.y);
    }

    /**
     * @return vector created from <code>this</code> and <code>other</code> by subtracting from their coords
     * <br>I.e. this - other
     */
    public Point2d subtract(Point2d other) {
        return new Point2d(x - other.x, y - other.y);
    }

    public Point2d opposite() {
        return new Point2d(-x, -y);
    }

    public static Point2d deserialize(JSONObject obj) {
        return new Point2d(obj.getInt("x"), obj.getInt("y"));
    }

    @Override
    public boolean equals(Object other) {
        // compare references
        if (this == other)
            return true;

        // objects of different types cannot be equal
        if (!(other instanceof Point2d point2D))
            return false;

        return x == point2D.x && y == point2D.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
