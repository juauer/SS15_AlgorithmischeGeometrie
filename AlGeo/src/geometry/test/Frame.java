package geometry.test;

import geometry.Line;
import geometry.LineSegment;
import geometry.Point;
import geometry.Polygon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Frame extends JFrame {
    private final BufferedImage image = new BufferedImage(Drawable.WIDTH + 100, Drawable.HEIGHT + 100, BufferedImage.TYPE_INT_RGB);

    public Frame() {
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Drawable.WIDTH + 100, Drawable.HEIGHT + 100);
        g.setColor(Color.LIGHT_GRAY);

        for(int i = 0; i <= Drawable.RANGE; ++i) {
            g.drawLine(Drawable.xToInt(i), Drawable.yToInt(0), Drawable.xToInt(i), Drawable.yToInt(Drawable.RANGE));
            g.drawLine(Drawable.xToInt(0), Drawable.yToInt(i), Drawable.xToInt(Drawable.RANGE), Drawable.yToInt(i));
        }

        setLayout(new BorderLayout());
        setSize(Drawable.WIDTH + 200, Drawable.HEIGHT + 200);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 50, 50, null);
    }

    public void writeToFile(String path) {
        try {
            ImageIO.write(image, "png", new File(path));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void drawPolygon(Polygon polygon, Color color) {
        Graphics g = image.getGraphics();
        polygon.paint(g, color);
        repaint();
    }

    public void drawLine(Line line, Color color) {
        Graphics g = image.getGraphics();
        line.paint(g, color);
        repaint();
    }

    public void drawLineSegment(LineSegment line, Color color) {
        Graphics g = image.getGraphics();
        line.paint(g, color);
        repaint();
    }

    public void drawPoint(Point point, Color color) {
        Graphics g = image.getGraphics();
        point.paint(g, color);
        repaint();
    }
}
