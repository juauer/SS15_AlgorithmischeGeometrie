package kdtrees;

import java.util.Arrays;
import java.util.Collection;

public class KDTree {

    private Node root = new Node();
    private int dimension;
    
    public KDTree(int dimension, Point... points) {
        this.dimension = dimension;
        constructTree(0, points);
    }
    
    private void constructTree(int depth, Point... points) {

        if (points.length > 0) {
            
            // sort at dimension
            KDComparator comp = new KDComparator(depth%dimension);
            Arrays.sort(points, comp);

            
            // add median to tree
            int median = points.length/2;
            add(points[median]);
            
            // do this recursively for left and right regions
            if( median != 0) {

                Point[] below = Arrays.copyOfRange(points, 0, median);
                constructTree(depth + 1, below);
                
                if (points.length > 2) {
                    Point[] above = Arrays.copyOfRange(points, median + 1, points.length);
                    constructTree(depth + 1, above);
                }
            }
        }
    }

    private void add(Point point) {
        Point p = (Point) point.clone();
        root.add(p, p, 0);
    }

    private boolean contains(Point point) {
        Point p = (Point) point.clone();
        return root.contains(p, 0);
    }
    
    private Collection<Point> search(Range range) {
      return  root.search(0, range);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static void main(String[] args) {
        Point[] points = new Point[]{new Point(1,1), new Point(3,3), new Point(4,1), new Point(4,4), new Point(5,2), new Point(7,1),new Point(4,6),new Point(3,6)};
        KDTree kd = new KDTree(2, points);
        System.out.println(kd);
        System.out.println(kd.search(new Range(new Tupel(0,50), new Tupel(0,50))));
    }
}
