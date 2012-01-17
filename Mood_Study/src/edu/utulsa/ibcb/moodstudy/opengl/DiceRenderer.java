package edu.utulsa.ibcb.moodstudy.opengl;

import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.DefaultMotionState;

import edu.utulsa.ibcb.moodstudy.opengl.SceneNode.RenderState;

import edu.utulsa.ibcb.moodstudy.R;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class DiceRenderer implements Renderer {
	
	float AR;
	float fov = 45.0f;
	static Context pContext;
	
	// timing variables
	long ltime=0;
	
	// environments
	HashMap<String, Environment> environments = new HashMap<String, Environment>();
	Environment current = null;
	boolean switchToNewEnvironment=false;
	
	Queue<Environment> uninitialized = new LinkedList<Environment>();
	
	private DiceRenderer(){
		
	}
	
	public static void setContext(Context pContext){
		DiceRenderer.pContext = pContext;
	}
	
	public static Context getContext(){
		return DiceRenderer.pContext;
	}
	
	static DiceRenderer singleton=null;
	
	public static DiceRenderer getInstance(){
		if(singleton==null){
			System.out.println("Creating new dicerenderer...");
			singleton = new DiceRenderer();
			singleton.createEnvironments();
		}
		return singleton;
	}
	
	public void createEnvironments(){
		DiceRollEnvironment dre = new DiceRollEnvironment();
        CupEnvironment cup = new CupEnvironment();
        
        dre.animationMode();
        cup.setSimulated(true);
        
        this.addEnvironment("cup", cup);
        this.addEnvironment("roll", dre);
	}

	// GL and display related variables
	private GLU glu;
	
	public GLU getGLU(){
		return glu;
	}
	
	float [] matrix = new float[16];
	
	private void copyState(Matrix3f m, Vector3f v){
		matrix[0] = m.m00;
		matrix[1] = m.m10;
		matrix[2] = m.m20;
		matrix[3] = 0;
		
		matrix[4] = m.m01;
		matrix[5] = m.m11;
		matrix[6] = m.m21;
		matrix[7] = 0;
		
		matrix[8] = m.m02;
		matrix[9] = m.m12;
		matrix[10] = m.m22;
		matrix[11] = 0;
		
		matrix[12] = v.x;
		matrix[13] = v.y;
		matrix[14] = v.z;
		matrix[15] = 1;
	}
	
	public void recursiveDraw(GL10 gl, SceneNode node){
		if(node.isSimulated()){
			DefaultMotionState myMotionState = (DefaultMotionState) node.getRigidBody().getMotionState();
			myMotionState.graphicsWorldTrans.getOpenGLMatrix(matrix);
		}else{
			copyState(node.getOrientation(), node.getTranslation());
		}
		
		gl.glMultMatrixf(matrix, 0);
		RenderState nodeState = node.getRenderState();
		
		if(nodeState!=null){
			nodeState.start(gl);
		}
		
		for(Mesh object : node.getObjects()){
			if(object.getTexture() > -1)
				gl.glBindTexture(GL10.GL_TEXTURE_2D, object.getTexture());
			
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, object.getGLTexCoords());
			gl.glNormalPointer(GL10.GL_FLOAT, 0, object.getGLNormals());
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, object.getGLVertices());
			
			gl.glDrawElements(GL10.GL_TRIANGLES, object.getFaceCount()*3, GL10.GL_UNSIGNED_SHORT, object.getGLIndices());
		}
		
		if(nodeState!=null){
			nodeState.stop(gl);
		}
		
		for(SceneNode child : node.getChildren()){
			gl.glPushMatrix();
			
			recursiveDraw(gl, child);
			
			gl.glPopMatrix();
		}
	}
	
	public void init(GL10 gl)
	{
		gl.glClearColor(0f, 0f, 0f, 0f); // set to non-transparent black
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		//gl.glPolygonMode(GL10.GL_FRONT_AND_BACK, GL10.);
		gl.glDisable(GL10.GL_CULL_FACE);
		
		//gl.glColorMaterial ( GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE ) ;
	    gl.glEnable ( GL10.GL_COLOR_MATERIAL ) ;
	    
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		
		glu = new GLU();
	}
	
	public Environment getEnvironment(){
		return current;
	}
	
	public void setEnvironment(String name){
		Environment e = environments.get(name);
		current = e;
		switchToNewEnvironment=true;
	}
	
	public Environment getEnvironment(String name){
		return environments.get(name);
	}
	
	public void reset(){
		current = null;
	}
	
	public void addEnvironment(String name, Environment e){
		environments.put(name, e);
		uninitialized.add(e);
	}
	
	
	
	private void cameraLookAt(GL10 gl, Camera cam){
		Vector3f pos = cam.getPosition();
		Vector3f target = cam.getTarget();
		
		GLU.gluLookAt(gl, pos.x,	 pos.y, 		pos.z, 
				target.x, target.y, 	target.z,
				0, 1, 0);
	}
	
	/*
	public void suspend()
	{
		animator.stop();
	}
	
	public void run()
	{
		animator.start();
	}
	*/

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		gl.glViewport(0, 0, width, height);
		
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		
		AR = ((float)width) / ((float)height);
		
		GLU.gluPerspective(gl, fov, AR, 1.0f, 100.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		// TODO Auto-generated method stub
		
		init(gl);
		
		
		for(Environment e : environments.values()){
			if(e.initialized())
				e.reloadGLContent(gl);
		}
		
	}
	

	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
	    //Reset Drawing Surface
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		while(uninitialized.size() > 0){
			Environment e = uninitialized.remove();
			
			if(e.isSimulated)
				e.initJBullet();
			e.setupEnvironment(gl);
			e.setInitialized();
			e.resetScene();
		}

		if(switchToNewEnvironment){
			current.resetScene();
			current.initPhysicalProperties();
			current.setupGLProperties(gl);
			switchToNewEnvironment=false;
		}
		
		if(current != null){
			cameraLookAt(gl, current.maincam);
			
			recursiveDraw(gl, current.root);
			
			long time = System.currentTimeMillis();
			float elapsed = (time-ltime) / 1000.0f;
			
			boolean firstFrame = ltime!=0;
			ltime = time;
			
			if(firstFrame){
				current.updateTime(elapsed);
			}
		}
	}
}
