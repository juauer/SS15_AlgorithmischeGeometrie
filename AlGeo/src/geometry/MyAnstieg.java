package geometry;

public class MyAnstieg implements Comparable<MyAnstieg>{

    private Double m;

    public MyAnstieg(Double m) {
        this.m = m;
    }
    
    public Double get() {
        return m;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof MyAnstieg && Math.abs(m - ((MyAnstieg) o).m) < C.E) {
            return true;
        } 
        return false;
    }

    @Override
    public int compareTo(MyAnstieg o) {
        return this.m.compareTo(o.m);
    }
}
