package geometry;

public class PodalPoints {
    public final int     index1;
    public final int     index2;
    public final Point   point1;
    public final Point   point2;
    public final Line    line1;
    public final Line    line2;
    public final boolean isBridge;

    public PodalPoints(int index1, int index2, Point point1, Point point2, Line line1, Line line2, boolean isBridge) {
        this.index1 = index1;
        this.index2 = index2;
        this.point1 = point1;
        this.point2 = point2;
        this.line1 = line1;
        this.line2 = line2;
        this.isBridge = isBridge;
    }

    public PodalPoints(int index1, int index2, Point point1, Point point2, Line line1, Line line2) {
        this(index1, index2, point1, point2, line1, line2, false);
    }
}
