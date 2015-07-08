package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Polygon implements Drawable {
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

    public final Point[]       points;
    public final LineSegment[] lines;
    private final BHDNode      bHDRoot;

    public Polygon(Point... points) {
        this.points = points;
        lines = new LineSegment[points.length];
        LinkedList<BHDNode> p1 = new LinkedList<BHDNode>();
        LinkedList<BHDNode> p2 = new LinkedList<BHDNode>();

        for(int i = 0; i < points.length - 1; ++i) {
            lines[i] = new LineSegment(points[i], points[i + 1]);
            p1.add(new BHDNode(null, null, lines[i]));
        }

        lines[lines.length - 1] = new LineSegment(points[points.length - 1], points[0]);
        p1.add(new BHDNode(null, null, lines[lines.length - 1]));

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

    public static LinkedList<PodalPoints> copodalPoints(Polygon polygon1, Polygon polygon2) {
        LinkedList<PodalPoints> result = new LinkedList<PodalPoints>();
        int p1 = 0;
        int p2 = 0;

        for(int i = 1; i < polygon1.points.length; i++)
            if(polygon1.points[i].compareTo(polygon1.points[p1]) < 0)
                p1 = i;

        for(int i = 1; i < polygon2.points.length; i++)
            if(polygon2.points[i].compareTo(polygon2.points[p2]) < 0)
                p2 = i;

        Line caliper1 = new Line(polygon1.points[p1], new Point(polygon1.points[p1].getX(), polygon1.points[p1].getY() + 10.0d));
        Line caliper2 = new Line(polygon2.points[p2], new Point(polygon2.points[p2].getX(), polygon2.points[p2].getY() + 10.0d));
        double rotatedAngle = 0.0d;

        while(rotatedAngle < 2.0d * Math.PI) {
            double angle1 = caliper1.angleTo(polygon1.lines[p1]);
            double angle2 = caliper2.angleTo(polygon2.lines[p2]);
            angle1 = Math.abs(angle1 > 0 ? 2.0d * Math.PI - angle1 : angle1);
            angle2 = Math.abs(angle2 > 0 ? 2.0d * Math.PI - angle2 : angle2);
            double minAngle = Math.min(angle1, angle2);
            caliper1 = new Line(polygon1.points[p1], caliper1.rotate(polygon1.points[p1], minAngle).u);
            caliper2 = new Line(polygon2.points[p2], caliper2.rotate(polygon2.points[p2], minAngle).u);
            rotatedAngle += minAngle;

            if(angle1 < angle2)
                p1 = (p1 + 1) % polygon1.points.length;
            else
                p2 = (p2 + 1) % polygon2.points.length;

            Line support = new Line(polygon1.points[p1], polygon2.points[p2]);
            double side1 = polygon1.points[p1 == 0 ? polygon1.points.length - 1 : p1 - 1].distanceTo(support);
            double side2 = polygon1.points[(p1 + 1) % polygon1.points.length].distanceTo(support);
            double side3 = polygon2.points[p2 == 0 ? polygon2.points.length - 1 : p2 - 1].distanceTo(support);
            double side4 = polygon2.points[(p2 + 1) % polygon2.points.length].distanceTo(support);
            int isBridge = 0;

            if(side1 < -C.E && side2 <= 0 && side3 <= 0 && side4 < -C.E)
                isBridge = -1;
            else if(side1 >= 0 && side2 > C.E && side3 > C.E && side4 >= 0)
                isBridge = 1;

            result.add(new PodalPoints(p1, p2, polygon1.points[p1], polygon2.points[p2], caliper1, caliper2, isBridge));
        }

        if(result.getFirst().point1 == result.getLast().point1 && result.getFirst().point2 == result.getLast().point2)
            result.removeFirst();

        return result;
    }

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        for(int i = 0; i < points.length; ++i) {
            points[i].paint(g, dimensions, color);
            lines[i].paint(g, dimensions, color);
        }
    }

    public Point point(int i) {
        if(i < 0)
            i += points.length;

        return points[i % points.length];
    }

    public LineSegment edge(int i) {
        if(i < 0)
            i += points.length;

        return new LineSegment(points[i], points[i == points.length - 1 ? 0 : i + 1]);
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
            Point intersection = node.line.intersectionWith(line);

            if(intersection != null)
                acc.add(intersection);

            return;
        }

        // The intersection of the extension of a linesegment with the line
        // gives a hint on which side the line lies. Anyways, if a linesegment
        // itself intersects with the line, the matching branch must always be
        // searched
        Point intersection_extendedLine = new Line(node.line.p1, node.line.p2).intersectionWith(line);

        if(node.left.line.intersectionWith(line) != null || intersection_extendedLine.distanceTo(node.left.line) > C.E)
            intersectionWith(line, node.left, acc);

        if(node.right.line.intersectionWith(line) != null || intersection_extendedLine.distanceTo(node.right.line) > C.E)
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

    public LinkedList<PodalPoints> antipodalPoints() {
        LinkedList<PodalPoints> result = new LinkedList<PodalPoints>();
        int p1 = 0;
        int p2 = 0;

        // the first antipodal points are given by the points with the min/max x
        // coordinate
        for(int i = 1; i < points.length; i++) {
            if(points[i].compareTo(points[p1]) < 0)
                p1 = i;

            if(points[i].compareTo(points[p2]) > 0)
                p2 = i;
        }

        // the left/right caliper is constructed 'upwards'/'downwards'
        Line caliper1 = new Line(points[p1], new Point(points[p1].getX(), points[p1].getY() + 10.0d));
        Line caliper2 = new Line(points[p2], new Point(points[p2].getX(), points[p2].getY() - 10.0d));
        double rotatedAngle = 0.0d;

        // do until the calipers rotated a total of 180 degrees
        while(rotatedAngle < Math.PI) {
            // for both calipers: compute the angle between the caliper at point
            // p_i and the linesegment p_i->p_(i+1)
            double angle1 = caliper1.angleTo(lines[p1]);
            double angle2 = caliper2.angleTo(lines[p2]);
            angle1 = Math.abs(angle1 > 0 ? 2.0d * Math.PI - angle1 : angle1);
            angle2 = Math.abs(angle2 > 0 ? 2.0d * Math.PI - angle2 : angle2);

            // choose the smaller angle and rotate both calipers by that angle
            double minAngle = Math.min(angle1, angle2);
            caliper1 = new Line(points[p1], caliper1.rotate(points[p1], minAngle).u);
            caliper2 = new Line(points[p2], caliper2.rotate(points[p2], minAngle).u);
            rotatedAngle += minAngle;

            // move the point that lies on the choosen caliper
            if(angle1 < angle2)
                p1 = (p1 + 1) % points.length;
            else
                p2 = (p2 + 1) % points.length;

            result.add(new PodalPoints(p1, p2, points[p1], points[p2], caliper1, caliper2));
        }

        // just in case we rotated slightly more then 180 degrees, check if the
        // first point is a duplicate of the last one
        if(result.getLast().point1 == result.getFirst().point2 && result.getLast().point2 == result.getFirst().point1)
            result.removeFirst();

        return result;
    }

    public double diameter() {
        double max = 0.0d;

        for(PodalPoints p : antipodalPoints()) {
            double d = Math.abs(p.point1.toPosition().substract(p.point2.toPosition()).length());

            if(d > max)
                max = d;
        }

        return max;
    }
}
