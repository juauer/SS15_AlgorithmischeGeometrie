package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;

public class Parabola implements Drawable {
    private static final double STEPSIZE_DRAWING = 0.2d;
    public double               a, d, e;

    public Parabola(double a, double d, double e) {
        this.a = a;
        this.d = d;
        this.e = e;
    }

    public Parabola(Point point, double distance) {
        a = 0.5d / distance;
        d = -point.getX();
        e = point.getY() - distance / 2.0d;
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        g.setColor(color);
        double y1 = a * d * d + e;

        for(double x = STEPSIZE_DRAWING; x <= dimensions.range_x; x += STEPSIZE_DRAWING) {
            double f = x + d;
            double y2 = a * f * f + e;

            if(y1 <= dimensions.range_y && y1 >= 0 && y2 <= dimensions.range_y && y2 >= 0)
                g.drawLine(dimensions.xToInt(x - STEPSIZE_DRAWING), dimensions.yToInt(y1), dimensions.xToInt(x), dimensions.yToInt(y2));

            y1 = y2;
        }
    }

    public static Point leftIntersection(Parabola parabola1, Parabola parabola2) {
        double p = (2.0d * parabola1.a * parabola1.d - 2.0d * parabola2.a * parabola2.d) / (2.0d * (parabola2.a - parabola1.a));
        double q = (parabola1.a * parabola1.d * parabola1.d - parabola2.a * parabola2.d * parabola2.d + parabola1.e - parabola2.e) / (parabola1.a - parabola2.a);
        double x = p - Math.sqrt(p * p - q);
        double f = x + parabola1.d;
        double y = parabola1.a * f * f + parabola1.e;
        return new Point(x, y);
    }

    public static Point rightIntersection(Parabola parabola1, Parabola parabola2) {
        double p = (2.0d * parabola1.a * parabola1.d - 2.0d * parabola2.a * parabola2.d) / (2.0d * (parabola2.a - parabola1.a));
        double q = (parabola1.a * parabola1.d * parabola1.d - parabola2.a * parabola2.d * parabola2.d + parabola1.e - parabola2.e) / (parabola1.a - parabola2.a);
        double x = p + Math.sqrt(p * p - q);
        double f = x + parabola1.d;
        double y = parabola1.a * f * f + parabola1.e;
        return new Point(x, y);
    }
}
