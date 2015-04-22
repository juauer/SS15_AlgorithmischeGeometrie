package mats;

public class Mat3x3 extends Mat {
	public Mat3x3(Mat mat) {
		data = mat.data;
	}

	public Mat3x3(double[][] rows) {
		data = rows;
	}

	public Mat3x3(double mat00, double mat01, double mat02,
			double mat10, double mat11, double mat12,
			double mat20, double mat21, double mat22) {
		data = new double[][] { { mat00, mat01, mat02 },
				{ mat10, mat11, mat12 },
				{ mat20, mat21, mat22 } };
	}

	public static Mat3x3 inverse(Mat3x3 mat) {
		return mat.clone().inverse();
	}

	@Override
	public Mat3x3 clone() {
		double[][] res = new double[data.length][data[0].length];

		for(int i = 0; i < data.length; ++i)
			for(int j = 0; j < data[0].length; ++j)
				res[i][j] = data[i][j];

		return new Mat3x3(res);
	}

	public double getDeterminant() {
		return data[0][0] * data[1][1] * data[2][2] + data[0][1] * data[1][2] * data[2][0] + data[0][2] * data[1][0] * data[2][1] -
				data[0][2] * data[1][1] * data[2][0] - data[0][1] * data[1][0] * data[2][2] - data[0][0] * data[1][2] * data[2][1];
	}

	public Mat3x3 inverse() {
		double det = getDeterminant();
		double mat00 = (data[1][1] * data[2][2] - data[1][2] * data[2][1]) / det;
		double mat01 = (data[0][2] * data[2][1] - data[0][1] * data[2][2]) / det;
		double mat02 = (data[0][1] * data[1][2] - data[0][2] * data[1][1]) / det;
		double mat10 = (data[1][2] * data[2][0] - data[1][0] * data[2][2]) / det;
		double mat11 = (data[0][0] * data[2][2] - data[0][2] * data[2][0]) / det;
		double mat12 = (data[0][2] * data[1][0] - data[0][0] * data[1][2]) / det;
		double mat20 = (data[1][0] * data[2][1] - data[1][1] * data[2][0]) / det;
		double mat21 = (data[0][1] * data[2][0] - data[0][0] * data[2][1]) / det;
		double mat22 = (data[0][0] * data[1][1] - data[0][1] * data[1][0]) / det;
		data[0][0] = mat00;
		data[0][1] = mat01;
		data[0][2] = mat02;
		data[1][0] = mat10;
		data[1][1] = mat11;
		data[1][2] = mat12;
		data[2][0] = mat20;
		data[2][1] = mat21;
		data[2][2] = mat22;
		return this;
	}
}
