package geometry.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Frame extends JFrame implements Runnable {
    protected static final String PATH_CAPTURES      = "./capture%d.png";
    protected static int          captured           = 0;
    private static boolean        displayedHelpOnce  = false;
    public final Dimensions       dimensions;
    protected final BufferedImage image_base;
    protected final BufferedImage image_animated;
    protected ArrayList<Scene>    scenes             = null;
    protected int                 i                  = 0;
    protected boolean             keyboardControlled = false;
    protected Point               mousePosition      = null;

    private Frame(String title, Dimensions dimensions) {
        this.dimensions = dimensions;
        setTitle(title);
        image_base = new BufferedImage(dimensions.width + 100, dimensions.height + 100, BufferedImage.TYPE_INT_RGB);
        image_animated = new BufferedImage(dimensions.width + 100, dimensions.height + 100, BufferedImage.TYPE_INT_RGB);

        Graphics g = image_base.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, dimensions.width + 100, dimensions.height + 100);
        g.setColor(Color.LIGHT_GRAY);

        for(int i = 0; i <= dimensions.range_x; ++i)
            g.drawLine(dimensions.xToInt(i), dimensions.yToInt(0), dimensions.xToInt(i), dimensions.yToInt(dimensions.range_y));

        for(int i = 0; i <= dimensions.range_y; ++i)
            g.drawLine(dimensions.xToInt(0), dimensions.yToInt(i), dimensions.xToInt(dimensions.range_x), dimensions.yToInt(i));

        setLayout(new BorderLayout());
        setSize(dimensions.width + 200, dimensions.height + 200);
        setLocationRelativeTo(null);
        setVisible(true);
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if(keyboardControlled && e.getX() > 100 && e.getX() < dimensions.width + 100
                        && e.getY() > 100 && e.getY() < dimensions.height + 100) {
                    mousePosition = e.getPoint();
                    repaint();
                }
                else
                    mousePosition = null;
            }
        });

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        writeToFile();
                        break;
                    default:
                        break;
                }
            }
        });

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();

                if(scenes != null)
                    synchronized(scenes) {
                        scenes.notifyAll();
                    }
            }
        });
    }

    public static Frame create(String title, int range_x, int range_y) {
        Frame f = new Frame(title, new Dimensions(range_x, range_y));
        SwingUtilities.invokeLater(f);
        return f;
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, dimensions.width + 200, dimensions.height + 200);

        if(scenes == null)
            g.drawImage(image_base, 50, 50, null);
        else
            synchronized(scenes) {
                g.drawImage(image_animated, 50, 50, null);
            }

        if(mousePosition != null)
            g.drawString(String.format(Locale.US, "%.1f, %.1f",
                    dimensions.intToX(mousePosition.getX()), dimensions.intToY(mousePosition.getY())),
                    (int) mousePosition.getX(), (int) mousePosition.getY());
    }

    @Override
    public void run() {
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
                        case KeyEvent.VK_RIGHT:
                            synchronized(scenes) {
                                scenes.notifyAll();
                            }
                            break;
                        case KeyEvent.VK_LEFT:
                            i = i > 1 ? i - 2 : scenes.size() + i - 2;
                            synchronized(scenes) {
                                scenes.notifyAll();
                            }
                            break;
                        default:
                            break;
                    }
                }
            });

            if(!displayedHelpOnce) {
                displayedHelpOnce = true;
                System.out.println(String.format("Keyboard Controls:%n"
                        + "\tt           toggle keyboard control / animation%n"
                        + "\t<-          previous scene%n"
                        + "\t->/space    next scene%n"
                        + "\tenter       save image to file"));
            }

            new Thread(new Runnable() {

                @Override
                public void run() {
                    Graphics g = image_animated.getGraphics();
                    Scene scene;

                    while(isVisible())
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
                                    scene.drawables.get(j).paint(g, dimensions, scene.colors.get(j));

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

    public void writeToFile() {
        try {
            String path = String.format(PATH_CAPTURES, ++captured);
            ImageIO.write(scenes == null ? image_base : image_animated, "png", new File(path));
            System.out.println(String.format("image written to %s", path));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Drawable drawable, Color color) {
        Graphics g = image_base.getGraphics();
        drawable.paint(g, dimensions, color);
        repaint();
    }
}
