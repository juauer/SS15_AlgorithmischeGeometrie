package kdtrees;

import java.util.Collection;
import java.util.HashSet;

public class Node {

    Node  leftChild;
    Node  rightChild;
    Node  middleChild;
    Point value;

    protected void add(Point point, int depth) {
        System.out.println(point + " " + depth);
        if(value == null) {
            this.value = point;
            this.leftChild = new Node();
            this.rightChild = new Node();
            this.middleChild = new Node();
            return;
        }
        else {
            Comparable actualValue = value.getActualValueAtDimension(depth);
            Comparable pointValue = point.getActualValueAtDimension(depth);

            switch(pointValue.compareTo(actualValue)) {
                case -1:
                    // smaller:
                    leftChild.add(point, depth + 1);
                    break;

                case 1:
                    // bigger:
                    rightChild.add(point, depth + 1);
                    break;

                default:
                    // actualValues same as pointValue, at same dimension
                    // do not remove if it is the last dimension already
                    if(point.values.size() > 1) {
                        Point tmp = (Point) point.clone();
                        tmp.removeValueAtDimension(depth);
                        middleChild.add(tmp, 0);
                    }
                    break;
            }
        }
    }

    protected boolean contains(Point point, int depth) {
        if(point.equals(value)) {
            return true;
        }
        else if(value == null) {
            return false;
        }
        else

        {
            Comparable actualValue = value.getActualValueAtDimension(depth);
            Comparable pointValue = point.getActualValueAtDimension(depth);

            switch(pointValue.compareTo(actualValue)) {
                case -1:
                    // smaller: point is smaller that value
                    return leftChild.contains(point, depth + 1);
                case 1:
                    // bigger:
                    return rightChild.contains(point, depth + 1);
                default:
                    // actualValueis same as pointValue, so same dimension
                    Point tmp = (Point) point.clone();
                    tmp.removeValueAtDimension(depth);
                    return middleChild.contains(tmp, 0);
            }
        }

    }

    protected Collection<Point> search(int depth, Range range) {
        Collection<Point> result = new HashSet<Point>();
        if(value != null) {

            Comparable a = value.getActualValueAtDimension(depth);
            Tupel r = range.values.get(depth % range.values.size());
            Comparable left = r.left;
            Comparable right = r.right;

           
            
            
            // if "a" is inside the actual range
            if(a.compareTo(left) >= 0 && a.compareTo(right) <= 0) {

                if(value.isInRange(range) == true) {
                    result.add(value);
                }

                if(a.compareTo(left) > 0) {
                    result.addAll(leftChild.search(depth + 1, range));
                }
                if(a.compareTo(right) < 0) {
                    result.addAll(rightChild.search(depth + 1, range));
                }

                // handle middleChild, remove a range
                Range tmp = (Range) range.clone();
                tmp.removeValueAtDimension(depth);
           
                Collection<Point> temp = middleChild.search(depth + 1, tmp); 
                for(Point p: temp){
                    p.addValueAtDimension(a,depth+1);
                }
                result.addAll(temp);
                
            }
            else {
                // if "a" is outside the given range look in left or right child
                if(a.compareTo(left) < 0) {
                    result.addAll(rightChild.search(depth + 1, range));
                }
                else {
                    if(a.compareTo(right) > 0) {
                        result.addAll(leftChild.search(depth + 1, range));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if(value == null)
            return "";
        sb.append("VALUE : " + value + "\n");
        sb.append("LEFT : " + leftChild.toString() +"\n");
        sb.append("Middle : " + middleChild.toString()+"\n");
        sb.append("RIGHT : " + rightChild.toString() +"\n");
        return sb.toString();
    }
}
