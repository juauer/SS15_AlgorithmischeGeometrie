package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;

public class Parabola implements Drawable {
    private static final double STEPSIZE_DRAWING = 0.1d;
    public double               a, d, e;
    public double               minX             = 0;
    public double               maxX             = Double.MAX_VALUE;

    public Parabola(double a, double d, double e) {
        this.a = a;
        this.d = d;
        this.e = e;
    }

    public Parabola(Point point, double lineY) {
        double distance = point.getY() - lineY;
        a = 0.5d / distance;
        d = -point.getX();
        e = point.getY() - distance / 2.0d;
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        g.setColor(color);
        double x = Math.max(0.0d, minX);
        double f = x + d;
        double y1 = a * f * f + e;

        for(x += STEPSIZE_DRAWING; x <= Math.min(dimensions.range_x, maxX); x += STEPSIZE_DRAWING) {
            f = x + d;
            double y2 = a * f * f + e;

            if(y1 <= dimensions.range_y && y1 >= 0 && y2 <= dimensions.range_y && y2 >= 0)
                g.drawLine(dimensions.xToInt(x - STEPSIZE_DRAWING), dimensions.yToInt(y1), dimensions.xToInt(x), dimensions.yToInt(y2));

            y1 = y2;
        }
    }

    public static Point intersection(Parabola parabola, double verticalBeamX) {
        double f = verticalBeamX + parabola.d;
        return new Point(verticalBeamX, parabola.a * f * f + parabola.e);
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

    public static Point midIntersection(Parabola parabola1, Parabola parabola2) {
        if(parabola1.e < parabola2.e)
            return rightIntersection(parabola1, parabola2);

        return leftIntersection(parabola1, parabola2);
    }
}
