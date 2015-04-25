package geometry;

import java.util.LinkedList;

public class Polygon {
    private class BHDNode {
        protected final BHDNode     left;
        protected final BHDNode     right;
        protected final LineSegment line;

        public BHDNode(BHDNode left, BHDNode right, LineSegment line) {
            this.left = left;
            this.right = right;
            this.line = line;
        }
    }

    public final Point[]  points;
    private final BHDNode bHDRoot;

    public Polygon(Point... points) {
        this.points = points;
        LinkedList<BHDNode> p1 = new LinkedList<BHDNode>();
        LinkedList<BHDNode> p2 = new LinkedList<BHDNode>();

        for(int i = 0; i < points.length - 1; ++i)
            p1.add(new BHDNode(null, null, new LineSegment(points[i], points[i + 1])));

        p1.add(new BHDNode(null, null, new LineSegment(points[points.length - 1], points[0])));

        while(p1.size() > 1) {
            while(p1.size() > 1)
                p2.add(new BHDNode(p1.get(0), p1.get(1), new LineSegment(p1.removeFirst().line.p1, p1.removeFirst().line.p2)));

            if(p1.size() == 1)
                p2.add(p1.removeFirst());

            p1.addAll(p2);
            p2.clear();
        }

        bHDRoot = p1.getFirst();
    }

    private boolean contains(Point point, BHDNode node) {
        if(node.left == null && node.right == null)
            return false;

        boolean leftOfLeftChild = point.distanceTo(node.left.line) > C.E;
        boolean leftOfRightChild = point.distanceTo(node.right.line) > C.E;

        if(leftOfLeftChild && leftOfRightChild)
            return false;

        if(!leftOfLeftChild && !leftOfRightChild)
            return true;

        if(leftOfLeftChild)
            return contains(point, node.left);

        return contains(point, node.right);
    }

    public boolean contains(Point point) {
        return contains(point, bHDRoot);
    }
}
