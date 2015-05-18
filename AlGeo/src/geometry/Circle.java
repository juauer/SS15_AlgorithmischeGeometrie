package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;

public class Circle implements Drawable {
    public Point  m;
    public double r;

    public Circle(Point m, double r) {
        this.m = m;
        this.r = r;
    }

    public static Circle create(Point p1, Point p2, Point p3) {
        if((p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p3.getX() - p1.getX()) * (p2.getY() - p1.getY()) > 0)
            return null;

        double a = p2.getX() - p1.getX();
        double b = p2.getY() - p1.getY();
        double c = p3.getX() - p1.getX();
        double d = p3.getY() - p1.getY();
        double e = a * (p1.getX() + p2.getX()) + b * (p1.getY() + p2.getY());
        double f = c * (p1.getX() + p3.getX()) + d * (p1.getY() + p3.getY());
        double g = 2.0d * (a * (p3.getY() - p2.getY()) - b * (p3.getX() - p2.getX()));

        if(g == 0.0d)
            return null;

        Point m = new Point((d * e - b * f) / g, (a * f - c * e) / g);
        return new Circle(m,
                Math.sqrt(Math.pow(p1.getX() - m.getX(), 2.0d) + Math.pow(p1.getY() - m.getY(), 2.0d)));
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        m.paint(g, dimensions, color);
        g.setColor(color);
        g.drawOval(dimensions.xToInt(m.getX() - r), dimensions.yToInt(m.getY() + r),
                (int) Math.round((r + r) * Dimensions.UNIT), (int) Math.round((r + r) * Dimensions.UNIT));
    }
}
