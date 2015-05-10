package algorithms;

import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import mats.Mat2x2;

import geometry.MyAnstieg;
import geometry.Point;
import geometry.Points;
import geometry.Polygon;
import geometry.Vector;

public class ConvexHull {
    
    public static Polygon grahamscan(Points pointcloud) {
                
        // get minimum
        Point min = pointcloud.getMinY();
        
        // select only relevant points from point cloud
        Map<MyAnstieg, Point> relevantPoints = pointcloud.getRelevantPoints();
        
        // sort by ascent
        Map<MyAnstieg, Point> sortedPoints = new TreeMap<MyAnstieg, Point>(Collections.reverseOrder());
        for (MyAnstieg a : relevantPoints.keySet()) {
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
        for (MyAnstieg ca : sortedPoints.keySet()) { // ca ... current ascent

            Point t = stack.pop();      // t ... top
            Point ntt = stack.peek();   // ntt ... next-to-top
            stack.push(t);              // put top back on stack
            
            // use determinant for testing left/right turns
            Vector v1 = new Vector(ntt.getX()-t.getX(), ntt.getY() - t.getY());
            Vector v2 = new Vector(sortedPoints.get(ca).getX() - t.getX(), sortedPoints.get(ca).getY() - t.getY());
            
            Mat2x2 matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));
            
            // testing for not left-turns
            while (! (matrix.getDeterminant() <= 0) ) {
                
                // remove last point
                stack.pop();
                
                // have a look at the next point
                t = stack.pop();
                ntt = stack.peek();
                stack.push(t);
                
                v1 = new Vector(ntt.getX()-t.getX(), ntt.getY() - t.getY());
                v2 = new Vector(sortedPoints.get(ca).getX() - t.getX(), sortedPoints.get(ca).getY() - t.getY());

                matrix = new Mat2x2(v1.get(0), v1.get(1), v2.get(0), v2.get(1));
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
