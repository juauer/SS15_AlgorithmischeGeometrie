package kdtrees;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class KDTree<T extends Point> {
    private Node root;

    public KDTree(int dimension, List<T> points) {
        LinkedList<Integer> dimensions = new LinkedList<Integer>();

        for(int i = 0; i < dimension; ++i)
            dimensions.addLast(i);

        root = new Node(points, dimensions);
    }

    public boolean contains(T point) {
        return root.contains(point);
    }

    public Collection<T> search(Range range) {
        return root.search(range);
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
