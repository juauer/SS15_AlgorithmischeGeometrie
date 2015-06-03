package kdtrees;

import java.util.Comparator;

public class KDComparator implements Comparator<Point> {
    
    int dimension;
    
    public KDComparator(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public int compare(Point o1, Point o2) {
        Comparable a = o1.getActualValueAtDimension(dimension);
        Comparable b = o2.getActualValueAtDimension(dimension);
        return a.compareTo(b);
    }
}