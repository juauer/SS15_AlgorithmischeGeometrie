package geometry;

import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Locale;

public class LineSegment extends Line implements Drawable {
    public LineSegment(Point p1, Point p2) {
        super(p1, p2);
    }

    @Override
    public Point getIntersection(Line line) {
        Point is = super.getIntersection(line);

        if(is == null || !is.isInsideBoundingBox(this))
            return null;

        return is;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "LineSegment: %s -> %s", p1, p2);
    }

    @Override
    public void paint(Graphics g, Color color) {
        g.setColor(color);
        g.drawLine(Drawable.xToInt(p1.getX()), Drawable.yToInt(p1.getY()),
                Drawable.xToInt(p2.getX()), Drawable.yToInt(p2.getY()));
    }
}
