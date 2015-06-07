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

    /**
     * Build the tree:
     * - find the median of points
     * - split the list into three lists: <, =, >
     * - create a new child node for each non empty list
     * 
     * @param points
     * @param dimensions list of indizes of still valid dimensions (ref inline
     *            doc for details)
     */
    protected Node(List<T> points, LinkedList<Integer> dimensions) {
        // at the end of a =-branch there must be only one element left (or
        // several elements with the same key). Elements with duplicate keys are
        // ignored silently. One could add the whole list instead, but we
        // don't.
        if(dimensions.isEmpty()) {
            median = points.get(0);
            return;
        }

        // 'dimensions' keeps track of the dimensions which are not excluded yet
        // by following a =-branch. The first one becomes the split-dimension
        // for this node and is moved to the end of the list for the < and >
        // branches. For the =-branch this dimension is removed from the list.
        d = dimensions.removeFirst();
        LinkedList<Integer> dimensions_mid = new LinkedList<Integer>();
        dimensions_mid.addAll(dimensions);
        dimensions.addLast(d);

        // find the median on dimension d
        Collections.sort(points, new Comparator<T>() {

            @Override
            public int compare(T p1, T p2) {
                return new Double(p1.getKey(d)).compareTo(new Double(p2.getKey(d)));
            }
        });

        median = points.get(points.size() / 2);

        // split the list
        LinkedList<T> points_left = new LinkedList<T>();
        LinkedList<T> points_mid = new LinkedList<T>();
        LinkedList<T> points_right = new LinkedList<T>();

        for(T p : points)
            if(p.getKey(d) < median.getKey(d))
                points_left.add(p);
            else if(p.getKey(d) == median.getKey(d))
                points_mid.add(p);
            else
                points_right.add(p);

        // create a new child for each non empty list
        if(!points_left.isEmpty())
            leftChild = new Node<T>(points_left, dimensions);

        if(!points_mid.isEmpty())
            midChild = new Node<T>(points_mid, dimensions_mid);

        if(!points_right.isEmpty())
            rightChild = new Node<T>(points_right, dimensions);
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

    /**
     * Find all elements in a given range
     * 
     * @param range one dupel (min,max) for each dimension of the data
     * @return list of elements
     */
    protected LinkedList<T> search(double[][] range) {
        LinkedList<T> result = new LinkedList<T>();

        // median is inside range at dimension d
        if(median.getKey(d) >= range[d][0] && median.getKey(d) <= range[d][1]) {
            // check if median is inside range at all dimensions
            boolean inRange = true;

            for(int i = 0; i < range.length; ++i)
                if(median.getKey(i) < range[i][0] || median.getKey(i) > range[i][1]) {
                    inRange = false;
                    break;
                }

            // add valid elements to result set
            if(inRange)
                result.add(median);

            if(midChild != null)
                result.addAll(midChild.search(range));

            if(leftChild != null && median.getKey(d) > range[d][0])
                result.addAll(leftChild.search(range));

            if(rightChild != null && median.getKey(d) < range[d][1])
                result.addAll(rightChild.search(range));
        }
        // median is NOT inside range at dimension d
        else {
            if(rightChild != null && median.getKey(d) < range[d][0])
                result.addAll(rightChild.search(range));
            else if(leftChild != null && median.getKey(d) > range[d][1])
                result.addAll(leftChild.search(range));
        }

        return result;
    }
}
