package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Triangulation implements Drawable {

    public class Vertex {
        public Point            location;
        public LinkedList<Edge> edges = new LinkedList<Edge>();

        public Vertex(Point location) {
            this.location = location;
            vertices.add(this);
        }
    }

    public class Edge implements Drawable {
        public Vertex vertex1 = null;
        public Vertex vertex2 = null;

        public Edge(Vertex vertex1, Vertex vertex2) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            edges.add(this);
            vertex1.edges.add(this);
            vertex2.edges.add(this);
        }

        @Override
        public void paint(Graphics g, Dimensions dimensions, Color color) {
            new LineSegment(vertex1.location, vertex2.location).paint(g, dimensions, color);
        }
    }

    public LinkedList<Vertex> vertices = new LinkedList<Vertex>();
    public LinkedList<Edge>   edges    = new LinkedList<Edge>();

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        for(Edge edge : edges)
            edge.paint(g, dimensions, color);
    }
}
