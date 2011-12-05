package edu.utulsa.ibcb.moodstudy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
 
import javax.microedition.khronos.opengles.GL10;
 
public enum Letters {
	D (new float[] {
			0.0f, 2.0f,
			0.0f, 0.0f,
 
			0.0f, 0.0f,
			1.0f, 0.0f,
 
			1.0f, 0.0f,
			1.25f, 0.25f,
 
			1.25f, 0.25f,
			1.25f, 1.75f,
 
			1.25f, 1.75f,
			1.0f, 2.0f,
 
			1.0f, 2.0f,
			0.0f, 2.0f
	}),
	E (new float[] {
			0.0f, 2.0f,
			0.0f, 0.0f,
 
			0.0f, 2.0f,
			1.0f, 2.0f,
 
			0.0f, 1.0f,
			1.0f, 1.0f,
 
			0.0f, 0.0f,
			1.0f, 0.0f
	}),
	H (new float[] {
			0.0f, 2.0f,
			0.0f, 0.0f,
 
			0.0f, 1.0f,
			1.5f, 1.0f,
 
			1.5f, 2.0f,
			1.5f, 0.0f
	}),
	L (new float[] {
			0.0f, 2.0f,
			0.0f, 0.0f,
 
			0.0f, 0.0f,
			1.0f, 0.0f
			}),
 
	O (new float[] {
			0.0f, 1.75f,
			0.0f, 0.25f,
 
			0.0f, 0.25f,
			0.25f, 0.0f,
 
			0.25f, 0.0f,
			1.25f, 0.0f,
 
			1.25f, 0.0f,
			1.5f, 0.25f,
 
			1.5f, 0.25f,
			1.5f, 1.75f,
 
			1.5f, 1.75f,
			1.25f, 2.0f,
 
			1.25f, 2.0f,
			0.25f, 2.0f,
 
			0.25f, 2.0f,
			0.0f, 1.75f
 
	}),
	R (new float[] {
			0.0f, 2.0f,
			0.0f, 0.0f,
 
			0.0f, 2.0f,
			1.0f, 2.0f,
 
			1.0f, 2.0f,
			1.5f, 1.5f,
 
			1.5f, 1.5f,
			1.5f, 1.25f,
 
			1.5f, 1.25f,
			1.0f, 1.0f,
 
			1.0f, 1.0f,
			0.0f, 1.0f,
 
			1.0f, 1.0f,
			1.5f, 0.0f
 
	}),
	W (new float[] {
			0.0f, 2.0f,
			0.75f, 0.0f,
 
			0.75f, 0.0f,
			1.0f, 1.0f,
 
			1.0f, 1.0f,
			1.25f, 0.0f,
 
			1.25f, 0.0f,
			2.0f, 2.0f
	});
 
	//Constructor - Load Vertices into a Buffer for OpenGL
	private Letters(float[] vertices)
	{
		this.vertices = vertices;
 
		ByteBuffer buffer = ByteBuffer.allocateDirect(vertices.length * 4);
		buffer.order(ByteOrder.nativeOrder());
		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(this.vertices);
		vertexBuffer.position(0);
	}
 
	public void draw(GL10 gl)
	{
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
 
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
 
		gl.glDrawArrays(GL10.GL_LINES, 0, vertices.length / 2);
 
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
 
	private float[] vertices;
	private FloatBuffer vertexBuffer;
}