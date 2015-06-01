package datastructures;

import geometry.Point;

public class YVerticalNode extends D2Node{

    public YVerticalNode(Point point) {
        super(point.getY(), point);
    }

    @Override
    public boolean isXvertical() {
        return false;
    }

    @Override
    public int isSmaller(Point point) {
        if(point.getY() < this.value) {
            return 1;
        } else {
            if (point.getY() > this.value) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public D2Node createChild(Point point) {
        return new XVerticalNode(point);
    }
}
