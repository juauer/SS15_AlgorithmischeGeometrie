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
            mid.s1 = new Edge(intersection, null, left.location, mid.location);
            mid.s2 = new Edge(intersection, null, right.location, mid.location);
            left.s2 = mid.s1;
            right.s1 = mid.s2;
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

            Edge s = new Edge(circle.m, null, arc.left.location, arc.right.location);

            if(arc.left != null) {
                arc.left.right = arc.right;
                arc.left.s2 = s;
            }

            if(arc.right != null) {
                arc.right.left = arc.left;
                arc.right.s1 = s;
            }

            if(arc.s1 != null)
                arc.s1.p2 = circle.m;
            if(arc.s2 != null)
                arc.s2.p2 = circle.m;

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

            if(left != null && right != null) {
                Circle circle = Circle.create(left.location, location, right.location);

                if(circle != null) {
                    event = new CircleEvent(this, circle);
                    queue.offer(event);
                }
            }
        }
    }

    private class Edge {
        Point region1;
        Point region2;
        Point p1;
        Point p2;

        public Edge(Point p1, Point p2, Point region1, Point region2) {
            this.region1 = region1;
            this.region2 = region2;
            this.p1 = p1;
            this.p2 = p2;
            edges.add(this);
        }
    }

    public LinkedList<Edge> edges = new LinkedList<Edge>();
    PriorityQueue<Event>    queue = new PriorityQueue<Event>();
    protected Arc           root  = null;

    public void fortunesSweep(Frame frame, Point... points) {
        for(Point p : points)
            queue.offer(new SiteEvent(p));

        Event e;

        while((e = queue.poll()) != null) {
            e.process();

            if(frame != null) {
                Scene s = new Scene(1000);
                s.add(new Line(new Point(0, e.triggersAt.getY()), new Point(1, e.triggersAt.getY())), Color.BLACK);
                Arc a = root;

                do {
                    Parabola mid = new Parabola(a.location, e.triggersAt.getY());

                    if(a.left != null)
                        mid.minX = Parabola.midIntersection(new Parabola(a.left.location, e.triggersAt.getY()), mid).getX();

                    if(a.right != null)
                        mid.maxX = Parabola.midIntersection(mid, new Parabola(a.right.location, e.triggersAt.getY())).getX();

                    s.add(mid, Color.BLUE);
                    s.add(a.location, Color.BLUE);

                    if(a.event != null)
                        s.add(a.event.circle, Color.GREEN);
                }
                while((a = a.right) != null);

                if(e instanceof CircleEvent)
                    s.add(((CircleEvent) e).circle, Color.ORANGE);

                for(Edge edge : edges) {
                    if(edge.p2 == null) {
                        Vector v = new Line(edge.region1, edge.region2).n0;

                        if(v.get(1) < 0)
                            v = v.multiply(-1);

                        edge.p2 = edge.p1.add(v.multiply(Math.max(frame.dimensions.range_x, frame.dimensions.range_y)));
                    }

                    s.add(new LineSegment(edge.p1, edge.p2), Color.BLACK);
                }

                s.add(e.triggersAt, e instanceof SiteEvent ? Color.RED : Color.ORANGE);
                frame.addScene(s);
            }
        }

        for(Arc a = root; a.right != null; a = a.right) {
            if(a.s1 != null)
                a.s1.p2 = (Parabola.midIntersection(new Parabola(a.location, -1), new Parabola(a.right.location, -1)));
        }

        if(frame != null) {
            Scene s = new Scene(5000);

            for(Edge edge : edges) {
                if(edge.p2 == null) {
                    Vector v = new Line(edge.region1, edge.region2).n0;

                    if(v.get(1) < 0)
                        v = v.multiply(-1);

                    edge.p2 = edge.p1.add(v.multiply(Math.max(frame.dimensions.range_x, frame.dimensions.range_y)));
                }

                s.add(new LineSegment(edge.p1, edge.p2), Color.BLACK);
            }

            frame.addScene(s);
        }
    }
}
