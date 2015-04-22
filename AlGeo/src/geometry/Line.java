package geometry;

import java.util.Locale;

public class Line {
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

    private Line(Point p1, Point p2, Vector u, Vector n, Vector n0, double d) {
        super();
        this.p1 = p1;
        this.p2 = p2;

        if(u == null)
            // u = p1 - p2
            this.u = p1.toPosition().substract(p2.toPosition());
        else
            this.u = u;

        if(n == null)
            // n = (-u.y, u.x)
            this.n = new Vector(-this.u.get(1), this.u.get(0));
        else
            this.n = n;

        if(n0 == null) {
            // n0 = n/|n|
            // d = p1 * n0
            this.n0 = this.n.multiply(1.0d / this.n.length());
            this.d = p1.toPosition().dotProduct(this.n0);
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

    @Override
    public String toString() {
        return String.format(Locale.US, "Line:%n"
                + "between %s and %s%n"
                + "from %s, slope %s%n"
                + "at %s, normal %s%n"
                + "normal %s, distance %.1f",
                p1, p2, p1, u, p1, n, n0, d);
    }
}
