package algorithms;

import geometry.C;
import geometry.LineSegment;
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
    public static Point getMinY(Point... points) {
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

    public static Polygon grahamscan(Frame frame, Point... pointcloud) {
        // get minimum
        Point min = getMinY(pointcloud);

        // sort by ascent first and distance second
        LinkedList<Point> sortedPoints = new LinkedList<Point>();

        for(Point p : pointcloud)
            if(p != min)
                sortedPoints.add(p);

        Collections.sort(sortedPoints, new Comparator<Point>() {

            @Override
            public int compare(Point p1, Point p2) {
                Vector pos1 = p1.toPosition().substract(min.toPosition());
                Vector pos2 = p2.toPosition().substract(min.toPosition());
                double ascent1 = pos2.getMirroredAscent();
                double ascent2 = pos1.getMirroredAscent();

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

        // have a look at each point
        for(Point cp : sortedPoints) { // ca ... current point
            Point t = stack.pop(); // t ... top
            Point ntt = stack.peek(); // ntt ... next-to-top
            stack.push(t); // put top back on stack

            // use determinant for testing left/right turns
            Vector v1 = new Vector(ntt.getX() - t.getX(), ntt.getY() - t.getY());
            Vector v2 = new Vector(cp.getX() - t.getX(), cp.getY() - t.getY());

            Mat2x2 matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));

            // testing for not left-turns
            while(!(matrix.getDeterminant() <= 0)) {
                if(frame != null) {
                    LineSegment line1 = new LineSegment(ntt, t);
                    LineSegment line2 = new LineSegment(t, cp);
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
                }

                // remove last point
                stack.pop();

                // have a look at the next point
                t = stack.pop();
                ntt = stack.peek();
                stack.push(t);

                v1 = new Vector(ntt.getX() - t.getX(), ntt.getY() - t.getY());
                v2 = new Vector(cp.getX() - t.getX(), cp.getY() - t.getY());

                matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));
            }

            if(frame != null) {
                LineSegment line1 = new LineSegment(ntt, t);
                LineSegment line2 = new LineSegment(t, cp);
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
            }

            // put current point on stack
            stack.push(cp);
        }

        // generate polygon from points on stack
        Point[] points = new Point[stack.size()];
        stack.toArray(points);

        return new Polygon(points);
    }
}
