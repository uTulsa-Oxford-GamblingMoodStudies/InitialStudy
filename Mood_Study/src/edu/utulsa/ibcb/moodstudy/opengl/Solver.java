package edu.utulsa.ibcb.moodstudy.opengl;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

// Various mathematical utilities. Implement the ODE solver here.
public class Solver {
	public static final Vector3f zerovector = new Vector3f(0, 0, 0);
	public static final Matrix3f zeromatrix = new Matrix3f(0, 0, 0, 0, 0, 0, 0,
			0, 0);
	public static final Matrix3f identity = new Matrix3f(1, 0, 0, 0, 1, 0, 0,
			0, 1);

	public static Matrix3f getRotationMatrixY(float theta) {
		Matrix3f m = new Matrix3f(identity);

		float cos_theta = (float) Math.cos(theta);
		float sin_theta = (float) Math.sin(theta);

		m.m00 = cos_theta;
		m.m02 = sin_theta;
		m.m20 = -sin_theta;
		m.m22 = cos_theta;

		return m;
	}

	public static Matrix3f RotateIntoXZ(Vector3f vec) {
		Matrix3f m = new Matrix3f(identity);

		float u = vec.x;
		float v = vec.y;

		m.m00 = u;
		m.m01 = v;
		m.m10 = -v;
		m.m11 = u;

		m.mul((float) (1.0 / Math.sqrt(u * u + v * v)));

		return m;
	}

	public static Matrix3f RotateXZ2Z(Vector3f vec) {
		Matrix3f m = new Matrix3f(identity);
		float u = vec.x;
		float v = vec.y;
		float w = vec.z;

		float uvnorm = (float) Math.sqrt(u * u + v * v);

		m.m00 = w;
		m.m02 = -uvnorm;
		m.m20 = uvnorm;
		m.m22 = w;

		m.mul((float) (1.0 / Math.sqrt(u * u + v * v + w * w)));

		return m;
	}

	public static Matrix3f getRotationMatrix(float theta, Vector3f axis) {
		Matrix3f Rxz = RotateIntoXZ(axis);
		Matrix3f Rxz2z = RotateXZ2Z(axis);
		Matrix3f Rtheta = getRotationMatrixZ(theta);

		Matrix3f Rxzinv = new Matrix3f(Rxz);
		Rxzinv.invert();

		Matrix3f Rxz2zinv = new Matrix3f(Rxz2z);
		Rxz2zinv.invert();

		Matrix3f result = new Matrix3f(identity);

		result.mul(Rxzinv);
		result.mul(Rxz2zinv);
		result.mul(Rtheta);
		result.mul(Rxz2z);
		result.mul(Rxz);

		return result;
	}

	public static Matrix3f getRotationMatrixZ(float theta) {
		Matrix3f m = new Matrix3f(identity);

		float cos_theta = (float) Math.cos(theta);
		float sin_theta = (float) Math.sin(theta);

		m.m00 = cos_theta;
		m.m01 = -sin_theta;
		m.m10 = sin_theta;
		m.m11 = cos_theta;
		return m;
	}
}
