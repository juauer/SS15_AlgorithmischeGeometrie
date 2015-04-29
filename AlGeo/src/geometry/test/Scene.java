package geometry.test;

import java.awt.Color;
import java.util.LinkedList;

public class Scene {
    public final int                  duration;
    public final LinkedList<Drawable> drawables = new LinkedList<Drawable>();
    public final LinkedList<Color>    colors    = new LinkedList<Color>();

    public Scene(int duration) {
        this.duration = duration;
    }

    public void add(Drawable d, Color color) {
        drawables.add(d);
        colors.add(color);
    }
}
