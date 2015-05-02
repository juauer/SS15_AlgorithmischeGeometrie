package geometry.test;

import geometry.Line;
import geometry.LineSegment;
import geometry.PodalPoints;
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
        Polygon p1 = new Polygon(new Point(8, 13), new Point(11, 13), new Point(15, 7), new Point(14, 5),
                new Point(11, 2), new Point(8, 2), new Point(5, 4), new Point(4, 7), new Point(5, 10));
        Polygon p2 = new Polygon(new Point(18, 13), new Point(22, 12), new Point(24, 11),
                new Point(20, 10), new Point(6, 8), new Point(3, 11));
        LinkedList<PodalPoints> antiPodalPoints = p1.antipodalPoints();
        LinkedList<PodalPoints> coPodalPoints = Polygon.copodalPoints(p1, p2);

        System.out.println(String.format("1A:%n"
                + "calipers (blue) rotating around polygon P1 (red) at antipodal points (green)%n"
                + "P1 diameter: %.1f%n%n"
                + "2A:%n"
                + "calipers rotating around copodal points of P1 and P2 (magenta) in search of bridges (orange)%n%n"
                + "2B:%n"
                + "convex hull of P1 and P2, found by following the polygonal chain across the bridges found in 2A%n"
                , p1.diameter()));

        Frame frame1 = Frame.create("1A", new Dimensions(19, 15));
        frame1.drawPolygon(p1, Color.RED);

        for(PodalPoints l : antiPodalPoints) {
            Scene s = new Scene(1000);
            s.add(new LineSegment(l.point1, l.point2), Color.ORANGE);
            s.add(l.line1, Color.BLUE);
            s.add(l.line2, Color.BLUE);
            s.add(l.point1, Color.GREEN);
            s.add(l.point2, Color.GREEN);
            frame1.addScene(s);
        }

        Frame frame2 = Frame.create("2A", new Dimensions(27, 15));
        frame2.drawPolygon(p1, Color.RED);
        frame2.drawPolygon(p2, Color.MAGENTA);

        for(PodalPoints l : coPodalPoints) {
            Scene s = new Scene(1000);
            s.add(l.line1, Color.BLUE);
            s.add(l.line2, Color.BLUE);
            s.add(l.point1, Color.GREEN);
            s.add(l.point2, Color.GREEN);

            if(l.isBridge != 0)
                s.add(new LineSegment(l.point1, l.point2), Color.ORANGE);

            frame2.addScene(s);
        }

        Frame.create("2B", new Dimensions(27, 15)).drawPolygon(Polygon.convexHull(p1, p2), Color.BLACK);
    }

    public static void ub1() {
        Polygon p = new Polygon(new Point(4, 9), new Point(6, 9), new Point(8, 7), new Point(9, 5), new Point(8, 3),
                new Point(6, 1), new Point(4, 1), new Point(2, 3), new Point(1, 5), new Point(2, 7));
        Frame frame = Frame.create("", new Dimensions(10, 10));
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
    }
}
