package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Locale;

import mats.Mat;
import mats.Mat2x2;

public class Point implements Drawable, Comparable<Point> {
    final public Mat m;

    private Point(Mat m) {
        super();
        this.m = m;
    }

    public Point(final double x, final double y) {
        this(new Mat(x, y).transpose());
    }

    public double getX() {
        return m.get(0, 0);
    }

    public double getY() {
        return m.get(0, 1);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.1f, %.1f)", m.get(0, 0), m.get(0, 1));
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        g.setColor(color);
        g.fillOval(dimensions.xToInt(getX()) - 3, dimensions.yToInt(getY()) - 3, 6, 6);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Point && Math.abs(getX() - ((Point) o).getX()) < C.E && Math.abs(getY() - ((Point) o).getY()) < C.E)
            return true;
    
        return false;
    }

    @Override
    public int compareTo(Point p) {
        if(getX() == p.getX())
            return getY() < p.getY() ? -1 : getY() == p.getY() ? 0 : 1;
    
        return getX() < p.getX() ? -1 : 1;
    }

    public Point add(Vector v) {
        return new Point(Mat.add(m, Mat.transpose(v.m)));
    }

    public Point substract(Vector v) {
        return new Point(Mat.subtract(m, Mat.transpose(v.m)));
    }

    public Vector toPosition() {
        return new Vector(Mat.transpose(m));
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
        return toPosition().dotProduct(line.n0) + line.d;
    }

    public boolean isInsideBoundingBox(LineSegment ls) {
        if(getX() < Math.min(ls.p1.getX(), ls.p2.getX()) - C.E
                || getX() > Math.max(ls.p1.getX(), ls.p2.getX()) + C.E
                || getY() < Math.min(ls.p1.getY(), ls.p2.getY()) - C.E
                || getY() > Math.max(ls.p1.getY(), ls.p2.getY()) + C.E)
            return false;

        return true;
    }

    public Point compose(Mat2x2 mat) {
        return new Point(Mat.compose(m, mat));
    }
}
