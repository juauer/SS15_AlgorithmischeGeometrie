package geometry;

public class Test {
    public static void main(String[] args) {
        LineSegment l1 = new LineSegment(new Point(0, 0), new Point(1, 1));
        LineSegment l2 = new LineSegment(new Point(0, 2), new Point(1, 2));
        System.out.println(l1.getIntersection(l2));
    }
}
