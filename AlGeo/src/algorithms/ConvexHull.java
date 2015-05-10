package algorithms;

import geometry.LineSegment;
import geometry.MyAnstieg;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;
import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

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

    public static Map<MyAnstieg, Point> getRelevantPoints(Point... points) {
        Point min = getMinY(points);
        Map<MyAnstieg, Point> relevantPoints = new HashMap<MyAnstieg, Point>();

        for(Point p : points) {

            if(p != min) {

                Double d = p.toPosition().substract(min.toPosition()).getMirroredAscent();
                MyAnstieg m = new MyAnstieg(d);

                if(Double.isInfinite(d)) {
                    m = new MyAnstieg(Double.MAX_VALUE);
                }

                if(!relevantPoints.containsKey(m)) {
                    relevantPoints.put(m, p);
                }
                else {
                    Point cp = relevantPoints.get(m);
                    double distanceold = cp.getX() * cp.getX() + cp.getY() * cp.getY();
                    double distancenew = p.getX() * p.getX() + p.getY() * p.getY();

                    if(distancenew > distanceold) {
                        relevantPoints.remove(m);
                        relevantPoints.put(m, p);
                    }
                }
            }
        }
        return relevantPoints;
    }

    public static Polygon grahamscan(Frame frame, Point... pointcloud) {
        // get minimum
        Point min = getMinY(pointcloud);

        // select only relevant points from point cloud
        Map<MyAnstieg, Point> relevantPoints = getRelevantPoints(pointcloud);

        // sort by ascent
        Map<MyAnstieg, Point> sortedPoints = new TreeMap<MyAnstieg, Point>(Collections.reverseOrder());
        for(MyAnstieg a : relevantPoints.keySet()) {
            sortedPoints.put(a, relevantPoints.get(a));
        }

        // put first 3 points on stack
        Stack<Point> stack = new Stack<Point>();
        stack.push(min);

        MyAnstieg ascent = ((TreeMap<MyAnstieg, Point>) sortedPoints).firstKey();
        Point p = sortedPoints.get(ascent);
        sortedPoints.remove(ascent);
        stack.push(p);

        ascent = ((TreeMap<MyAnstieg, Point>) sortedPoints).firstKey();
        p = sortedPoints.get(ascent);
        sortedPoints.remove(ascent);
        stack.push(p);

        // have a look at each point
        for(MyAnstieg ca : sortedPoints.keySet()) { // ca ... current ascent
            Point t = stack.pop(); // t ... top
            Point ntt = stack.peek(); // ntt ... next-to-top
            stack.push(t); // put top back on stack

            // use determinant for testing left/right turns
            Vector v1 = new Vector(ntt.getX() - t.getX(), ntt.getY() - t.getY());
            Vector v2 = new Vector(sortedPoints.get(ca).getX() - t.getX(), sortedPoints.get(ca).getY() - t.getY());

            Mat2x2 matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));

            // testing for not left-turns
            while(!(matrix.getDeterminant() <= 0)) {
                if(frame != null) {
                    LineSegment line1 = new LineSegment(ntt, t);
                    LineSegment line2 = new LineSegment(t, sortedPoints.get(ca));
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
                v2 = new Vector(sortedPoints.get(ca).getX() - t.getX(), sortedPoints.get(ca).getY() - t.getY());

                matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));
            }

            if(frame != null) {
                LineSegment line1 = new LineSegment(ntt, t);
                LineSegment line2 = new LineSegment(t, sortedPoints.get(ca));
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
            stack.push(sortedPoints.get(ca));
        }

        // generate polygon from points on stack
        Point[] points = new Point[stack.size()];
        stack.toArray(points);

        return new Polygon(points);
    }
}
