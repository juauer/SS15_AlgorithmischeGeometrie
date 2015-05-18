package geometry;

import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.LinkedList;

public class Voronoi {
    public class Vertex {
        Point  location;
        Edge[] edges;

        public Vertex(Point location, Edge... edges) {
            this.location = location;
            this.edges = edges;
            vertices.add(this);
        }
    }

    public class Edge {
        Point  region1;
        Point  region2;
        Vertex v1 = null;
        Vertex v2 = null;

        public Edge(Point region1, Point region2) {
            this.region1 = region1;
            this.region2 = region2;
            edges.add(this);
        }

        public void connect(Vertex v) {
            if(v1 == null)
                v1 = v;
            else
                v2 = v;
        }

        public void paint(Frame frame, Scene s) {
            if(v1 != null && v2 != null) {
                s.add(new LineSegment(v1.location, v2.location), Color.BLACK);
                return;
            }

            Line l = new Line(region1, region2);
            Point mid = region1.add(l.u.multiply(region2.substract(region1.toPosition()).toPosition().length() / 2));

            if(v1 == null && v2 == null) {
                s.add(new Line(l.u, mid), Color.BLACK);
                return;
            }

            Vertex v = v1 != null ? v1 : v2;
            Point lowerSite = null;

            for(Edge e2 : v.edges)
                if(e2 != this && e2 != null) {
                    lowerSite = e2.region1 == region1 || e2.region1 == region2 ? e2.region2 : e2.region1;
                    break;
                }

            Vector u = v1.location.substract(lowerSite.toPosition()).toPosition().length()
                    < mid.substract(lowerSite.toPosition()).toPosition().length() ? new Line(v1.location, mid).u : new Line(mid, v1.location).u;
            s.add(new Beam(v1.location, v1.location.add(u)), Color.BLACK);
        }
    }

    public LinkedList<Edge>   edges    = new LinkedList<Edge>();
    public LinkedList<Vertex> vertices = new LinkedList<Vertex>();
}
