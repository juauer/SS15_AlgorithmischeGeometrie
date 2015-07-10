package geometry.test;

import geometry.Beam;
import geometry.Line;
import geometry.LineSegment;
import geometry.PodalPoints;
import geometry.Point;
import geometry.Polygon;
import geometry.Voronoi;
import geometry.algorithms.ConvexHull;
import geometry.algorithms.FortunesSweep;
import geometry.algorithms.RotationalSweep;
import geometry.algorithms.VisibilityGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import kdtrees.KDKey;
import kdtrees.KDTree;

public class Test {
    public static void main(String[] args) {
        ub12();
        // ub7(args);
        // ub6();
        // ub5();
        // ub3();
        // ub2();
        // ub1();
        // fortunesSweep();
    }

    public static void ub12() {
        Polygon[] polygons = new Polygon[] {
                ConvexHull.grahamScan(null, randomPoints(1, 5, 1, 13, 16)),
                ConvexHull.grahamScan(null, randomPoints(6, 10, 3, 7, 8)),
                ConvexHull.grahamScan(null, randomPoints(6, 10, 9, 13, 8)),
                ConvexHull.grahamScan(null, randomPoints(16, 20, 1, 13, 16)),
                ConvexHull.grahamScan(null, randomPoints(11, 15, 1, 5, 8)),
                ConvexHull.grahamScan(null, randomPoints(11, 15, 8, 12, 8))
        };

        /* ================================================= */

        Frame frame1 = Frame.create("Rotational Sweep", 21, 14);
        Point point = new Point(10.5, 7);

        for(Polygon p : polygons)
            frame1.draw(p, Color.BLACK);

        RotationalSweep.visiblePoints(frame1, point, polygons);

        /* ================================================= */

        Frame frame2 = Frame.create("Visibility Graph", 21, 14);

        for(Polygon p : polygons)
            frame2.draw(p, Color.BLACK);

        VisibilityGraph graph = VisibilityGraph.create(frame2, polygons);

        /* ================================================= */

        Frame frame3 = Frame.create("Shortest Path", 21, 14);
        ArrayList<Point> points = new ArrayList<Point>();

        for(Polygon poly : polygons) {
            frame3.draw(poly, Color.BLACK);

            for(Point p : poly.points)
                points.add(p);
        }

        Collections.sort(points);
        LinkedList<Point> path = graph.shortestPath(points.get(0), points.get(points.size() - 1));
        System.out.println("Path Length: " + graph.shortestPathLength(points.get(0), points.get(points.size() - 1)));

        for(int i = 0; i < path.size() - 1; ++i)
            frame3.draw(new LineSegment(path.get(i), path.get(i + 1)), Color.MAGENTA);
    }

