package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Locale;

public class LineSegment extends Line implements Drawable {
    public LineSegment(Point p1, Point p2) {
        super(p1, p2);
    }

    @Override
    public boolean isInsideBoundingBox(Point point) {
        if(point.getX() < Math.min(p1.getX(), p2.getX()) - C.E
                || point.getX() > Math.max(p1.getX(), p2.getX()) + C.E
                || point.getY() < Math.min(p1.getY(), p2.getY()) - C.E
                || point.getY() > Math.max(p1.getY(), p2.getY()) + C.E)
            return false;

        return true;
    }

    @Override
    public LineSegment rotate(Point point, double angle) {
        Line l = super.rotate(point, angle);
        return new LineSegment(l.p1, l.p2);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "LineSegment: %s -> %s", p1, p2);
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        g.setColor(color);
        g.drawLine(dimensions.xToInt(p1.getX()), dimensions.yToInt(p1.getY()),
                dimensions.xToInt(p2.getX()), dimensions.yToInt(p2.getY()));
    }
}
