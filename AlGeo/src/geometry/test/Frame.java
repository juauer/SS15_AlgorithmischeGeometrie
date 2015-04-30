package geometry.test;

import geometry.Line;
import geometry.LineSegment;
import geometry.Point;
import geometry.Polygon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Frame extends JFrame implements Runnable {
    protected final BufferedImage image_base         = new BufferedImage(Drawable.WIDTH + 100, Drawable.HEIGHT + 100, BufferedImage.TYPE_INT_RGB);
    protected final BufferedImage image_animated     = new BufferedImage(Drawable.WIDTH + 100, Drawable.HEIGHT + 100, BufferedImage.TYPE_INT_RGB);
    protected final String        debugPath;
    protected ArrayList<Scene>    scenes             = null;
    protected boolean             keyboardControlled = false;

    private Frame(String debugPath) {
        this.debugPath = debugPath;
        Graphics g = image_base.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Drawable.WIDTH + 100, Drawable.HEIGHT + 100);
        g.setColor(Color.LIGHT_GRAY);

        for(int i = 0; i <= Drawable.RANGE_X; ++i)
            g.drawLine(Drawable.xToInt(i), Drawable.yToInt(0), Drawable.xToInt(i), Drawable.yToInt(Drawable.RANGE_Y));

        for(int i = 0; i <= Drawable.RANGE_Y; ++i)
            g.drawLine(Drawable.xToInt(0), Drawable.yToInt(i), Drawable.xToInt(Drawable.RANGE_X), Drawable.yToInt(i));

        setLayout(new BorderLayout());
        setSize(Drawable.WIDTH + 200, Drawable.HEIGHT + 200);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static Frame create(String debugPath) {
        Frame f = new Frame(debugPath);
        SwingUtilities.invokeLater(f);
        return f;
    }

    public void addScene(Scene scene) {
        if(scenes == null) {
            scenes = new ArrayList<Scene>(2);
            addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_T:
                            keyboardControlled = !keyboardControlled;

                            if(!keyboardControlled) {
                                synchronized(scenes) {
                                    scenes.notifyAll();
                                }

                                System.out.println("keyboard control OFF, animation ON");
                            }
                            else
                                System.out.println("keyboard control ON, animation OFF");

                            break;
                        case KeyEvent.VK_SPACE:
                            synchronized(scenes) {
                                scenes.notifyAll();
                            }
                            break;
                        case KeyEvent.VK_ENTER:
                            writeToFile();
                            System.out.println(String.format("image written to %s", debugPath));
                            break;
                        default:
                            break;
                    }
                }
            });

            System.out.println(String.format("Keyboard Controls:%n"
                    + "\tt\ttoggle keyboard control / animation%n"
                    + "\tspace\tnext scene%n"
                    + "\tenter\tsave image to file"));

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
                                    scenes.wait(keyboardControlled ? 0 : scene.duration);
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

    public void writeToFile() {
        try {
            ImageIO.write(scenes == null ? image_base : image_animated, "png", new File(debugPath));
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
