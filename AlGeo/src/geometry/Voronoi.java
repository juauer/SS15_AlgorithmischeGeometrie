package geometry;

import geometry.test.Dimensions;
import geometry.test.Drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.LinkedList;

public class Voronoi implements Drawable {
    public class Region {
        public Point            location;
        public LinkedList<Edge> edges = new LinkedList<Edge>();

        public Region(Point location) {
            this.location = location;
            regions.add(this);
        }
    }

    public class Vertex {
        public Point  location;
        public Edge[] edges;

        public Vertex(Point location, Edge... edges) {
            this.location = location;
            this.edges = edges;
            vertices.add(this);

            for(Edge edge : edges)
                edge.connect(this);
        }
    }

    public class Edge implements Drawable {
        public Region region1;
        public Region region2;
        public Vertex vertex1 = null;
        public Vertex vertex2 = null;

        public Edge(Region region1, Region region2) {
            this.region1 = region1;
            this.region2 = region2;
            region1.edges.add(this);
            region2.edges.add(this);
            edges.add(this);
        }

        public void connect(Vertex v) {
            if(vertex1 == null)
                vertex1 = v;
            else
                vertex2 = v;
        }

        @Override
        public void paint(Graphics g, Dimensions dimensions, Color color) {
            if(vertex1 != null && vertex2 != null) {
                new LineSegment(vertex1.location, vertex2.location).paint(g, dimensions, color);
                return;
            }

            Line l = new Line(region1.location, region2.location);
            Point mid = region1.location.add(l.u.multiply(
                    region2.location.substract(region1.location.toPosition()).toPosition().length() / 2));

            if(vertex1 == null && vertex2 == null) {
                new Line(l.u, mid).paint(g, dimensions, color);
                return;
            }

            Vertex v = vertex1 != null ? vertex1 : vertex2;
            Point lowerSite = null;

            for(Edge e2 : v.edges)
                if(e2 != this && e2 != null) {
                    lowerSite = e2.region1 == region1 || e2.region1 == region2
                            ? e2.region2.location : e2.region1.location;
                    break;
                }

            Vector u = vertex1.location.substract(lowerSite.toPosition()).toPosition().length()
                    < mid.substract(lowerSite.toPosition()).toPosition().length()
                    ? new Line(vertex1.location, mid).u : new Line(mid, vertex1.location).u;
            new Beam(vertex1.location, vertex1.location.add(u)).paint(g, dimensions, color);
        }
    }

    public LinkedList<Region> regions  = new LinkedList<Region>();
    public LinkedList<Edge>   edges    = new LinkedList<Edge>();
    public LinkedList<Vertex> vertices = new LinkedList<Vertex>();

    @Override
    public void paint(Graphics g, Dimensions dimensions, Color color) {
        for(Edge edge : edges)
            edge.paint(g, dimensions, color);
    }

    public Triangulation triangulation() {
        Triangulation triangulation = new Triangulation();
        HashMap<Region, Triangulation.Vertex> nodes = new HashMap<Region, Triangulation.Vertex>(regions.size(), 1.0f);

        for(Edge edge : edges) {
            Triangulation.Vertex v1 = nodes.get(edge.region1);
            Triangulation.Vertex v2 = nodes.get(edge.region2);

            if(v1 == null) {
                v1 = triangulation.new Vertex(edge.region1.location);
                nodes.put(edge.region1, v1);
            }

            if(v2 == null) {
                v2 = triangulation.new Vertex(edge.region2.location);
                nodes.put(edge.region2, v2);
            }

            triangulation.new Edge(v1, v2);
        }

        return triangulation;
    }
}