    public static void ub7(String[] args) {
        class Settlement implements KDKey, Drawable {
            double[] latLong;
            Point    latLong_transformed;

            Settlement(double latitude, double longitude) {
                latLong = new double[] { latitude, longitude };
                latLong_transformed = new Point((latitude - 47) * 5, (longitude - 8) * 5);
            }

            @Override
            public double getKey(int d) {
                return latLong[d];
            }

            @Override
            public void paint(Graphics g, Dimensions dimensions, Color color) {
                g.setColor(color);
                g.fillOval(dimensions.xToInt(latLong_transformed.getX()) - 1,
                        dimensions.yToInt(latLong_transformed.getY()) - 1, 3, 3);
            }
        }

        LinkedList<Settlement> settlements = new LinkedList<Settlement>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(args.length > 0 ? args[0] :
                            "./orte_deutschland.txt"), "ISO-8859-1"));
            String line;

            while((line = reader.readLine()) != null) {
                String[] cols = line.split("\t");
                settlements.add(new Settlement(Double.parseDouble(cols[4]), Double.parseDouble(cols[5])));
            }

            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        KDTree<Settlement> kdtree = new KDTree<Settlement>(2, settlements);
        double[][] query1 = new double[][] { { 51.0, 53.0 }, { 11.0, 12.0 } };
        double[][] query2 = new double[][] { { 49.0, 50.0 }, { 9.0, 11.0 } };
        Frame frame = Frame.create("", 40, 35);

        for(Settlement s : settlements)
            frame.draw(s, Color.BLACK);

        for(double[][] query : new double[][][] { query1, query2 }) {
            for(Settlement s : kdtree.search(query))
                frame.draw(s, Color.GREEN);

            frame.draw(new LineSegment(new Point((query[0][0] - 47) * 5, (query[1][0] - 8) * 5),
                    new Point((query[0][0] - 47) * 5, (query[1][1] - 8) * 5)), Color.RED);
            frame.draw(new LineSegment(new Point((query[0][0] - 47) * 5, (query[1][1] - 8) * 5),
                    new Point((query[0][1] - 47) * 5, (query[1][1] - 8) * 5)), Color.RED);
            frame.draw(new LineSegment(new Point((query[0][1] - 47) * 5, (query[1][1] - 8) * 5),
                    new Point((query[0][1] - 47) * 5, (query[1][0] - 8) * 5)), Color.RED);
            frame.draw(new LineSegment(new Point((query[0][1] - 47) * 5, (query[1][0] - 8) * 5),
                    new Point((query[0][0] - 47) * 5, (query[1][0] - 8) * 5)), Color.RED);
        }
    }

    public static void ub6() {
        Frame frame = Frame.create("", 18, 20);
        Point[] points = new Point[] {
                new Point(6, 14), new Point(10, 12), new Point(12, 10), new Point(12, 6) };

        for(Point p : points)
            frame.draw(p, Color.BLACK);

        new FortunesSweep().fortunesSweep(frame, points);
    }

    public static void ub5() {
        Frame frame = Frame.create("", 11, 11);
        Scene s = new Scene(1000)
                .add(new Point(5, 5), Color.BLACK);

        for(int i = 1; i < 4; ++i) {
            s.add(new LineSegment(new Point(5, 5 + i), new Point(5 + i, 5)), Color.GRAY);
            s.add(new LineSegment(new Point(5 + i, 5), new Point(5, 5 - i)), Color.GRAY);
            s.add(new LineSegment(new Point(5, 5 - i), new Point(5 - i, 5)), Color.GRAY);
            s.add(new LineSegment(new Point(5 - i, 5), new Point(5, 5 + i)), Color.GRAY);
        }

        frame.addScene(s);

        for(Point[] ps : new Point[][] {
                { new Point(4, 3), new Point(6, 8) },
                { new Point(5, 4), new Point(5, 7) },
                { new Point(4, 8), new Point(6, 3) },
                { new Point(3, 4), new Point(8, 6) },
                { new Point(4, 5), new Point(7, 5) },
                { new Point(3, 6), new Point(8, 4) },
                { new Point(4, 4), new Point(7, 7) },
        }) {
            Point p1 = ps[0];
            Point p2 = ps[1];
            double dx = Math.abs(p1.getX() - p2.getX());
            double dy = Math.abs(p1.getY() - p2.getY());
            double d2 = (dx + dy) / 2;
            double r = dx / dy;

            if(r < 1.0d) {
                Point q1 = new Point(p1.getX(), p1.getY() + (p1.getY() < p2.getY() ? d2 : -d2));
                Point q2 = new Point(p2.getX(), p2.getY() + (p1.getY() < p2.getY() ? -d2 : d2));
                LineSegment ls = new LineSegment(q1, q2);
                Beam b1 = new Beam(q1, new Point(q1.getX() + (q1.getX() < q2.getX() ? -1 : 1), q1.getY()));
                Beam b2 = new Beam(q2, new Point(q2.getX() + (q1.getX() < q2.getX() ? 1 : -1), q2.getY()));
                frame.addScene(new Scene(1000)
                        .add(p1, Color.BLACK).add(p2, Color.BLACK).add(ls, Color.BLACK)
                        .add(b1, Color.BLACK).add(b2, Color.BLACK));
            }
            else if(r > 1.0d) {
                Point q1 = new Point(p1.getX() + (p1.getX() < p2.getX() ? d2 : -d2), p1.getY());
                Point q2 = new Point(p2.getX() + (p1.getX() < p2.getX() ? -d2 : d2), p2.getY());
                LineSegment ls = new LineSegment(q1, q2);
                Beam b1 = new Beam(q1, new Point(q1.getX(), q1.getY() + (q1.getY() < q2.getY() ? -1 : 1)));
                Beam b2 = new Beam(q2, new Point(q2.getX(), q2.getY() + (q1.getY() < q2.getY() ? 1 : -1)));
                frame.addScene(new Scene(1000)
                        .add(p1, Color.BLACK).add(p2, Color.BLACK).add(ls, Color.BLACK)
                        .add(b1, Color.BLACK).add(b2, Color.BLACK));
            }
            else {
                s = new Scene(1000);
                Point q1 = new Point(p1.getX(), p2.getY());
                Point q2 = new Point(p2.getX(), p1.getY());
                LineSegment ls = new LineSegment(q1, q2);
                Point top = q1.getY() < q2.getY() ? q2 : q1;
                Point bottom = q1.getY() < q2.getY() ? q1 : q2;
                Point left = q1.getX() < q2.getX() ? q1 : q2;
                Point right = q1.getX() < q2.getX() ? q2 : q1;
                Beam bt = new Beam(top, new Point(top.getX(), top.getY() + 1));
                Beam bb = new Beam(bottom, new Point(bottom.getX(), bottom.getY() - 1));
                Beam bl = new Beam(left, new Point(left.getX() - 1, left.getY()));
                Beam br = new Beam(right, new Point(right.getX() + 1, right.getY()));

                for(double i = -10.5; i < 11; i += 0.5) {
                    Line l = new Line(new Point(i, 0), new Point(i + 11, 11));
                    Point is1 = null;
                    Point is2 = null;

                    for(Beam b : new Beam[] { bt, bb, bl, br })
                        if(is2 != null)
                            break;
                        else if(is1 == null)
                            is1 = l.intersectionWith(b);
                        else
                            is2 = l.intersectionWith(b);

                    if(is2 != null)
                        s.add(new LineSegment(is1, is2), Color.RED);
                }

                frame.addScene(s.add(p1, Color.BLACK).add(p2, Color.BLACK).add(ls, Color.BLACK)
                        .add(bt, Color.BLACK).add(bb, Color.BLACK).add(bl, Color.BLACK).add(br, Color.BLACK));
            }
        }
    }

    public static void ub3() {
        int dim = 30;
        Frame frame = Frame.create("Graham Scan - animated steps", dim, dim);
        Point[] pointcloud = randomPoints(0, dim, 0, dim, dim);

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

    public static Point[] randomPoints(double minX, double maxX, double minY, double maxY, int total) {
        Random rand = new Random();
        Point[] randomPoints = new Point[total];

        for(int i = 0; i < total; i++)
            randomPoints[i] = new Point(minX + rand.nextDouble() * (maxX - minX), minY + rand.nextDouble() * (maxY - minY));

        return randomPoints;
    }

    public static void fortunesSweep() {
        Frame frame = Frame.create("", 50, 30);
        Point[] points = randomPoints(5, 45, 5, 25, 80);

        for(Point p : points)
            frame.draw(p, Color.BLACK);

        Voronoi v = new FortunesSweep().fortunesSweep(frame, points);
        frame.addScene(new Scene(5000)
                .add(v, Color.BLACK)
                .add(v.triangulation(), Color.RED));
    }
}
