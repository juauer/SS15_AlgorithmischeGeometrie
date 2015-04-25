package geometry;

public class Test {
    public static void main(String[] args) {
        Polygon p = new Polygon(new Point(5, 10), new Point(7, 10), new Point(9, 8), new Point(10, 6), new Point(9, 4),
                new Point(7, 2), new Point(5, 2), new Point(3, 4), new Point(2, 6), new Point(3, 8));
        System.out.println(p.contains(new Point(4, 8)));
        System.out.println(p.contains(new Point(3, 8)));
        System.out.println(p.contains(new Point(3, 9)));
    }
}
