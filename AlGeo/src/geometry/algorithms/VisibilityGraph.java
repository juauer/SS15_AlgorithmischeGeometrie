package geometry.algorithms;

import geometry.Line;
import geometry.LineSegment;
import geometry.Point;
import geometry.Polygon;
import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

public class VisibilityGraph {
    // all pairs shortest paths. At dimension 3 there are two values: [0] stores
    // the weight of the path, [1] stores the index of the next vertex on the
    // path
    double[][][]     paths = null;

    // the visibility graph. For values equal to Double.MAX_VALUE two vertices
    // do not see each other
    double[][]       graph;

    // all vertices of the polygons as list. The indices are corresponding to
    // those used for the arrays above
    ArrayList<Point> points;

    private VisibilityGraph(Polygon... obstacles) {
        // nothing special here. Put all points into a list, and allocate memory
        // for the arrays
        int n = 0;

        for(Polygon poly : obstacles)
            n += poly.points.length;

        points = new ArrayList<Point>(n);
        graph = new double[n][];

        for(Polygon poly : obstacles)
            for(Point p : poly.points)
                points.add(p);
    }

    public static VisibilityGraph create(Frame frame, Polygon... obstacles) {
        VisibilityGraph graph = new VisibilityGraph(obstacles);
        int i = 0;

        // for each point, run the rotational sweep
        for(Polygon poly : obstacles)
            for(int j = 0; j < poly.points.length; ++j) {
                // the beams origin for the rotational sweep must not be within
                // any polygon. Pull it a little outside ...
                Point p = Point.fromPosition(poly.point(j).toPosition().add(
                        new Line(poly.point(j - 1), poly.point(j + 1)).n0.multiply(0.1)));

                // output of the rotational sweep is one line of the result
                graph.graph[i] = RotationalSweep.visiblePoints(null, p, obstacles);

                // the weight of edge i->i is 0
                graph.graph[i][i] = 0.0d;
                ++i;

                // draw debug
                if(frame != null) {
                    Scene s = new Scene(500);
                    Color c = Color.RED;

                    for(int k = 0; k < i; ++k) {
                        if(k == i - 1)
                            c = Color.GREEN;

                        for(int l = 0; l < graph.points.size(); ++l)
                            if(graph.graph[k][l] != 0 && graph.graph[k][l] < Double.MAX_VALUE)
                                s.add(new LineSegment(graph.points.get(k), graph.points.get(l)), c);
                    }

                    s.add(p, Color.BLUE);
                    frame.addScene(s);
                }
            }

        return graph;
    }

    /**
     * It's the very popular algorithm of Floyd and Warshall ...
     */
    private void floydWarshall() {
        double[][] adjacencyMatrix = new double[graph.length][];

        for(int i = 0; i < graph.length; ++i) {
            adjacencyMatrix[i] = new double[graph.length];
            System.arraycopy(graph[i], 0, adjacencyMatrix[i], 0, graph.length);
        }

        paths = new double[graph.length][graph.length][2];

        for(int i = 0; i < graph.length; i++)
            for(int j = 0; j < graph.length; j++) {
                paths[i][j][0] = graph[i][j];

                if(i == j || adjacencyMatrix[i][j] == Double.MAX_VALUE)
                    paths[i][j][1] = -1;
                else
                    paths[i][j][1] = i;
            }

        // above: initialization
        // =======================
        // below: actual algorithm

        for(int k = 0; k < graph.length; k++)
            for(int i = 0; i < graph.length; i++)
                for(int j = 0; j < graph.length; j++)
                    if(paths[i][k][0] != Double.MAX_VALUE && paths[k][j][0] != Double.MAX_VALUE) {
                        double newDist = paths[i][k][0] + paths[k][j][0];

                        // the only 'special' thing on this implementation is
                        // that the weight and the next vertex on the path are
                        // stored in the same array (as explained at top of this
                        // file)
                        if(newDist < paths[i][j][0]) {
                            paths[i][j][0] = newDist;
                            paths[i][j][1] = paths[k][j][1];
                        }
                    }
    }

    /**
     * List the vertices on the shortest path from 'from' to 'to'
     */
    public LinkedList<Point> shortestPath(Point from, Point to) {
        if(paths == null)
            floydWarshall();

        LinkedList<Point> result = new LinkedList<Point>();
        int f = points.indexOf(from);
        int t = points.indexOf(to);

        while(f != t) {
            f = (int) paths[t][f][1];
            result.add(points.get(f));
        }

        return result;
    }

    /**
     * Get the length of the shortest path from 'from' to 'to'
     */
    public double shortestPathLength(Point from, Point to) {
        if(paths == null)
            floydWarshall();

        return paths[points.indexOf(to)][points.indexOf(from)][0];
    }
}
