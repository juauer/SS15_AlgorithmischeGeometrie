package geometry.test;

public class Dimensions {
    private static final int UNIT = 20;
    public final int         range_x;
    public final int         range_y;
    public final int         width;
    public final int         height;

    public Dimensions(int range_x, int range_y) {
        this.range_x = range_x;
        this.range_y = range_y;
        this.width = range_x * UNIT;
        this.height = range_y * UNIT;
    }

    public int xToInt(double x) {
        return 50 + (int) Math.round(x * UNIT);
    }

    public int yToInt(double y) {
        return height + 50 - (int) Math.round(y * UNIT);
    }
}
