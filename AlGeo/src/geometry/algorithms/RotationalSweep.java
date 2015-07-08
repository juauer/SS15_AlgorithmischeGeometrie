package geometry.algorithms;

import geometry.Beam;
import geometry.C;
import geometry.Line;
import geometry.LineSegment;
import geometry.Point;
import geometry.Polygon;
import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class RotationalSweep {
    protected Beam beam;

    private List<Point> _visiblePoints(Frame frame, Point location, Polygon... obstacles) {
        LinkedList<Point> result = new LinkedList<Point>();

        class PP implements Comparable<PP> {
            Polygon polygon;
            int     index;

            PP(Polygon polygon, int index) {
                this.polygon = polygon;
                this.index = index;
            }

            @Override
            public int compareTo(PP p) {
                Line axis = new Line(location, new Point(location.getX() + 1, location.getY()));
                double angle1 = new Line(location, point()).angleTo(axis);
                double angle2 = new Line(location, p.point()).angleTo(axis);

                if(angle1 < 0)
                    angle1 += 2 * Math.PI;

                if(angle2 < 0)
                    angle2 += 2 * Math.PI;

                return angle1 == angle2 ? 0 : angle1 < angle2 ? -1 : 1;
            }

            Point point() {
                return polygon.points[index];
            }
        }

        LinkedList<PP> points = new LinkedList<PP>();
        beam = new Beam(location, new Point(location.getX() + 1, location.getY()));
        TreeMap<LineSegment, Object> tree = new TreeMap<LineSegment, Object>(new Comparator<LineSegment>() {

            @Override
            public int compare(LineSegment l1, LineSegment l2) {
                if(l1.p1.equals(l2.p1) && l1.p2.equals(l2.p2))
                    return 0;

                double dist1 = Math.abs(beam.intersectionWith(new Line(l1.p1, l1.p2)).toPosition().substract(location.toPosition()).length());
                double dist2 = Math.abs(beam.intersectionWith(new Line(l2.p1, l2.p2)).toPosition().substract(location.toPosition()).length());

                if(Math.abs(dist1 - dist2) < C.E) {
                    Point p1 = l1.p1.equals(l2.p1) || l1.p1.equals(l2.p2) ? l1.p2 : l1.p1;
                    Point p2 = l2.p1.equals(l1.p1) || l2.p1.equals(l1.p2) ? l2.p2 : l2.p1;
                    return Math.abs(p1.toPosition().substract(location.toPosition()).length())
                    < Math.abs(p2.toPosition().substract(location.toPosition()).length()) ? -1 : 1;
                }

                return dist1 < dist2 ? -1 : 1;
            }
        });

        for(Polygon polygon : obstacles)
            for(int i = 0; i < polygon.points.length; ++i) {
                points.add(new PP(polygon, i));
                LineSegment l = polygon.edge(i);

                if(beam.intersectionWith(l) != null)
                    tree.put(l, null);
            }

        Collections.sort(points);

        for(PP p : points) {
            beam = new Beam(location, p.point());
            LineSegment edge1 = p.polygon.edge(p.index - 1);
            LineSegment edge2 = p.polygon.edge(p.index);
            boolean e1LeftOfBeam = p.polygon.point(p.index - 1).distanceTo(beam) > 0;
            boolean e2LeftOfBeam = p.polygon.point(p.index + 1).distanceTo(beam) > 0;

            if(e1LeftOfBeam)
                tree.remove(edge1);

            if(e2LeftOfBeam)
                tree.remove(edge2);

            LineSegment candidateForBlocking = tree.isEmpty() ? null : tree.firstKey();
            Point intersection = candidateForBlocking == null ? null : beam.intersectionWith(candidateForBlocking);

            if(intersection != null
                    && Math.abs(location.toPosition().substract(p.point().toPosition()).length())
                    < Math.abs(location.toPosition().substract(intersection.toPosition()).length()))
                intersection = null;

            if(intersection == null)
                result.add(p.point());

            if(!e1LeftOfBeam)
                tree.put(edge1, null);

            if(!e2LeftOfBeam)
                tree.put(edge2, null);

            if(frame != null) {
                Scene scene = new Scene(500);

                for(LineSegment l : tree.keySet())
                    scene.add(l, Color.CYAN);

                if(intersection != null)
                    scene.add(candidateForBlocking, Color.RED);

                scene.add(beam, Color.YELLOW);

                for(Point pp : result)
                    scene.add(pp, Color.GREEN);

                frame.addScene(scene);
            }
        }

        return result;
    }

    public static List<Point> visiblePoints(Frame frame, Point location, Polygon... obstacles) {
        return new RotationalSweep()._visiblePoints(frame, location, obstacles);
    }
}
