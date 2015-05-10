package geometry.test;

import geometry.Line;
import geometry.LineSegment;
import geometry.MyAnstieg;
import geometry.PodalPoints;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;

import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.TreeMap;

import mats.Mat2x2;
import algorithms.ConvexHull;

public class Test {
    public static void main(String[] args) {
        ub3();
        // ub2();
        // ub1();
    }

    public static void ub3() {
        int dim = 30;
        Frame frame = Frame.create("Graham Scan - animated steps", new Dimensions(dim, dim));
        Point[] pointcloud = randomPoints(dim, dim, dim);

        for(int i = 0; i < pointcloud.length; i++) {
            frame.drawPoint(pointcloud[i], Color.BLACK);
        }

        // get minimum
        Point min = ConvexHull.getMinY(pointcloud);
        frame.drawPoint(min, Color.RED);

        // select only relevant points from point cloud
        Map<MyAnstieg, Point> relevantPoints = ConvexHull.getRelevantPoints(pointcloud);
        System.out.println(relevantPoints.size() + "relevant points for graham from " + dim + "points in point cloud.");

        // sort by ascent
        Map<MyAnstieg, Point> sortedPoints = new TreeMap<MyAnstieg, Point>(Collections.reverseOrder());
        for(MyAnstieg a : relevantPoints.keySet()) {
            sortedPoints.put(a, relevantPoints.get(a));
        }

        // put first 3 points on stack
        Stack<Point> stack = new Stack<Point>();
        stack.push(min); // first

        MyAnstieg ascent = ((TreeMap<MyAnstieg, Point>) sortedPoints).firstKey();
        Point p = sortedPoints.get(ascent);
        sortedPoints.remove(ascent);
        stack.push(p); // second point

        ascent = ((TreeMap<MyAnstieg, Point>) sortedPoints).firstKey();
        p = sortedPoints.get(ascent);
        sortedPoints.remove(ascent);
        stack.push(p); // third point

        // have a look at each point
        for(MyAnstieg ca : sortedPoints.keySet()) { // ca ... current ascent

            Point t = stack.pop(); // t ... top
            Point ntt = stack.peek(); // ntt ... next-to-top
            stack.push(t); // put top back on stack

            LineSegment line1 = new LineSegment(ntt, t);
            LineSegment line2 = new LineSegment(t, sortedPoints.get(ca));

            // use determinant for testing left/right turns
            Vector v1 = new Vector(ntt.getX() - t.getX(), ntt.getY() - t.getY());
            Vector v2 = new Vector(sortedPoints.get(ca).getX() - t.getX(), sortedPoints.get(ca).getY() - t.getY());

            Mat2x2 matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));

            // testing for not left-turns
            while(!(matrix.getDeterminant() <= 0)) {

                Scene s = new Scene(1000);
                Point ptemp = min;
                for(Iterator<Point> it = stack.iterator(); it.hasNext();) {
                    LineSegment l = new LineSegment(ptemp, ptemp = it.next());
                    s.add(l, Color.BLACK);
                }
                s.add(ntt, Color.GREEN);
                s.add(line1, Color.GREEN);
                s.add(line2, Color.GREEN);
                frame.addScene(s);

                // remove last point
                stack.pop();

                // have a look at the next point
                t = stack.pop();
                ntt = stack.peek();
                stack.push(t);

                line1 = new LineSegment(ntt, t);
                line2 = new LineSegment(t, sortedPoints.get(ca));

                v1 = new Vector(ntt.getX() - t.getX(), ntt.getY() - t.getY());
                v2 = new Vector(sortedPoints.get(ca).getX() - t.getX(), sortedPoints.get(ca).getY() - t.getY());

                matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));
            }

            Scene s = new Scene(1000);
            Point ptemp = min;
            for(Iterator<Point> it = stack.iterator(); it.hasNext();) {
                LineSegment l = new LineSegment(ptemp, ptemp = it.next());
                s.add(l, Color.BLACK);
            }
            s.add(ntt, Color.BLACK);
            s.add(line1, Color.BLACK);
            s.add(line2, Color.BLACK);
            frame.addScene(s);

            // put current point on stack
            stack.push(sortedPoints.get(ca));
        }

        // generate polygon from points on stack
        Point[] points = new Point[stack.size()];
        stack.toArray(points);
        Polygon ch = new Polygon(points);

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

    private static Point[] randomPoints(int rangex, int rangey, int total) {
        Random rand = new Random();
        Point[] randomPoints = new Point[total];
        for(int i = 0; i < total; i++) {
            int randX = rand.nextInt(rangex) + 1;
            int randY = rand.nextInt(rangey) + 1;
            randomPoints[i] = new Point(randX, randY);
        }
        return randomPoints;
    }

    // Wo soll das hin?
    private static Point[] randomPoints(double rangex, double rangey, int total) {
        Random rand = new Random();
        Point[] randomPoints = new Point[total];
        for(int i = 0; i < total; i++) {
            double randX = rand.nextDouble() * total;
            double randY = rand.nextDouble() * total;
            randomPoints[i] = new Point(randX, randY);
        }
        return randomPoints;
    }
}
