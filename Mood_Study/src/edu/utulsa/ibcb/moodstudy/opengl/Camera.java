package edu.utulsa.ibcb.moodstudy.opengl;

import javax.vecmath.Vector3f;

import android.opengl.GLU;

public abstract class Camera {
	
	public abstract Vector3f getTarget();
	public abstract Vector3f getPosition();
	
}
