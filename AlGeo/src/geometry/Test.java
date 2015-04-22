package geometry;

public class Test {
    public static void main(String[] args) {
        Line l1 = new Line(new Point(1, 2), new Point(0, 1));
        Point p1 = new Point(1, 1);
        System.out.println(p1.distanceTo(l1));
    }
}
