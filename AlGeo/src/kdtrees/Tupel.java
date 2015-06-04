package kdtrees;

public class Tupel {
    
    public Comparable left;
    public Comparable right;

    public Tupel(Comparable left, Comparable right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String toString() {
        return "[" + left + "," + right + "]" ;
    }
}
