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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

public class RotationalSweep {
    protected Beam beam;

    private double[] _visiblePoints(Frame frame, Point location, Polygon... obstacles) {
        class PP implements Comparable<PP> {
            Polygon polygon;
            int     index_poly;
            int     index_global;

            PP(Polygon polygon, int index_poly, int index_global) {
                this.polygon = polygon;
                this.index_poly = index_poly;
                this.index_global = index_global;
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
                return polygon.points[index_poly];
            }
        }

        LinkedList<PP> points = new LinkedList<PP>();
        beam = new Beam(location, new Point(location.getX() + 1, location.getY()));
        TreeMap<LineSegment, Object> tree = new TreeMap<LineSegment, Object>(new Comparator<LineSegment>() {

            @Override
            public int compare(LineSegment l1, LineSegment l2) {
                if(l1.p1 == l2.p1 && l1.p2 == l2.p2)
                    return 0;

                double dist1 = Math.abs(beam.intersectionWith(l1).toPosition().substract(location.toPosition()).length());
                double dist2 = Math.abs(beam.intersectionWith(l2).toPosition().substract(location.toPosition()).length());

                if(Math.abs(dist1 - dist2) < C.E) {
                    LineSegment left = l1.p2 == l2.p1 ? l1 : l2;
                    LineSegment right = l1.p2 == l2.p1 ? l2 : l1;

                    if(left.p2 == right.p1)
                        if(left == l1)
                            return Math.abs(left.p1.toPosition().substract(location.toPosition()).length())
                            < Math.abs(right.p2.toPosition().substract(location.toPosition()).length()) ? -1 : 1;
                        else
                            return Math.abs(left.p1.toPosition().substract(location.toPosition()).length())
                            < Math.abs(right.p2.toPosition().substract(location.toPosition()).length()) ? 1 : -1;
                }

                return dist1 < dist2 ? -1 : 1;
            }
        });

        int index = 0;

        for(Polygon polygon : obstacles)
            for(int i = 0; i < polygon.points.length; ++i) {
                points.add(new PP(polygon, i, index++));
                LineSegment l = polygon.edge(i);

                if(beam.intersectionWith(l) != null)
                    tree.put(l, null);
            }

        Collections.sort(points);
        double[] result = new double[points.size()];
        Arrays.fill(result, Double.MAX_VALUE);

        for(PP p : points) {
            beam = new Beam(location, p.point());
            LineSegment edge1 = p.polygon.edge(p.index_poly - 1);
            LineSegment edge2 = p.polygon.edge(p.index_poly);
            boolean e1LeftOfBeam = p.polygon.point(p.index_poly - 1).distanceTo(beam) >= 0;
            boolean e2LeftOfBeam = p.polygon.point(p.index_poly + 1).distanceTo(beam) >= 0;

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
                result[p.index_global] = Math.abs(location.toPosition().substract(p.point().toPosition()).length());

            if(!e1LeftOfBeam)
                tree.put(edge1, null);

            if(!e2LeftOfBeam)
                tree.put(edge2, null);

            if(frame != null) {
                Scene scene = new Scene(500);
                scene.add(location, Color.BLUE);

                for(LineSegment l : tree.keySet())
                    scene.add(l, Color.CYAN);

                if(intersection != null) {
                    scene.add(p.point(), Color.RED);
                    scene.add(candidateForBlocking, Color.RED);
                }

                scene.add(beam, Color.YELLOW);

                for(int i = 0; i < result.length; ++i)
                    if(result[i] < Double.MAX_VALUE) {
                        int j = i;
                        int polygon = 0;

                        while(j >= obstacles[polygon].points.length) {
                            j -= obstacles[polygon].points.length;
                            ++polygon;
                        }

                        scene.add(obstacles[polygon].point(j), Color.GREEN);
                    }

                frame.addScene(scene);
            }
        }

        return result;
    }

    public static double[] visiblePoints(Frame frame, Point location, Polygon... obstacles) {
        return new RotationalSweep()._visiblePoints(frame, location, obstacles);
    }
}
