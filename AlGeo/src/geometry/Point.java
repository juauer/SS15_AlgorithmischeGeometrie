package geometry;

import java.util.Locale;

import mats.Mat;

public class Point {
    final public Mat m;

    private Point(Mat m) {
        super();
        this.m = m;
    }

    public Point(final double x, final double y) {
        this(new Mat(x, y).transpose());
    }

    public Point add(Vector v) {
        return new Point(Mat.add(m, v.m.clone().transpose()));
    }

    public Vector toPosition() {
        return new Vector(Mat.transpose(m));
    }

    public double getX() {
        return m.get(0, 0);
    }

    public double getY() {
        return m.get(0, 1);
    }

    /**
     * For directed lines:
     * +distance, if point is located left of the line
     * -distance, if point is located the right of the line
     * 
     * @param line a line
     * @return distance between point and line
     */
    public double distanceTo(Line line) {
        return toPosition().dotProduct(line.n0) - line.d;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.1f, %.1f)", m.get(0, 0), m.get(0, 1));
    }
}
