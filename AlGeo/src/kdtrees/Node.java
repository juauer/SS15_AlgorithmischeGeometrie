package kdtrees;

public class Node {

    Node  leftChild;
    Node  rightChild;
    Node  middleChild;
    Point value;

    protected void add(Point point, int depth) {
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
                    Point tmp = (Point) point.clone();
                    tmp.removeValueAtDimension(depth);
                    middleChild.add(tmp, 0);
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

    protected void search(int depth, Range... range) {

        if (value != null) {

            Comparable a = value.getActualValueAtDimension(depth);
            Range r = range[depth%range.length];
            Comparable left = r.left;
            Comparable right = r.right;

            // a is inside the actual range
            if(a.compareTo(left) >= 0 && a.compareTo(right) <= 0 ){

                if(value.isInRange(range) == true) {
                    System.out.println(value);
                }

                if(!isLeafe()) {
                    if(a.compareTo(left) > 0 ) {
                        leftChild.search(depth + 1, range);
                    }
                    middleChild.search(depth + 1, range);
                    if(a.compareTo(right) < 0) {
                        rightChild.search(depth + 1, range);
                    }

                }
            } else {
                // if "a" is outside the given range look in left or right child
                if(a.compareTo(left) < 0) {
                    rightChild.search( depth + 1, range);
                } else {
                    if (a.compareTo(right) > 0) {
                        leftChild.search(depth +1, range);
                    }
                }
            }
        }
    }

    private boolean isLeafe() {
        if (leftChild.value == null && rightChild.value == null) {
            return true;
        }
        return false;
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
