package geometry.test;

import geometry.Line;
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
    private final BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);

    public Frame() {
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 500, 500);
        g.setColor(Color.LIGHT_GRAY);

        for(int i = 0; i < 11; ++i) {
            g.drawLine(xToInt(i), yToInt(0), xToInt(i), yToInt(10));
            g.drawLine(xToInt(0), yToInt(i), xToInt(10), yToInt(i));
        }

        setLayout(new BorderLayout());
        setSize(600, 600);
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

    private int xToInt(double x) {
        return 50 + (int) Math.round(x * 40.0d);
    }

    private int yToInt(double y) {
        return 450 - (int) Math.round(y * 40.0d);
    }

    public void drawPolygon(Polygon polygon, Color color) {
        int[] xPoints = new int[polygon.points.length];
        int[] yPoints = new int[polygon.points.length];
        Graphics g = image.getGraphics();
        g.setColor(color);

        for(int i = 0; i < polygon.points.length; ++i) {
            xPoints[i] = xToInt(polygon.points[i].getX());
            yPoints[i] = yToInt(polygon.points[i].getY());
            g.fillOval(xPoints[i] - 2, yPoints[i] - 2, 4, 4);
        }

        g.drawPolygon(xPoints, yPoints, polygon.points.length);
        repaint();
    }

    public void drawLine(Line line, Color color) {
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.drawLine(xToInt(line.p1.getX()), yToInt(line.p1.getY()), xToInt(line.p2.getX()), yToInt(line.p2.getY()));
        repaint();
    }

    public void drawPoint(Point point, Color color) {
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillOval(xToInt(point.getX()) - 3, yToInt(point.getY()) - 3, 6, 6);
        repaint();
    }
}
