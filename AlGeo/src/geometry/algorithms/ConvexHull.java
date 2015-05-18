package geometry.algorithms;

import geometry.C;
import geometry.LineSegment;
import geometry.PodalPoints;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;
import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import mats.Mat2x2;

public class ConvexHull {
    /**
     * Compute the convex hull for a set of points using the
     * graham-scan-algorithm.
     * 
     * @param frame a Frame for drawing, or null if no debug drawings are needed
     * @param pointcloud a set of points
     * @return the polygonial chain forming a convex hull for the set of points
     */
    public static Polygon grahamScan(Frame frame, Point... pointcloud) {
        // copy points to list and find minimum
        Point m = pointcloud[0];
        LinkedList<Point> sortedPoints = new LinkedList<Point>();

        for(Point p : pointcloud) {
            sortedPoints.add(p);

            if(p.compareTo(m) < 0)
                m = p;
        }

        final Point min = m;
        sortedPoints.remove(min);

        // sort by ascent first and distance second
        Collections.sort(sortedPoints, new Comparator<Point>() {

            @Override
            public int compare(Point p1, Point p2) {
                Vector pos1 = p1.toPosition().substract(min.toPosition());
                Vector pos2 = p2.toPosition().substract(min.toPosition());
                double ascent1 = pos1.getAscent();
                double ascent2 = pos2.getAscent();

                if(Math.abs(ascent1 - ascent2) < C.E) {
                    double l = pos1.length() - pos2.length();
                    return (int) (l < 0 ? Math.floor(l) : Math.ceil(l));
                }

                double d = ascent1 - ascent2;
                return (int) (d < 0 ? Math.floor(d) : Math.ceil(d));
            }
        });

        // put first 3 points on stack
        Stack<Point> stack = new Stack<Point>();
        stack.push(min);
        stack.push(sortedPoints.removeFirst());
        stack.push(sortedPoints.removeFirst());

        for(Point p1 : sortedPoints) {
            Point p2 = stack.pop();
            Point p3 = stack.peek();
            stack.push(p2);

            // use determinant for testing left/right turns
            Vector v1 = p3.toPosition().substract(p2.toPosition());
            Vector v2 = p1.toPosition().substract(p2.toPosition());
            Mat2x2 matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));

            // testing for not left-turns
            while(matrix.getDeterminant() > 0) {
                if(frame != null) {
                    LineSegment line1 = new LineSegment(p3, p2);
                    LineSegment line2 = new LineSegment(p2, p1);
                    Scene s = new Scene(1000);
                    Point ptemp = min;

                    for(Iterator<Point> it = stack.iterator(); it.hasNext();) {
                        LineSegment l = new LineSegment(ptemp, ptemp = it.next());
                        s.add(l, Color.BLACK);
                    }

                    s.add(p3, Color.GREEN);
                    s.add(line1, Color.GREEN);
                    s.add(line2, Color.GREEN);
                    frame.addScene(s);
                }

                // remove last point
                stack.pop();

                // have a look at the next point
                p2 = stack.pop();
                p3 = stack.peek();
                stack.push(p2);
                v1 = p3.toPosition().substract(p2.toPosition());
                v2 = p1.toPosition().substract(p2.toPosition());
                matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));
            }

            if(frame != null) {
                LineSegment line1 = new LineSegment(p3, p2);
                LineSegment line2 = new LineSegment(p2, p1);
                Scene s = new Scene(1000);
                Point ptemp = min;

                for(Iterator<Point> it = stack.iterator(); it.hasNext();) {
                    LineSegment l = new LineSegment(ptemp, ptemp = it.next());
                    s.add(l, Color.BLACK);
                }

                s.add(p3, Color.BLACK);
                s.add(line1, Color.BLACK);
                s.add(line2, Color.BLACK);
                frame.addScene(s);
            }

            // put current point on stack
            stack.push(p1);
        }

        // generate polygon from points on stack
        Point[] points = new Point[stack.size()];

        for(int i = 0; i < points.length; ++i)
            points[i] = stack.pop();

        return new Polygon(points);
    }

    /**
     * Compute the convex hull of two polygons using the rotating calipers.
     * 
     * @param p1 a polygon
     * @param p2 another polygon
     * @return a polygon describing the convex hull of p1 and p2
     */
    public static Polygon convexHull(Polygon p1, Polygon p2) {
        LinkedList<PodalPoints> copodalPoints = Polygon.copodalPoints(p1, p2);
        LinkedList<Point> result = new LinkedList<Point>();

        for(int i = copodalPoints.size() - 1; i > -1; --i)
            if(copodalPoints.get(i).isBridge == 0)
                copodalPoints.remove(i);

        copodalPoints.add(copodalPoints.getFirst());
        Polygon current = copodalPoints.getFirst().isBridge == 1 ? p1 : p2;
        int j;

        for(int i = 0; i < copodalPoints.size() - 1; ++i) {
            PodalPoints pp1 = copodalPoints.get(i);
            PodalPoints pp2 = copodalPoints.get(i + 1);

            if(current == p1) {
                j = pp1.index1;

                result.add(p1.points[j]);

                while(j != pp2.index1) {
                    result.add(p1.points[j]);
                    j = (j + 1) % p1.points.length;
                }

                current = p2;
            }
            else {
                j = pp1.index2;
                result.add(p2.points[j]);

                while(j != pp2.index2) {
                    result.add(p2.points[j]);
                    j = (j + 1) % p2.points.length;
                }

                current = p1;
            }
        }

        return new Polygon(result.toArray(new Point[result.size()]));
    }
}
