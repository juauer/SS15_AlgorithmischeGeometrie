package geometry.test;

import geometry.Line;
import geometry.LineSegment;
import geometry.Point;
import geometry.Polygon;

import java.awt.Color;
import java.util.LinkedList;

public class Test {
    public static void main(String[] args) {
        ub2();
        // ub1();
    }

    public static void ub2() {
        Polygon p = new Polygon(new Point(8, 13), new Point(11, 13), new Point(15, 7), new Point(14, 5),
                new Point(11, 2), new Point(8, 2), new Point(5, 4), new Point(4, 7), new Point(5, 10));
        LinkedList<Line[]> antiPodalPoints = p.antipodalPoints();
        Frame frame = Frame.create();
        frame.drawPolygon(p, Color.RED);

        for(Line[] l : antiPodalPoints) {
            Scene s = new Scene(1000);
            s.add(l[0].p1, Color.GREEN);
            s.add(l[1].p1, Color.GREEN);
            s.add(l[0], Color.BLUE);
            s.add(l[1], Color.BLUE);
            s.add(new LineSegment(l[0].p1, l[1].p1), Color.ORANGE);
            frame.addScene(s);
        }

        // frame.writeToFile("./../assignments/ub2/test.png");
    }

    public static void ub1() {
        Polygon p = new Polygon(new Point(4, 9), new Point(6, 9), new Point(8, 7), new Point(9, 5), new Point(8, 3),
                new Point(6, 1), new Point(4, 1), new Point(2, 3), new Point(1, 5), new Point(2, 7));
        Frame frame = Frame.create();
        frame.drawPolygon(p, Color.RED);

        Point point1 = new Point(3, 7);
        Point point2 = new Point(2, 7);
        Point point3 = new Point(2, 8);
        frame.drawPoint(point1, Color.BLUE);
        frame.drawPoint(point2, Color.GREEN);
        frame.drawPoint(point3, Color.CYAN);
        System.out.println(String.format("polygon contains %s (blue): %s", point1, p.contains(point1)));
        System.out.println(String.format("polygon contains %s (green): %s", point2, p.contains(point2)));
        System.out.println(String.format("polygon contains %s (cyan): %s", point3, p.contains(point3)));

        Line line1 = new Line(new Point(3, 1), new Point(9, 6));
        Line line2 = new Line(new Point(8.5, 1), new Point(8.5, 9));
        Line line3 = new Line(new Point(9, 1), new Point(10, 7));
        frame.drawLine(line1, Color.BLUE);
        frame.drawLine(line2, Color.GREEN);
        frame.drawLine(line3, Color.CYAN);
        System.out.println(String.format("intersection of polygon and line1 (blue): %s", p.intersectionWith(line1)));
        System.out.println(String.format("intersection of polygon and line2 (green): %s", p.intersectionWith(line2)));
        System.out.println(String.format("intersection of polygon and line3 (cyan): %s", p.intersectionWith(line3)));

        // frame.writeToFile("./../assignments/ub1/test.png");
    }
}
