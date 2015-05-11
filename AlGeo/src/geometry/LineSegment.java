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
    public Point intersectionWith(Line line) {
        Point is = super.intersectionWith(line);

        if(is == null || !is.isInsideBoundingBox(this))
            return null;

        return is;
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
