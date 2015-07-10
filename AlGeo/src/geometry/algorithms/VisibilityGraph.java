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
    double[][][]     paths = null;
    double[][]       graph;
    ArrayList<Point> points;

    private VisibilityGraph(Polygon... obstacles) {
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

        for(Polygon poly : obstacles)
            for(int j = 0; j < poly.points.length; ++j) {
                Point p = Point.fromPosition(poly.point(j).toPosition().add(
                        new Line(poly.point(j - 1), poly.point(j + 1)).n0.multiply(0.1)));
                graph.graph[i] = RotationalSweep.visiblePoints(null, p, obstacles);
                graph.graph[i][i] = 0.0d;
                ++i;

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

    private void floydWarshal() {
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

        for(int k = 0; k < graph.length; k++)
            for(int i = 0; i < graph.length; i++)
                for(int j = 0; j < graph.length; j++)
                    if(paths[i][k][0] != Double.MAX_VALUE && paths[k][j][0] != Double.MAX_VALUE) {
                        double newDist = paths[i][k][0] + paths[k][j][0];

                        if(newDist < paths[i][j][0]) {
                            paths[i][j][0] = newDist;
                            paths[i][j][1] = paths[k][j][1];
                        }
                    }
    }

    public LinkedList<Point> shortestPath(Point from, Point to) {
        if(paths == null)
            floydWarshal();

        LinkedList<Point> result = new LinkedList<Point>();
        int f = points.indexOf(from);
        int t = points.indexOf(to);

        while(f != t) {
            f = (int) paths[t][f][1];
            result.add(points.get(f));
        }

        return result;
    }

    public double shortestPathLength(Point from, Point to) {
        if(paths == null)
            floydWarshal();

        return paths[points.indexOf(to)][points.indexOf(from)][0];
    }
}
