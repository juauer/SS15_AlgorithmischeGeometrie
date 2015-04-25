package geometry.test;

import geometry.Line;
import geometry.Point;
import geometry.Polygon;

import java.awt.Color;

public class Test {
    public static void main(String[] args) {
        Frame frame = new Frame();
        Polygon p = new Polygon(new Point(4, 9), new Point(6, 9), new Point(8, 7), new Point(9, 5), new Point(8, 3),
                new Point(6, 1), new Point(4, 1), new Point(2, 3), new Point(1, 5), new Point(2, 7));
        frame.drawPolygon(p, Color.RED);

        Point point1 = new Point(3, 7);
        Point point2 = new Point(2, 7);
        Point point3 = new Point(2, 8);
        System.out.println(p.contains(point1));
        System.out.println(p.contains(point2));
        System.out.println(p.contains(point3));
        frame.drawPoint(point1, Color.BLUE);
        frame.drawPoint(point2, Color.GREEN);
        frame.drawPoint(point3, Color.YELLOW);

        Line line1 = new Line(new Point(3, 1), new Point(9, 6));
        Line line2 = new Line(new Point(8.5, 1), new Point(8.5, 9));
        Line line3 = new Line(new Point(9, 1), new Point(11, 7));
        System.out.println(p.intersectionWith(line1));
        System.out.println(p.intersectionWith(line2));
        System.out.println(p.intersectionWith(line3));
        frame.drawLine(line1, Color.BLUE);
        frame.drawLine(line2, Color.GREEN);
    }
}
