package kdtrees;

import java.util.Vector;

public class Point {

    Vector<Comparable> values;

    public Point(Comparable... point) {
        this.values = new Vector<Comparable>();

        for(Comparable c : point) {
            this.values.add(c);
        }
    }

    @Override
    protected Object clone() {
        Point newPoint = new Point();
        newPoint.values = new Vector<Comparable>(this.values);
        return newPoint;
    }

    public Comparable getActualValueAtDimension(int depth) {
        return values.get(depth % values.size());
    }

    public void removeValueAtDimension(int depth) {
        values.remove(depth % values.size());
    }
    
    public boolean isInRange(Range range) {
        for (int i = 0; i < values.size(); i++) {
            if(!(values.get(i).compareTo(range.values.get(i).left ) >= 0 && values.get(i).compareTo(range.values.get(i).right) <= 0)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        for(int i = 0; i < values.size(); i++) {
            if(!values.get(i).equals(((Point) obj).values.get(i))) {
                return false;
            }

        }
        return true;
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return values.toString();
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    public void addValueAtDimension(Comparable a, int depth) {
       values.add((values.size())%depth, a);
        
    }
}
