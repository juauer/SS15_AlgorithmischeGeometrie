package datastructures;

import geometry.Point;

public class XVerticalNode extends D2Node{

    public XVerticalNode(Point point) {
        super(point.getX(), point);
    }

    @Override
    public boolean isXvertical() {
        return true;
    }

    @Override
    public int isSmaller(Point point) {
        if(point.getX() < this.value) {
            return 1;
        } else {
            if (point.getX() > this.value) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public D2Node createChild(Point point) {
        return new YVerticalNode(point);
    }
}
