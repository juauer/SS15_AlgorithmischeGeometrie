package kdtrees;

import java.util.LinkedList;
import java.util.List;

public class KDTree<T extends KDKey> {
    private Node<T> root;

    public KDTree(int dimension, List<T> points) {
        LinkedList<Integer> dimensions = new LinkedList<Integer>();

        for(int i = 0; i < dimension; ++i)
            dimensions.addLast(i);

        root = new Node<T>(points, dimensions);
    }

    public boolean contains(T point) {
        return root.contains(point);
    }

    public LinkedList<T> search(double[][] range) {
        return root.search(range);
    }
}
