package mats;

public class Mat2x2 extends Mat {
	public Mat2x2(Mat mat) {
		data = mat.data;
	}

	public Mat2x2(double[][] rows) {
		data = rows;
	}

	public Mat2x2(double mat00, double mat01, double mat10, double mat11) {
		data = new double[][] { { mat00, mat01 }, { mat10, mat11 } };
	}

	public static Mat2x2 inverse(Mat2x2 mat) {
		return mat.clone().inverse();
	}

	@Override
	public Mat2x2 clone() {
		double[][] res = new double[data.length][data[0].length];

		for(int i = 0; i < data.length; ++i)
			for(int j = 0; j < data[0].length; ++j)
				res[i][j] = data[i][j];

		return new Mat2x2(res);
	}

	public double getDeterminant() {
		return data[0][0] * data[1][1] - data[0][1] * data[1][0];
	}

	public Mat2x2 inverse() {
		double det = getDeterminant();
		double res00 = data[1][1] / det;
		double res01 = -data[0][1] / det;
		double res10 = -data[1][0] / det;
		double res11 = data[0][0] / det;
		data[0][0] = res00;
		data[0][1] = res01;
		data[1][0] = res10;
		data[1][1] = res11;
		return this;
	}
}
