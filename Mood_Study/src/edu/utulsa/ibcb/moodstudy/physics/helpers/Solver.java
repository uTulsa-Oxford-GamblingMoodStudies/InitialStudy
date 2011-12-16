package edu.utulsa.ibcb.moodstudy.physics.helpers;

import Jama.Matrix;


// Various mathematical utilities. Implement the ODE solver here.
public class Solver {
	
	public interface Differentiable{
		
		
		
		
	}
	
	public static void RungeKutta(double x0[], double xEnd[], int len, double t0,
			double t1, Differentiable dxdt){
		
		
		
		
	}
	
	public static Matrix reflect(Matrix vector, Matrix normal){
		Matrix result = normal.copy();
		result.timesEquals(-2 * dot(normal,vector));
		result.plusEquals(vector);
		return result;
	}
	
	public static double dot(Matrix vec1, Matrix vec2) {
		double[][] arr1 = vec1.getArray();
		double[][] arr2 = vec2.getArray();
		
		return arr1[0][0]*arr2[0][0] + arr1[1][0]*arr2[1][0] + arr1[2][0]*arr2[2][0];
	}
	
	public static Matrix crossProduct3(Matrix vec1, Matrix vec2) {
		Matrix pvec = new Matrix(3, 1);
		double[][] arr1 = vec1.getArray();
		double[][] arr2 = vec2.getArray();
		
		pvec.set(
				0,
				0,
				arr1[1][0] * arr2[2][0] - arr1[2][0]
						* arr2[1][0]);
		pvec.set(
				1,
				0,
				-arr1[0][0] * arr2[2][0] + arr1[2][0]
						* arr2[2][0]);
		pvec.set(
				2,
				0,
				arr1[0][0] * arr2[1][0] - arr1[1][0]
						* arr2[0][0]);

		
		return pvec;
	}
	
	public static Matrix getRotationMatrixY(double theta) {
		Matrix m = Matrix.identity(3, 3);

		double cos_theta = Math.cos(theta);
		double sin_theta = Math.sin(theta);

		m.set(0, 0, cos_theta);
		m.set(0, 2, sin_theta);
		m.set(2, 0, -sin_theta);
		m.set(2, 2, cos_theta);
		return m;
	}
	
	public static Matrix RotateIntoXZ(Matrix vector) {
		Matrix m = Matrix.identity(3, 3);

		double u = vector.get(0, 0);
		double v = vector.get(1, 0);

		m.set(0, 0, u);
		m.set(0, 1, v);
		m.set(1, 0, -v);
		m.set(1, 1, u);

		m.timesEquals(1.0 / Math.sqrt(u * u + v * v));

		return m;
	}

	public static Matrix RotateXZ2Z(Matrix vector) {
		Matrix m = Matrix.identity(3, 3);

		double u = vector.get(0, 0);
		double v = vector.get(1, 0);
		double w = vector.get(2, 0);

		double uvnorm = Math.sqrt(u * u + v * v);

		m.set(0, 0, w);
		m.set(0, 2, -uvnorm);
		m.set(2, 0, uvnorm);
		m.set(2, 2, w);

		m.timesEquals(1.0 / Math.sqrt(u * u + v * v + w * w));

		return m;
	}

	public static Matrix getRotationMatrix(double theta, Matrix axis) {
		Matrix Rxz = RotateIntoXZ(axis);
		Matrix Rxz2z = RotateXZ2Z(axis);
		Matrix Rtheta = getRotationMatrixZ(theta);
		return Rxz.inverse().times(
				Rxz2z.inverse().times(Rtheta.times(Rxz2z.times(Rxz))));
	}
	
	public static Matrix getRotationMatrixZ(double theta) {
		Matrix m = Matrix.identity(3, 3);

		double cos_theta = Math.cos(theta);
		double sin_theta = Math.sin(theta);

		m.set(0, 0, cos_theta);
		m.set(0, 1, -sin_theta);
		m.set(1, 0, sin_theta);
		m.set(1, 1, cos_theta);
		return m;
	}
}
