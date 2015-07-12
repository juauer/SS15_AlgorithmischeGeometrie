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
    // the rotating beam is an instance field to provide accesse from inner
    // anonymous classes
    protected Beam beam;

    /**
     * Find all vertices of given polygons that are visible from a given point.
     * Result is an array containing the distance from the point to all visible
     * vertices.
     */
    private double[] _visiblePoints(Frame frame, Point location, Polygon... obstacles) {
        // A wrapper-class for points that provides access to the vertex of a
        // polygon by either:
        //
        // 1) knowing the polygon and the index of the point inside the polygon
        // ('PP' means 'Point-Pointer' ;) )
        //
        // 2) or knowing the index of the point in the list of all points
        // (needed for mapping purposes)
        class PP implements Comparable<PP> {
            Polygon polygon;
            int     index_poly;
            int     index_global;

            PP(Polygon polygon, int index_poly, int index_global) {
                this.polygon = polygon;
                this.index_poly = index_poly;
                this.index_global = index_global;
            }

            // the natural order of points here is determined by the angle
            // between 'location' and a point
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

        // a simple list containing all points (decoupled from the original
        // polygons)
        LinkedList<PP> points = new LinkedList<PP>();

        // initialize the beam (it points straight to the right)
        beam = new Beam(location, new Point(location.getX() + 1, location.getY()));

        // a red-black tree storing linesegments. The order of two linesegments
        // is given by the distance to the corresponding intersections with the
        // beam
        TreeMap<LineSegment, Object> tree = new TreeMap<LineSegment, Object>(new Comparator<LineSegment>() {

            @Override
            public int compare(LineSegment l1, LineSegment l2) {
                if(l1.p1 == l2.p1 && l1.p2 == l2.p2)
                    return 0;

                double dist1 = Math.abs(beam.intersectionWith(l1).toPosition().substract(location.toPosition()).length());
                double dist2 = Math.abs(beam.intersectionWith(l2).toPosition().substract(location.toPosition()).length());

                // if the intersections are very close, it is highly likely the
                // two lines have a common point. In this case the two
                // linesegments are ordered by the index they have in their
                // polygon
                if(Math.abs(dist1 - dist2) < C.E) {
                    if(l1.p2 == l2.p1)
                        return -1;

                    if(l2.p2 == l1.p1)
                        return 1;
                }

                return dist1 < dist2 ? -1 : 1;
            }
        });

        int index = 0;

        // fill the point-list with all vertices of the polygons. Check for each
        // edge e of the polygons if e intersects with the initial beam - if so,
        // insert e into the tree
        for(Polygon polygon : obstacles)
            for(int i = 0; i < polygon.points.length; ++i) {
                points.add(new PP(polygon, i, index++));
                LineSegment l = polygon.edge(i);

                if(beam.intersectionWith(l) != null)
                    tree.put(l, null);
            }

        // sort the points (by angle to 'location' - see comparator above)
        Collections.sort(points);
        double[] result = new double[points.size()];
        Arrays.fill(result, Double.MAX_VALUE);

        // run the actual algorithm
        for(PP p : points) {
            // rotate the beam to the next point
            beam = new Beam(location, p.point());
            LineSegment edge1 = p.polygon.edge(p.index_poly - 1);
            LineSegment edge2 = p.polygon.edge(p.index_poly);

            // check for the two vertices at p wether they lie left or right of
            // the beam
            boolean e1LeftOfBeam = p.polygon.point(p.index_poly - 1).distanceTo(beam) >= 0;
            boolean e2LeftOfBeam = p.polygon.point(p.index_poly + 1).distanceTo(beam) >= 0;
            boolean unableToRemove = false;

            // remove those edges from the tree which lie left of the beam
            if(e1LeftOfBeam)
                unableToRemove = tree.remove(edge1) == null;

            if(e2LeftOfBeam)
                tree.remove(edge2);

            // who looked at the comparator closely knows, there might be a
            // singularity issue if the beam hits a vertex and both edges have
            // to be removed from the tree. In this case it may be edge1 can not
            // be found in the tree - after removing edge2 the singularity is
            // gone and edge1 can be removed as well
            if(e1LeftOfBeam && unableToRemove)
                tree.remove(edge1);

            // IF (the beam does not intersect the very first edge in the tree
            // OR if the intersection of beam and edge is farther away then p)
            // THEN p is visible
            LineSegment candidateForBlocking = tree.isEmpty() ? null : tree.firstKey();
            Point intersection = candidateForBlocking == null ? null : beam.intersectionWith(candidateForBlocking);

            if(intersection != null
                    && Math.abs(location.toPosition().substract(p.point().toPosition()).length())
                    < Math.abs(location.toPosition().substract(intersection.toPosition()).length()))
                intersection = null;

            if(intersection == null)
                result[p.index_global] = Math.abs(location.toPosition().substract(p.point().toPosition()).length());

            // insert those edges to the tree which lie right of the beam
            if(!e1LeftOfBeam)
                tree.put(edge1, null);

            if(!e2LeftOfBeam)
                tree.put(edge2, null);

            // draw debug
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
