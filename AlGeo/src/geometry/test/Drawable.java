package geometry.test;

import java.awt.Color;
import java.awt.Graphics;

public interface Drawable {
    public static final int    WIDTH  = 400;
    public static final int    HEIGHT = 400;
    public static final int    RANGE  = 10;

    public static final double UNIT_X = WIDTH / (double) (RANGE);
    public static final double UNIT_Y = HEIGHT / (double) (RANGE);

    public static int xToInt(double x) {
        return 50 + (int) Math.round(x * UNIT_X);
    }

    public static int yToInt(double y) {
        return HEIGHT + 50 - (int) Math.round(y * UNIT_Y);
    }

    public void paint(Graphics g, Color color);
}
