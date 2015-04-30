package geometry;

import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Locale;

import mats.Mat;
import mats.Mat2x2;

public class Point implements Drawable {
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
        return toPosition().dotProduct(line.n0) + line.d;
    }

    public boolean isInsideBoundingBox(LineSegment ls) {
        if(getX() < Math.min(ls.p1.getX(), ls.p2.getX())
                || getX() > Math.max(ls.p1.getX(), ls.p2.getX())
                || getY() < Math.min(ls.p1.getY(), ls.p2.getY())
                || getY() > Math.max(ls.p1.getY(), ls.p2.getY()))
            return false;

        return true;
    }

    public Point compose(Mat2x2 mat) {
        return new Point(Mat.compose(m, mat));
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.1f, %.1f)", m.get(0, 0), m.get(0, 1));
    }

    @Override
    public void paint(Graphics g, Color color) {
        g.setColor(color);
        g.fillOval(Drawable.xToInt(getX()) - 3, Drawable.yToInt(getY()) - 3, 6, 6);
    }
}
