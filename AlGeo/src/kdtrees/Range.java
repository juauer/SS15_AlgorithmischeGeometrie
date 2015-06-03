package kdtrees;

public class Range {
    
    public Comparable left;
    public Comparable right;

    public Range(Comparable left, Comparable right) {
        this.left = left;
        this.right = right;
    }
    
    public boolean isElement(Comparable value) {
        if(value.compareTo(left) >= 0 && value.compareTo(right) <= 0) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "[" + left + "," + right + "]" ;
    }
}
