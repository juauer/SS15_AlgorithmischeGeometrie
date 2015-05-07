package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

public class Points implements Drawable {
    public final Point[] points;

    public Points(Point... points) {
        this.points = points;
    }

    public Point getMinY() {
        Point min = points[0];
        for(Point p : points) {
            if(min.getY() <= p.getY()) {
                if(min.getY() == p.getY()) {
                    // Compare x-values
                    if(min.getX() > p.getX()) {
                        min = p;
                    }
                }
            }
            else {
                min = p;
            }
        }
        return min;
    }

    public Map<MyAnstieg, Point> getRelevatPoints() {
        Point min = getMinY();
        Map<MyAnstieg, Point> relevantPoints = new HashMap<MyAnstieg, Point>();

        for(Point p : points) {
            if(p != min) {
                Double d = p.toPosition().substract(min.toPosition()).getAscent();
                MyAnstieg m = new MyAnstieg(d);
                if(!relevantPoints.containsKey(m)) {
                    System.out.println(p);
                    relevantPoints.put(m, p);
                }
            }
        }
        return relevantPoints;
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        for(int i = 0; i < points.length - 1; ++i) {
            points[1].paint(g, dimensions, color);
        }
    }
}
