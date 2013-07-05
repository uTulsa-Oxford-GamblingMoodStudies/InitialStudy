package edu.utulsa.ibcb.moodstudy.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class HelloRenderer implements Renderer {

	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		// Reset Drawing Surface
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(-4.0f, 5.0f, -20.0f);
		Letters.H.draw(gl);

		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		Letters.E.draw(gl);

		gl.glTranslatef(1.5f, 0.0f, 0.0f);
		Letters.L.draw(gl);

		gl.glTranslatef(1.5f, 0.0f, 0.0f);
		Letters.L.draw(gl);

		gl.glTranslatef(1.5f, 0.0f, 0.0f);
		Letters.O.draw(gl);

		gl.glTranslatef(-7.0f, -5.0f, 0.0f);
		Letters.W.draw(gl);

		gl.glTranslatef(2.5f, 0.0f, 0.0f);
		Letters.O.draw(gl);

		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		Letters.R.draw(gl);

		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		Letters.L.draw(gl);

		gl.glTranslatef(1.5f, 0.0f, 0.0f);
		Letters.D.draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 60.0f, (float) width / (float) height, 0.1f,
				100.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		// TODO Auto-generated method stub

	}

}
