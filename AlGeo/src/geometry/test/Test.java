package geometry.test;

import geometry.Line;
import geometry.LineSegment;
import geometry.Parabola;
import geometry.PodalPoints;
import geometry.Point;
import geometry.Polygon;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;

import algorithms.ConvexHull;

public class Test {
    public static void main(String[] args) {
        fortuneSweep();
        // ub3();
        // ub2();
        // ub1();
    }

    public static void ub3() {
        int dim = 30;
        Frame frame = Frame.create("Graham Scan - animated steps", dim, dim);
        Point[] pointcloud = randomPoints(dim, dim, dim);

        for(int i = 0; i < pointcloud.length; i++)
            frame.draw(pointcloud[i], Color.BLACK);

        Polygon ch = ConvexHull.grahamScan(frame, pointcloud);
        Scene s = new Scene(5000);
        s.add(ch, Color.RED);
        frame.addScene(s);
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

        Frame frame1 = Frame.create("1A", 19, 15);
        frame1.draw(p1, Color.RED);

        for(PodalPoints l : antiPodalPoints) {
            Scene s = new Scene(1000);
            s.add(new LineSegment(l.point1, l.point2), Color.ORANGE);
            s.add(l.line1, Color.BLUE);
            s.add(l.line2, Color.BLUE);
            s.add(l.point1, Color.GREEN);
            s.add(l.point2, Color.GREEN);
            frame1.addScene(s);
        }

        Frame frame2 = Frame.create("2A", 27, 15);
        frame2.draw(p1, Color.RED);
        frame2.draw(p2, Color.MAGENTA);

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

        Frame.create("2B", 27, 15).draw(ConvexHull.convexHull(p1, p2), Color.BLACK);
    }

    public static void ub1() {
        Polygon p = new Polygon(new Point(4, 9), new Point(6, 9), new Point(8, 7), new Point(9, 5), new Point(8, 3),
                new Point(6, 1), new Point(4, 1), new Point(2, 3), new Point(1, 5), new Point(2, 7));
        Frame frame = Frame.create("", 10, 10);
        frame.draw(p, Color.RED);

        Point point1 = new Point(3, 7);
        Point point2 = new Point(2, 7);
        Point point3 = new Point(2, 8);
        frame.draw(point1, Color.BLUE);
        frame.draw(point2, Color.GREEN);
        frame.draw(point3, Color.CYAN);
        System.out.println(String.format("polygon contains %s (blue): %s", point1, p.contains(point1)));
        System.out.println(String.format("polygon contains %s (green): %s", point2, p.contains(point2)));
        System.out.println(String.format("polygon contains %s (cyan): %s", point3, p.contains(point3)));

        Line line1 = new Line(new Point(3, 1), new Point(9, 6));
        Line line2 = new Line(new Point(8.5, 1), new Point(8.5, 9));
        Line line3 = new Line(new Point(9, 1), new Point(10, 7));
        frame.draw(line1, Color.BLUE);
        frame.draw(line2, Color.GREEN);
        frame.draw(line3, Color.CYAN);
        System.out.println(String.format("intersection of polygon and line1 (blue): %s", p.intersectionWith(line1)));
        System.out.println(String.format("intersection of polygon and line2 (green): %s", p.intersectionWith(line2)));
        System.out.println(String.format("intersection of polygon and line3 (cyan): %s", p.intersectionWith(line3)));
    }

    public static Point[] randomPoints(double rangex, double rangey, int total) {
        Random rand = new Random();
        Point[] randomPoints = new Point[total];

        for(int i = 0; i < total; i++)
            randomPoints[i] = new Point(rand.nextDouble() * rangex, rand.nextDouble() * rangey);

        return randomPoints;
    }

    public static void fortuneSweep() {
        Frame f = Frame.create("", 30, 30);
        Point p1 = new Point(11, 13);
        Point p2 = new Point(18, 26);
        f.drawPoint(p1, Color.BLUE);
        f.drawPoint(p2, Color.GREEN);

        for(int i = 1; i < p1.getY(); ++i) {
            Scene s = new Scene(500);
            s.add(new Line(new Point(0, i), new Point(30, i)), Color.BLACK);
            Parabola pa1 = new Parabola(p1, p1.getY() - i);
            Parabola pa2 = new Parabola(p2, p2.getY() - i);
            s.add(pa1, Color.BLUE);
            s.add(pa2, Color.GREEN);
            s.add(Parabola.leftIntersection(pa1, pa2), Color.RED);
            s.add(Parabola.rightIntersection(pa1, pa2), Color.ORANGE);
            f.addScene(s);
        }
    }
}
