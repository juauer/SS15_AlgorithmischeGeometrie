package geometry;

public class LineSegment extends Line {
    public LineSegment(Point p1, Point p2) {
        super(p1, p2);
    }

    @Override
    public Point getIntersection(Line line) {
        Point is = super.getIntersection(line);

        if(is == null || !is.isInsideBoundingBox(this)
                || (line instanceof LineSegment && !is.isInsideBoundingBox((LineSegment) line)))
            return null;

        return is;
    }
}
