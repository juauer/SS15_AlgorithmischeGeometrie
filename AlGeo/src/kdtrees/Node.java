package kdtrees;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Node<T extends Point> {
    Node leftChild   = null;
    Node rightChild  = null;
    Node middleChild = null;
    T    median;
    int  d;

    protected Node(List<T> points, LinkedList<Integer> dimensions) {
        if(dimensions.isEmpty()) {
            median = points.get(0);
            return;
        }

        d = dimensions.removeFirst();
        LinkedList<Integer> dimensions_mid = new LinkedList<Integer>();
        LinkedList<Integer> dimensions_right = new LinkedList<Integer>();
        dimensions_mid.addAll(dimensions);
        dimensions.addLast(d);
        dimensions_right.addAll(dimensions);
        Collections.sort(points, new Comparator<T>() {

            @Override
            public int compare(T p1, T p2) {
                return p1.getKey(d).compareTo(p2.getKey(d));
            }
        });

        median = points.get(points.size() / 2);
        LinkedList<Point> left = new LinkedList<Point>();
        LinkedList<Point> mid = new LinkedList<Point>();
        LinkedList<Point> right = new LinkedList<Point>();

        for(Point p : points)
            if(p.getKey(d).compareTo(median.getKey(d)) < 0)
                left.add(p);
            else if(p.getKey(d).compareTo(median.getKey(d)) == 0)
                mid.add(p);
            else
                right.add(p);

        if(!left.isEmpty())
            leftChild = new Node(left, dimensions);

        if(!mid.isEmpty())
            middleChild = new Node(mid, dimensions_mid);

        if(!right.isEmpty())
            rightChild = new Node(right, dimensions_right);
    }

    protected boolean contains(T point) {
        if(point.equals(median))
            return true;

        if(point.getKey(d) < median.getKey(d))
            return leftChild == null ? false : leftChild.contains(point);

        if(point.getKey(d) > median.getKey(d))
            return rightChild == null ? false : rightChild.contains(point);

        return middleChild == null ? false : middleChild.contains(point);
    }

    protected Collection<T> search(Range range) {
        Collection<T> result = new HashSet<T>();
        Comparable a = median.getKey(d);
        Tupel r = range.values.get(d);
        Comparable left = r.left;
        Comparable right = r.right;

        // if "a" is inside the actual range
        if(a.compareTo(left) >= 0 && a.compareTo(right) <= 0) {
            result.add(median);

            if(middleChild != null)
                result.addAll(middleChild.search(range));

            if(leftChild != null && a.compareTo(left) > 0)
                result.addAll(leftChild.search(range));

            if(rightChild != null && a.compareTo(right) < 0)
                result.addAll(rightChild.search(range));
        }
        else {
            // if "a" is outside the given range look in left or right child
            if(rightChild != null && a.compareTo(left) < 0)
                result.addAll(rightChild.search(range));
            else if(leftChild != null && a.compareTo(right) > 0)
                result.addAll(leftChild.search(range));
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if(median == null)
            return "";
        sb.append("VALUE : " + median + "\n");
        sb.append("LEFT : " + leftChild.toString() + "\n");
        sb.append("Middle : " + middleChild.toString() + "\n");
        sb.append("RIGHT : " + rightChild.toString() + "\n");
        return sb.toString();
    }
}
