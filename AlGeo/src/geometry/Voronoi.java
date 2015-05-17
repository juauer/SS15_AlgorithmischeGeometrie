package geometry;

import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Voronoi {
    private abstract class Event implements Comparable<Event> {
        Point triggersAt;

        public Event(Point triggersAt) {
            this.triggersAt = triggersAt;
        }

        @Override
        public int compareTo(Event e) {
            return triggersAt.getY() < e.triggersAt.getY() ? 1 : -1;
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
                root = new Arc(triggersAt);
                return;
            }

            Arc left = root;

            while(left.right != null &&
                    triggersAt.getX() > Parabola.midIntersection(
                            new Parabola(left.location, triggersAt.getY()),
                            new Parabola(left.right.location, triggersAt.getY())).getX())
                left = left.right;

            Arc right = new Arc(left.location);
            right.right = left.right;
            Arc mid = new Arc(triggersAt, left, right);
            left.right = mid;
            right.left = mid;
            left.createCycleEvent();
            right.createCycleEvent();

            Point intersection = Parabola.intersection(new Parabola(left.location, triggersAt.getY()), triggersAt.getX());
            mid.s1 = new Edge(null, null, left.location, mid.location);
            mid.s2 = mid.s1;
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

            Edge newEdge = new Edge(null, null, arc.left.location, arc.right.location);
            Vertex v = new Vertex(circle.m, arc.left.s2, arc.right.s1, newEdge);
            newEdge.connect(v);
            arc.left.s2.connect(v);
            arc.right.s1.connect(v);
            arc.left.s2 = newEdge;
            arc.right.s1 = newEdge;
            arc.left.right = arc.right;
            arc.right.left = arc.left;

            if(arc.left != null)
                arc.left.createCycleEvent();

            if(arc.right != null)
                arc.right.createCycleEvent();
        }
    }

    private class Arc {
        Point       location;
        Arc         left;
        Arc         right;
        CircleEvent event = null;
        Edge        s1    = null;
        Edge        s2    = null;

        public Arc(Point location, Arc left, Arc right) {
            this.location = location;
            this.left = left;
            this.right = right;
        }

        public Arc(Point location) {
            this(location, null, null);
        }

        public void createCycleEvent() {
            if(event != null) {
                event.valid = false;
                event = null;
            }

            if(left != null && right != null && left.location.getY() < location.getY() && right.location.getY() < location.getY()) {
                Circle circle = Circle.create(left.location, location, right.location);

                if(circle != null) {
                    event = new CircleEvent(this, circle);
                    queue.offer(event);
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
        }
    }

    private class Edge {
        Point  region1;
        Point  region2;
        Vertex v1;
        Vertex v2;

        public Edge(Vertex v1, Vertex v2, Point region1, Point region2) {
            this.region1 = region1;
            this.region2 = region2;
            this.v1 = v1;
            this.v2 = v2;
            edges.add(this);
        }

        void connect(Vertex v) {
            if(v1 == null)
                v1 = v;
            else
                v2 = v;
        }

        void paint(Frame frame, Scene s) {
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

            Point closestBBSite = new Point(2 * maxX, 2 * maxY);
            l = new Line(v1.location, l.n0);

            for(LineSegment bbs : new LineSegment[] {
                    new LineSegment(new Point(minX, minY), new Point(maxX, minY)),
                    new LineSegment(new Point(minX, minY), new Point(minX, maxY)),
                    new LineSegment(new Point(minX, maxY), new Point(maxX, maxY)),
                    new LineSegment(new Point(maxX, minY), new Point(maxX, maxY))
            }) {
                Point is = l.intersectionWith(bbs);

                if(is != null && is.toPosition().substract(mid.toPosition()).length()
                        < closestBBSite.toPosition().substract(mid.toPosition()).length())
                    closestBBSite = is;
            }

            s.add(new Beam(v1.location, closestBBSite), Color.BLACK);
        }
    }

    public LinkedList<Edge> edges = new LinkedList<Edge>();
    PriorityQueue<Event>    queue = new PriorityQueue<Event>();
    protected Arc           root  = null;
    double                  minX, minY = Double.MAX_VALUE;
    double                  maxX, maxY = -Double.MAX_VALUE;

    public void fortunesSweep(Frame frame, Point... points) {
        for(Point p : points) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
            queue.offer(new SiteEvent(p));
        }

        Event e;

        while((e = queue.poll()) != null) {
            e.process();

            if(frame != null && (e instanceof SiteEvent || ((CircleEvent) e).valid)) {
                Scene s = new Scene(1000);
                s.add(new Line(new Point(0, e.triggersAt.getY()), new Point(1, e.triggersAt.getY())), Color.BLACK);
                double y = e.triggersAt.getY() - 0.001;
                Arc a = root;

                do {
                    Parabola mid = new Parabola(a.location, y);

                    if(a.left != null)
                        mid.minX = Parabola.midIntersection(new Parabola(a.left.location, y), mid).getX();

                    if(a.right != null)
                        mid.maxX = Parabola.midIntersection(mid, new Parabola(a.right.location, y)).getX();

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

                s.add(e.triggersAt, e instanceof SiteEvent ? Color.RED : Color.ORANGE);
                frame.addScene(s);
            }
        }

        for(Arc a = root; a.right != null; a = a.right)
            if(a.s2 != null && a.right.s1 != null && a.s2 != a.right.s1) {
                Vertex v = new Vertex(Parabola.midIntersection(
                        new Parabola(a.location, -100),
                        new Parabola(a.right.location, -100)
                        ), a.s2, a.right.s1);
                a.s2.connect(v);
                a.right.s1.connect(v);
            }

        if(frame != null) {
            Scene s = new Scene(5000);

            for(Edge edge : edges)
                edge.paint(frame, s);

            frame.addScene(s);
        }
    }
}
