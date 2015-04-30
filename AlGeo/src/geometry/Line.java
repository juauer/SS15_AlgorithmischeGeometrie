package geometry;

import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Locale;

import mats.Mat2x2;

public class Line implements Drawable {
    /**
     * Any point on the line
     */
    final public Point  p1;

    /**
     * A point on the line, other than p1. In case of a directed line, the line
     * is directed from p1 to p2
     */
    final public Point  p2;

    /**
     * Slope of the line
     */
    final public Vector u;

    /**
     * A normal vector, orthogonal to the line
     */
    final public Vector n;

    /**
     * The normalized normal vector
     */
    final public Vector n0;

    /**
     * Distance between line and the coordinate systems center
     */
    final public double d;

    protected Line(Point p1, Point p2, Vector u, Vector n, Vector n0, double d) {
        super();
        this.p1 = p1;
        this.p2 = p2;

        if(u == null)
            // u = p2 - p1
            this.u = p2.toPosition().substract(p1.toPosition()).normalize();
        else
            this.u = u.normalize();

        if(n == null)
            // n = (-u.y, u.x)
            this.n = new Vector(-this.u.get(1), this.u.get(0));
        else
            this.n = n;

        if(n0 == null) {
            // n0 = n/|n|
            // d = - p1 * n0
            this.n0 = this.n.normalize();
            this.d = -p1.toPosition().dotProduct(this.n0);
        }
        else {
            this.n0 = n0;
            this.d = d;
        }
    }

    public Line(Vector n0, double d) {
        this(n0.get(0) == 0.0d ?
                new Point(0.0d, d) :
                new Point(d / n0.get(0), 0.0d),
                n0.get(0) == 0.0d ?
                        new Point(1.0d, d) :
                        new Point((d - n0.get(1)) / n0.get(0), 1.0d), null, null, n0, d);
    }

    public Line(Vector n, Point p) {
        this(p, n.get(0) == 0.0d ?
                new Point(p.getX() + 1, p.getY()) :
                new Point(p.getX() - n.get(1) / n.get(0), p.getY() + 1),
                null, n, null, 0.0d);
    }

    public Line(Point p, Vector u) {
        this(p, p.add(u), u, null, null, 0.0d);
    }

    public Line(Point p1, Point p2) {
        this(p1, p2, null, null, null, 0.0d);
    }

    public boolean isParallelTo(Line line) {
        return 1.0d - Math.abs(u.dotProduct(line.u)) < C.E;
    }

    public Point getIntersection(Line line) {
        if(isParallelTo(line))
            return null;

        double d1 = p2.getX() - p1.getX();
        double d2 = p2.getY() - p1.getY();
        double d3 = line.p2.getX() - line.p1.getX();
        double d4 = line.p2.getY() - line.p1.getY();
        double d5 = p2.getX() * p1.getY() - p1.getX() * p2.getY();
        double d6 = line.p2.getX() * line.p1.getY() - line.p1.getX() * line.p2.getY();
        double d7 = d1 * d4 - d3 * d2;
        Point is = new Point((d3 * d5 - d1 * d6) / d7,
                (d5 * d4 - d6 * d2) / d7);

        if(line instanceof LineSegment && !is.isInsideBoundingBox((LineSegment) line))
            return null;

        return is;
    }

    /**
     * Compute the angle between this and another line. Result is positive or
     * negative just like an atan2 were used.
     * 
     * @param line A line with the direction this line would have if rotated
     *            anticlockwise by the resulting angle
     * @return Angle, in radians
     */
    public double angleTo(Line line) {
        return Math.atan2(line.u.get(1), line.u.get(0)) - Math.atan2(u.get(1), u.get(0));
    }

    public Line rotate(Point point, double angle) {
        Vector pos = point.toPosition();
        double cosa = Math.cos(angle);
        double sina = Math.sin(angle);
        Mat2x2 mat = new Mat2x2(cosa, -sina, sina, cosa);
        return new Line(p1.add(pos.multiply(-1)).compose(mat).add(pos),
                p2.add(pos.multiply(-1)).compose(mat).add(pos));
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Line:%n"
                + "between %s and %s%n"
                + "from %s, slope %s%n"
                + "at %s, normal %s%n"
                + "normal %s, distance %.1f",
                p1, p2, p1, u, p1, n, n0, d);
    }

    @Override
    public void paint(Graphics g, Color color) {
        Point p1 = null;
        Point p2 = null;

        for(LineSegment l : new LineSegment[] {
                new LineSegment(new Point(0.0d, 0.0d), new Point(Drawable.RANGE_X, 0.0d)),
                new LineSegment(new Point(0.0d, 0.0d), new Point(0.0d, Drawable.RANGE_Y)),
                new LineSegment(new Point(0.0d, Drawable.RANGE_Y), new Point(Drawable.RANGE_X, Drawable.RANGE_Y)),
                new LineSegment(new Point(Drawable.RANGE_X, 0.0d), new Point(Drawable.RANGE_X, Drawable.RANGE_Y)) }) {
            Point p = getIntersection(l);

            if(p != null)
                if(p1 == null)
                    p1 = p;
                else if(p1.add(p.toPosition().multiply(-1.0d)).toPosition().length() > C.E) {
                    p2 = p;
                    break;
                }
        }

        new LineSegment(this.p1, this.p1.add(u.multiply(-1.0d))).rotate(this.p1, Math.PI / 4.0d).paint(g, color);
        new LineSegment(this.p1, this.p1.add(u.multiply(-1.0d))).rotate(this.p1, Math.PI / -4.0d).paint(g, color);

        if(p1 != null && p2 != null)
            new LineSegment(p1, p2).paint(g, color);
    }
}
