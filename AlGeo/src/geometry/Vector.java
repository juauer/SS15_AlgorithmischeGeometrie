package geometry;

import java.util.Locale;

import mats.Mat;

public class Vector {
    public Mat m;

    protected Vector(Mat m) {
        super();
        this.m = m;
    }

    public Vector(double x, double y) {
        this(new Mat(x, y));
    }

    public double get(int i) {
        return m.get(i, 0);
    }

    public Vector substract(Vector v) {
        return new Vector(Mat.subtract(v.m, m));
    }

    public double length() {
        return Math.sqrt(dotProduct(this));
    }

    public double dotProduct(Vector v) {
        return Mat.compose(Mat.transpose(m), v.m).get(0, 0);
    }

    public Vector multiply(double s) {
        return new Vector(Mat.multiply(m, s));
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.1f, %.1f)", m.get(0, 0), m.get(1, 0));
    }
}
