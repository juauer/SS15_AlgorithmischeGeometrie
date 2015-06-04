package kdtrees;

public class Range {
    
    public Comparable left;
    public Comparable right;

    public Range(Comparable left, Comparable right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String toString() {
        return "[" + left + "," + right + "]" ;
    }
}
