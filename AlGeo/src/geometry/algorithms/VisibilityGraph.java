package geometry.algorithms;

import geometry.Line;
import geometry.LineSegment;
import geometry.Point;
import geometry.Polygon;
import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;

public class VisibilityGraph {
    double[][] graph;
    Point[]    points;

    private VisibilityGraph(Polygon... obstacles) {
        int n = 0;

        for(Polygon poly : obstacles)
            n += poly.points.length;

        points = new Point[n];
        graph = new double[n][];
        int i = 0;

        for(Polygon poly : obstacles)
            for(Point p : poly.points)
                points[i++] = p;
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

                        for(int l = 0; l < graph.points.length; ++l)
                            if(graph.graph[k][l] != 0 && graph.graph[k][l] < Double.MAX_VALUE)
                                s.add(new LineSegment(graph.points[k], graph.points[l]), c);
                    }

                    s.add(p, Color.BLUE);
                    frame.addScene(s);
                }
            }

        return graph;
    }
}
