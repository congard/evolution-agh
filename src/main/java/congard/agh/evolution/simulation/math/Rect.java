package congard.agh.evolution.simulation.math;

public class Rect {
    private final Point2d upperLeft;
    private final Point2d lowerRight;

    public Rect(Point2d upperLeft, Point2d lowerRight) {
        // TODO: check correctness
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    public int getWidth() {
        return lowerRight.getX() - upperLeft.getX();
    }

    public int getHeight() {
        return lowerRight.getY() - upperLeft.getY();
    }

    public Point2d getUpperLeft() {
        return upperLeft;
    }

    public Point2d getLowerRight() {
        return lowerRight;
    }

    public boolean contains(Point2d point2d) {
        var x1 = upperLeft.getX();
        var x2 = lowerRight.getX();
        var y1 = upperLeft.getY();
        var y2 = lowerRight.getY();
        var x = point2d.getX();
        var y = point2d.getY();

        return (x1 <= x && x <= x2) && (y1 <= y && y <= y2);
    }
}
