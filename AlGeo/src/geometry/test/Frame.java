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
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Frame extends JFrame implements Runnable {
    protected final BufferedImage image_base     = new BufferedImage(Drawable.WIDTH + 100, Drawable.HEIGHT + 100, BufferedImage.TYPE_INT_RGB);
    protected final BufferedImage image_animated = new BufferedImage(Drawable.WIDTH + 100, Drawable.HEIGHT + 100, BufferedImage.TYPE_INT_RGB);
    protected ArrayList<Scene>    scenes         = null;

    private Frame() {
        Graphics g = image_base.getGraphics();
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

    public static Frame create() {
        Frame f = new Frame();
        SwingUtilities.invokeLater(f);
        return f;
    }

    public void addScene(Scene scene) {
        if(scenes == null) {
            scenes = new ArrayList<Scene>(2);

            new Thread(new Runnable() {
                private int i = 0;

                @Override
                public void run() {
                    Graphics g = image_animated.getGraphics();
                    Scene scene;

                    while(true)
                        synchronized(scenes) {
                            g.drawImage(image_base, 0, 0, null);

                            if(scenes.isEmpty()) {
                                try {
                                    scenes.wait();
                                }
                                catch(InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                scene = scenes.get(i);

                                for(int j = 0; j < scene.drawables.size(); ++j)
                                    scene.drawables.get(j).paint(g, scene.colors.get(j));

                                repaint();
                                i = (i + 1) % scenes.size();

                                try {
                                    scenes.wait(scene.duration);
                                }
                                catch(InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                }
            }).start();
        }

        synchronized(scenes) {
            scenes.add(scene);
            scenes.notifyAll();
        }
    }

    @Override
    public void paint(Graphics g) {
        if(scenes == null)
            g.drawImage(image_base, 50, 50, null);
        else
            synchronized(scenes) {
                g.drawImage(image_animated, 50, 50, null);
            }
    }

    public void writeToFile(String path) {
        try {
            ImageIO.write(image_base, "png", new File(path));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void drawPolygon(Polygon polygon, Color color) {
        Graphics g = image_base.getGraphics();
        polygon.paint(g, color);
        repaint();
    }

    public void drawLine(Line line, Color color) {
        Graphics g = image_base.getGraphics();
        line.paint(g, color);
        repaint();
    }

    public void drawLineSegment(LineSegment line, Color color) {
        Graphics g = image_base.getGraphics();
        line.paint(g, color);
        repaint();
    }

    public void drawPoint(Point point, Color color) {
        Graphics g = image_base.getGraphics();
        point.paint(g, color);
        repaint();
    }

    @Override
    public void run() {
    }
}
