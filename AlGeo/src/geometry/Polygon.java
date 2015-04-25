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

        bHDRoot = new BHDNode(p1.getFirst().left, p1.removeFirst().right, null);
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

    /**
     * Search recursively for intersections of a line with the polygons hull and
     * store them in an accumulator.
     * 
     * @param line a line
     * @param node the current node of the BHD-Tree
     * @param acc an accumulator
     */
    private void intersectionWith(Line line, BHDNode node, LinkedList<Point> acc) {
        // Once only it may occur, that a linesegment is parallel to the line.
        // In this case, nothing helps but to search both branches. The same can
        // be done at root level
        if(node.line == null || node.line.isParallelTo(line)) {
            intersectionWith(line, node.left, acc);
            intersectionWith(line, node.right, acc);
            return;
        }

        // On leaf level compute intersections immediately and return
        if(node.left == null && node.right == null) {
            Point intersection = node.line.getIntersection(line);

            if(intersection != null)
                acc.add(intersection);

            return;
        }

        // The intersection of the extension of a linesegment with the line
        // gives a hint on which side the line lies. Anyways, if a linesegment
        // itself intersects with the line, the matching branch must always be
        // searched
        Point intersection_extendedLine = new Line(node.line.p1, node.line.p2).getIntersection(line);

        if(node.left.line.getIntersection(line) != null || intersection_extendedLine.distanceTo(node.left.line) > C.E)
            intersectionWith(line, node.left, acc);

        if(node.right.line.getIntersection(line) != null || intersection_extendedLine.distanceTo(node.right.line) > C.E)
            intersectionWith(line, node.right, acc);
    }

    public LineSegment intersectionWith(Line line) {
        LinkedList<Point> acc = new LinkedList<Point>();
        intersectionWith(line, bHDRoot, acc);

        if(acc.size() > 2)
            throw new RuntimeException("WTF: found more than 2 intersections");

        // The line passes the polygon
        if(acc.isEmpty())
            return null;

        // The line intersects the polygon. If it intersects in only one point
        // p, the line p->p is returned
        return new LineSegment(acc.getFirst(), acc.size() > 1 ? acc.get(1) : acc.getFirst());
    }
}
