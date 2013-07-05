package edu.utulsa.ibcb.moodstudy.opengl;

import javax.vecmath.Vector3f;

import android.opengl.GLU;

public class TargetCamera extends Camera {
	Vector3f target;
	Vector3f position;

	public TargetCamera() {
		target = new Vector3f(0, 0, 0);
		position = new Vector3f(0, 0, 0);
	}

	public Vector3f getPosition() {
		Vector3f worldtarget = new Vector3f(target);
		worldtarget.add(position);

		return worldtarget;
	}

	public Vector3f getTarget() {
		return target;
	}

	public void setTarget(Vector3f m) {
		target = m;
	}

	public void setRelPosition(Vector3f m) {
		position = m;
	}
}
