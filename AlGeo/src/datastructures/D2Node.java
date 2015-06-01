package datastructures;

import geometry.Point;

public abstract class D2Node {
    
    public double value;
    public Point point;
    
    private D2Node bigger;
    private D2Node smaller;
    private D2Node equal;
    
    public D2Node(double value, Point point){
        this.value = value;
        this.point = point;
        this.bigger = null;
        this.smaller = null;
        this.equal = null;
    }
    
    D2Node getBigger() {
        return bigger;
    }
    
    D2Node getSmaller() {
        return smaller;
    }
    
    D2Node getEqual() {
        return equal;
    }
    
    public abstract boolean isXvertical();
    
    public abstract int isSmaller(Point point);
    
    public abstract D2Node createChild(Point point);
    
    public void setBigger(D2Node node) {
        this.bigger = node;
    }
    
    public void setSmaller(D2Node node) {
        this.smaller = node;
    }
    
    public void setEqual(D2Node node) {
        this.equal = node;
    }
}
