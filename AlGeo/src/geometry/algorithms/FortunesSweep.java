package geometry.algorithms;

import geometry.Circle;
import geometry.Line;
import geometry.LineSegment;
import geometry.Parabola;
import geometry.Point;
import geometry.Voronoi;
import geometry.Voronoi.Edge;
import geometry.Voronoi.Region;
import geometry.test.Frame;
import geometry.test.Scene;

import java.awt.Color;
import java.util.PriorityQueue;

public class FortunesSweep {
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
        Region region;

        public SiteEvent(Region region) {
            super(region.location);
            this.region = region;
        }

        @Override
        public void process() {
            if(root == null) {
                root = new Arc(region);
                return;
            }

            Arc left = root;

            while(left.right != null &&
                    triggeredAt.getX() > Parabola.midIntersection(
                            new Parabola(left.region.location, triggeredAt.getY()),
                            new Parabola(left.right.region.location, triggeredAt.getY())).getX())
                left = left.right;

            Arc right = new Arc(left.region);
            right.right = left.right;

            if(right.right != null)
                right.right.left = right;

            Arc mid = new Arc(region, left, right);
            left.right = mid;
            right.left = mid;

            left.createCycleEvent(triggeredAt.getY());
            right.createCycleEvent(triggeredAt.getY());

            mid.edge1 = voronoi.new Edge(left.region, mid.region);
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

            Edge newEdge = voronoi.new Edge(arc.left.region, arc.right.region);
            voronoi.new Vertex(circle.m, arc.left.edge2, arc.right.edge1, newEdge);
            arc.left.edge2 = newEdge;
            arc.right.edge1 = newEdge;
            arc.left.right = arc.right;
            arc.right.left = arc.left;
            arc.left.createCycleEvent(triggeredAt.getY());
            arc.right.createCycleEvent(triggeredAt.getY());
        }
    }

    private class Arc {
        Region      region;
        Arc         left;
        Arc         right;
        CircleEvent event = null;
        Edge        edge1 = null;
        Edge        edge2 = null;

        public Arc(Region region, Arc left, Arc right) {
            this.region = region;
            this.left = left;
            this.right = right;
        }

        public Arc(Region region) {
            this(region, null, null);
        }

        public void createCycleEvent(double lineY) {
            if(event != null) {
                event.valid = false;
                event = null;
            }

            if(left != null && right != null) {
                Circle circle = Circle.create(left.region.location, region.location, right.region.location);

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

    protected Voronoi              voronoi = new Voronoi();
    protected PriorityQueue<Event> queue   = new PriorityQueue<Event>();
    protected Arc                  root    = null;

    public Voronoi fortunesSweep(Frame frame, Point... points) {
        for(Point p : points)
            queue.offer(new SiteEvent(voronoi.new Region(p)));

        Event e;

        while((e = queue.poll()) != null) {
            e.process();

            if(frame != null && (e instanceof SiteEvent || ((CircleEvent) e).valid)) {
                Scene s = new Scene(100);
                s.add(new Line(new Point(0, e.triggeredAt.getY()), new Point(1, e.triggeredAt.getY())), Color.BLACK);
                double y = e.triggeredAt.getY() - 0.001;
                Arc a = root;

                do {
                    Parabola mid = new Parabola(a.region.location, y);

                    if(a.left != null)
                        mid.minX = Parabola.midIntersection(new Parabola(a.left.region.location, y), mid).getX();

                    if(a.right != null)
                        mid.maxX = Parabola.midIntersection(mid, new Parabola(a.right.region.location, y)).getX();
                    else
                        mid.maxX = frame.dimensions.range_x;

                    s.add(new LineSegment(a.region.location, new Point(mid.minX + (mid.maxX - mid.minX) / 2, mid.y(mid.minX + (mid.maxX - mid.minX) / 2))), Color.CYAN);
                    s.add(mid, Color.BLUE);
                    s.add(a.region.location, Color.BLUE);

                    if(a.event != null)
                        s.add(a.event.circle, Color.GREEN);
                }
                while((a = a.right) != null);

                if(e instanceof CircleEvent)
                    s.add(((CircleEvent) e).circle, Color.ORANGE);

                for(Edge edge : voronoi.edges)
                    s.add(edge, Color.BLACK);

                s.add(e.triggeredAt, e instanceof SiteEvent ? Color.RED : Color.ORANGE);
                frame.addScene(s);
            }
        }

        for(Arc a = root; a.right != null; a = a.right)
            if(a.edge2 != null && a.right.edge1 != null) {
                double y = -2.0d * (Math.abs(a.region.location.getY()) + Math.abs(a.right.region.location.getY()));
                voronoi.new Vertex(Parabola.midIntersection(
                        new Parabola(a.region.location, y),
                        new Parabola(a.right.region.location, y)
                        ), a.edge2, a.right.edge1);
            }

        if(frame != null)
            frame.addScene(new Scene(5000).add(voronoi, Color.BLACK));

        return voronoi;
    }
}
