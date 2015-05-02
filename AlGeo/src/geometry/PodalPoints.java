package geometry;

public class PodalPoints {
    /**
     * Index of podalpoint 1
     */
    public final int   index1;

    /**
     * Index of podalpoint 2
     */
    public final int   index2;

    /**
     * Podalpoint 1
     */
    public final Point point1;

    /**
     * Podalpoint 2
     */
    public final Point point2;

    /**
     * The tangent on point1 as found while rotating the calipers
     */
    public final Line  line1;

    /**
     * The tangent on point2 as found while rotating the calipers
     */
    public final Line  line2;

    /**
     * Only has a meaning when searching for copodal points. If a bridge b has
     * colinear points on any polygon, the shorter bridges that are a subset of
     * b are NOT counted as valid bridges.
     * 
     * -1: Both polygons are located to the right of the line point1->point2.
     * (the line is a bridge from polygon 1 to polygon 2)
     * +1: Both polygons are located to the left of the line point1->point2.
     * (the line is a bridge from polygon 2 to polygon 1)
     * 0: point1->point2 is not a bridge.
     */
    public final int   isBridge;

    public PodalPoints(int index1, int index2, Point point1, Point point2, Line line1, Line line2, int isBridge) {
        this.index1 = index1;
        this.index2 = index2;
        this.point1 = point1;
        this.point2 = point2;
        this.line1 = line1;
        this.line2 = line2;
        this.isBridge = isBridge;
    }

    public PodalPoints(int index1, int index2, Point point1, Point point2, Line line1, Line line2) {
        this(index1, index2, point1, point2, line1, line2, 0);
    }
}
