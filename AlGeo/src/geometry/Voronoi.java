package geometry;

import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Voronoi {
    private abstract class Event implements Comparable<Event> {
        Point triggeredAt;

        public Event(Point triggersAt) {
            this.triggeredAt = triggersAt;
        }

        @Override
        public int compareTo(Event e) {
            return triggeredAt.getY() < e.triggeredAt.getY() ? 1 : -1;
        }

        public abstract void process();
    }

    private class SiteEvent extends Event {
        public SiteEvent(Point triggersAt) {
            super(triggersAt);
        }

        @Override
        public void process() {
            if(root == null) {
                root = new Arc(triggeredAt);
                return;
            }

            Arc left = root;

            while(left.right != null &&
                    triggeredAt.getX() > Parabola.midIntersection(
                            new Parabola(left.location, triggeredAt.getY()),
                            new Parabola(left.right.location, triggeredAt.getY())).getX())
                left = left.right;

            Arc right = new Arc(left.location);
            right.right = left.right;

            if(right.right != null)
                right.right.left = right;

            Arc mid = new Arc(triggeredAt, left, right);
            left.right = mid;
            right.left = mid;

            left.createCycleEvent(triggeredAt.getY());
            right.createCycleEvent(triggeredAt.getY());

            mid.edge1 = new Edge(left.location, mid.location);
            mid.edge2 = mid.edge1;
            right.edge1 = mid.edge1;
            right.edge2 = left.edge2;
            left.edge2 = mid.edge1;
        }
    }

    private class CircleEvent extends Event {
        Arc     arc;
        Circle  circle;
        boolean valid = true;

        public CircleEvent(Arc arc, Circle circle) {
            super(new Point(circle.m.getX(), circle.m.getY() - circle.r));
            this.arc = arc;
            this.circle = circle;
        }

        @Override
        public void process() {
            if(!valid)
                return;

            Edge newEdge = new Edge(arc.left.location, arc.right.location);
            Vertex v = new Vertex(circle.m, arc.left.edge2, arc.right.edge1, newEdge);
            newEdge.connect(v);

            arc.left.edge2.connect(v);
            arc.left.edge2 = newEdge;

            arc.right.edge1.connect(v);
            arc.right.edge1 = newEdge;

            arc.left.right = arc.right;
            arc.right.left = arc.left;
            arc.left.createCycleEvent(triggeredAt.getY());
            arc.right.createCycleEvent(triggeredAt.getY());
        }
    }

    private class Arc {
        Point       location;
        Arc         left;
        Arc         right;
        CircleEvent event = null;
        Edge        edge1 = null;
        Edge        edge2 = null;

        public Arc(Point location, Arc left, Arc right) {
            this.location = location;
            this.left = left;
            this.right = right;
        }

        public Arc(Point location) {
            this(location, null, null);
        }

        public void createCycleEvent(double lineY) {
            if(event != null) {
                event.valid = false;
                event = null;
            }

            if(left != null && right != null) {
                Circle circle = Circle.create(left.location, location, right.location);

                if(circle != null) {
                    event = new CircleEvent(this, circle);

                    if(event.triggeredAt.getY() <= lineY)
                        queue.offer(event);
                    else
                        event.valid = false;
                }
            }
        }
    }

    private class Vertex {
        Point  location;
        Edge[] edges;

        public Vertex(Point location, Edge... edges) {
            this.location = location;
            this.edges = edges;
            vertices.add(this);
        }
    }

    private class Edge {
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

    public LinkedList<Edge>        edges    = new LinkedList<Edge>();
    public LinkedList<Vertex>      vertices = new LinkedList<Vertex>();
    protected PriorityQueue<Event> queue    = new PriorityQueue<Event>();
    protected Arc                  root     = null;

    public void fortunesSweep(Frame frame, Point... points) {
        for(Point p : points)
            queue.offer(new SiteEvent(p));

        Event e;

        while((e = queue.poll()) != null) {
            e.process();

            if(frame != null && (e instanceof SiteEvent || ((CircleEvent) e).valid)) {
                Scene s = new Scene(100);
                s.add(new Line(new Point(0, e.triggeredAt.getY()), new Point(1, e.triggeredAt.getY())), Color.BLACK);
                double y = e.triggeredAt.getY() - 0.001;
                Arc a = root;

                do {
                    Parabola mid = new Parabola(a.location, y);

                    if(a.left != null)
                        mid.minX = Parabola.midIntersection(new Parabola(a.left.location, y), mid).getX();

                    if(a.right != null)
                        mid.maxX = Parabola.midIntersection(mid, new Parabola(a.right.location, y)).getX();
                    else
                        mid.maxX = frame.dimensions.range_x;

                    s.add(new LineSegment(a.location, new Point(mid.minX + (mid.maxX - mid.minX) / 2, mid.y(mid.minX + (mid.maxX - mid.minX) / 2))), Color.CYAN);
                    s.add(mid, Color.BLUE);
                    s.add(a.location, Color.BLUE);

                    if(a.event != null)
                        s.add(a.event.circle, Color.GREEN);
                }
                while((a = a.right) != null);

                if(e instanceof CircleEvent)
                    s.add(((CircleEvent) e).circle, Color.ORANGE);

                for(Edge edge : edges)
                    edge.paint(frame, s);

                s.add(e.triggeredAt, e instanceof SiteEvent ? Color.RED : Color.ORANGE);
                frame.addScene(s);
            }
        }

        for(Arc a = root; a.right != null; a = a.right)
            if(a.edge2 != null && a.right.edge1 != null) {
                double y = -2.0d * (Math.abs(a.location.getY()) + Math.abs(a.right.location.getY()));
                Vertex v = new Vertex(Parabola.midIntersection(
                        new Parabola(a.location, y),
                        new Parabola(a.right.location, y)
                        ), a.edge2, a.right.edge1);
                a.edge2.connect(v);
                a.right.edge1.connect(v);
            }

        if(frame != null) {
            Scene s = new Scene(5000);

            for(Edge edge : edges)
                edge.paint(frame, s);

            frame.addScene(s);
        }
    }
}
