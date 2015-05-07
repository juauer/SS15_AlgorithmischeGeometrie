package geometry;

public class MyAnstieg {

    Double m;

    public MyAnstieg(Double m) {
        this.m = m;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof MyAnstieg && Math.abs(m - ((MyAnstieg) o).m) < C.E) {
            System.out.println("schon drin");
            return true;
        }

        System.out.println("nicht drin");
        return false;
    }
}
