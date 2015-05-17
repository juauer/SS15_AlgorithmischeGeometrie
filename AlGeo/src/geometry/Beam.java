package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Locale;

public class Beam extends Line implements Drawable {
    public Beam(Point p1, Point p2) {
        super(p1, p2);
    }

    @Override
    public Point intersectionWith(Line line) {
        Point is = super.intersectionWith(line);

        // TODO

        return is;
    }

    @Override
    public Beam rotate(Point point, double angle) {
        Line l = super.rotate(point, angle);
        return new Beam(l.p1, l.p2);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Beam: %s -> %s -> oo", p1, p2);
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        g.setColor(color);
        Point farPoint = p2.add(u.multiply(Math.max(dimensions.range_x, dimensions.range_y)));
        g.drawLine(dimensions.xToInt(p1.getX()), dimensions.yToInt(p1.getY()),
                dimensions.xToInt(farPoint.getX()), dimensions.yToInt(farPoint.getY()));
    }
}
