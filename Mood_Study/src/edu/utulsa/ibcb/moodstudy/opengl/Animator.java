package edu.utulsa.ibcb.moodstudy.opengl;

import java.util.ArrayList;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class Animator {
	public static class KeyFrame {
		public float t;
		public Matrix3f orientation;
		public Vector3f translation;
	}

	SceneNode node;
	ArrayList<KeyFrame> keyframes;

	float ctime = 0;
	int cindex = 1;

	public Animator(SceneNode node) {
		this.node = node;
		keyframes = new ArrayList<KeyFrame>();
	}

	public SceneNode getNode() {
		return node;
	}

	public ArrayList<KeyFrame> getKeyFrames() {
		return keyframes;
	}

	public void addKeyFrame(float time, Matrix4f transform) {
		KeyFrame kf = new KeyFrame();
		kf.t = time;
		kf.orientation = new Matrix3f();
		kf.translation = new Vector3f();
		transform.get(kf.orientation, kf.translation);
		keyframes.add(kf);
	}

	public void addKeyFrame(float time, Matrix3f orientation,
			Vector3f translation) {
		KeyFrame kf = new KeyFrame();
		kf.t = time;
		kf.orientation = new Matrix3f(orientation);
		kf.translation = new Vector3f(translation);
		keyframes.add(kf);
	}

	public boolean atEnd() {
		return cindex == keyframes.size();
	}

	public void resetInterpolator() {
		ctime = 0;
		cindex = 1;

		interpolate(0);
	}

	public void interpolate(float time) {
		ctime = time;

		while (cindex < keyframes.size() && ctime > keyframes.get(cindex).t) {
			cindex++;
		}

		if (cindex == keyframes.size()) {
			node.setOrientation(keyframes.get(cindex - 1).orientation);
			node.setPosition(keyframes.get(cindex - 1).translation);
		} else {
			// interpolate matrices and vectors

			KeyFrame kf1 = keyframes.get(cindex - 1);
			KeyFrame kf2 = keyframes.get(cindex);

			float alpha = (ctime - kf1.t) / (kf2.t - kf1.t);

			Matrix3f mf1 = new Matrix3f();
			mf1.mul(1 - alpha, kf1.orientation);

			Matrix3f mf2 = new Matrix3f();
			mf2.mul(alpha, kf2.orientation);

			mf1.add(mf2);
			node.setOrientation(mf1);

			Vector3f vf1 = new Vector3f();
			vf1.scale(1 - alpha, kf1.translation);

			Vector3f vf2 = new Vector3f();
			vf2.scale(alpha, kf2.translation);

			vf1.add(vf2);
			node.setPosition(vf1);
		}
	}
}
