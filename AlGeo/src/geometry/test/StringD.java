package geometry.test;

import geometry.Point;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class StringD implements Drawable {
    static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    String            string;
    Font              font;
    Point             location;

    public StringD(String string, Point location, Font font) {
        this.string = string;
        this.location = location;
        this.font = font;
    }

    public StringD(String string, Point location) {
        this(string, location, DEFAULT_FONT);
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(string, dimensions.xToInt(location.getX()), dimensions.yToInt(location.getY()));
    }
}
