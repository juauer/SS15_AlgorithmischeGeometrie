package mats;

public class Mat {
    protected double[][] data;

    public Mat() {
        data = new double[0][];
    }

    public Mat(double[][] rows) {
        data = rows;
    }

    public Mat(double... values) {
        data = new double[values.length][1];

        for(int i = 0; i < values.length; ++i)
            data[i][0] = values[i];
    }

    public static Mat abs(Mat mat) {
        return mat.clone().abs();
    }

    public static Mat add(Mat mat1, Mat mat2) {
        return mat1.clone().add(mat2);
    }

    public static Mat compose(Mat mat1, Mat mat2) {
        return mat1.clone().compose(mat2);
    }

    public static Mat multiply(Mat mat, double scalar) {
        return mat.clone().multiply(scalar);
    }

    public static Mat subtract(Mat mat1, Mat mat2) {
        return mat1.clone().subtract(mat2);
    }

    public static Mat transpose(Mat mat) {
        return mat.clone().transpose();
    }

    @Override
    public Mat clone() {
        double[][] res = new double[data.length][data[0].length];

        for(int i = 0; i < data.length; ++i)
            for(int j = 0; j < data[0].length; ++j)
                res[i][j] = data[i][j];

        return new Mat(res);
    }

    @Override
    public String toString() {
        String res = "";

        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data[0].length; ++j)
                res = res + String.format("%.3f ", data[i][j]);

            res = res + "\n";
        }

        return res;
    }

    public Mat abs() {
        for(int i = 0; i < data.length; ++i)
            for(int j = 0; j < data[0].length; ++j)
                data[i][j] = Math.abs(data[i][j]);

        return this;
    }

    public Mat add(Mat mat) {
        for(int i = 0; i < data.length; ++i)
            for(int j = 0; j < data[0].length; ++j)
                data[i][j] = data[i][j] + mat.data[i][j];

        return this;
    }

    public Mat compose(Mat mat) {
        double[][] res = new double[data.length][mat.data[0].length];

        for(int i = 0; i < res.length; ++i)
            for(int j = 0; j < res[0].length; ++j)
                for(int k = 0; k < mat.data.length; ++k)
                    res[i][j] += data[i][k] * mat.data[k][j];

        data = res;
        return this;
    }

    public double get(int row, int column) {
        return data[row][column];
    }

    public double[][] getArray() {
        return data;
    }

    public Mat multiply(double scalar) {
        for(int i = 0; i < data.length; ++i)
            for(int j = 0; j < data[0].length; ++j)
                data[i][j] = scalar * data[i][j];

        return this;
    }

    public Mat subtract(Mat mat) {
        for(int i = 0; i < data.length; ++i)
            for(int j = 0; j < data[0].length; ++j)
                data[i][j] = data[i][j] - mat.data[i][j];

        return this;
    }

    public Mat transpose() {
        double[][] res = new double[data[0].length][data.length];

        for(int i = 0; i < data.length; ++i)
            for(int j = 0; j < data[0].length; ++j)
                res[j][i] = data[i][j];

        data = res;
        return this;
    }
}
