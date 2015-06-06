package kdtrees;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Node<T extends KDKey> {
    Node<T> leftChild  = null;
    Node<T> rightChild = null;
    Node<T> midChild   = null;
    T       median;
    int     d;

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
        LinkedList<T> points_left = new LinkedList<T>();
        LinkedList<T> points_mid = new LinkedList<T>();
        LinkedList<T> points_right = new LinkedList<T>();

        for(T p : points)
            if(p.getKey(d).compareTo(median.getKey(d)) < 0)
                points_left.add(p);
            else if(p.getKey(d).compareTo(median.getKey(d)) == 0)
                points_mid.add(p);
            else
                points_right.add(p);

        if(!points_left.isEmpty())
            leftChild = new Node<T>(points_left, dimensions);

        if(!points_mid.isEmpty())
            midChild = new Node<T>(points_mid, dimensions_mid);

        if(!points_right.isEmpty())
            rightChild = new Node<T>(points_right, dimensions_right);
    }

    protected boolean contains(T point) {
        if(point.equals(median))
            return true;

        if(point.getKey(d) < median.getKey(d))
            return leftChild == null ? false : leftChild.contains(point);

        if(point.getKey(d) > median.getKey(d))
            return rightChild == null ? false : rightChild.contains(point);

        return midChild == null ? false : midChild.contains(point);
    }

    protected LinkedList<T> search(double[][] range) {
        LinkedList<T> result = new LinkedList<T>();

        // if "a" is inside the actual range
        if(median.getKey(d).compareTo(range[d][0]) >= 0 && median.getKey(d).compareTo(range[d][1]) <= 0) {
            result.add(median);

            if(midChild != null)
                result.addAll(midChild.search(range));

            if(leftChild != null && median.getKey(d).compareTo(range[d][0]) > 0)
                result.addAll(leftChild.search(range));

            if(rightChild != null && median.getKey(d).compareTo(range[d][1]) < 0)
                result.addAll(rightChild.search(range));
        }
        // if "a" is outside the given range look in left or right child
        else {
            if(rightChild != null && median.getKey(d).compareTo(range[d][0]) < 0)
                result.addAll(rightChild.search(range));
            else if(leftChild != null && median.getKey(d).compareTo(range[d][1]) > 0)
                result.addAll(leftChild.search(range));
        }

        return result;
    }
}
