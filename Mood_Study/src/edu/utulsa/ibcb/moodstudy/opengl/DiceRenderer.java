package edu.utulsa.ibcb.moodstudy.opengl;

import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.utulsa.ibcb.moodstudy.opengl.SceneNode.RenderState;
import edu.utulsa.ibcb.moodstudy.physics.RigidBody;
import edu.utulsa.ibcb.moodstudy.physics.helpers.Solver;

import Jama.Matrix;
import edu.utulsa.ibcb.moodstudy.R;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class DiceRenderer implements Renderer {
	
	float AR;
	float fov = 45.0f;
	Context pContext;
	public DiceRenderer(Context context){
		this.pContext=context;
		
	}

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
	}
	

	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
	    //Reset Drawing Surface
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	    gl.glLoadIdentity();

	    cameraLookAt(gl);
		
		recursiveDraw(gl, root);
	}
	
	int surfacetex = 0;
	
	int rotation = 65;
	
	// SceneGraph variables
	SceneNode root = new SceneNode("root");
	SceneNode diefaces;
	SceneNode die;
	SceneNode table;
	
	Camera cam;
	
	static int target = 5;
	static Matrix diefaceOrientations[] = new Matrix[6];
	
	// opposite faces:
	// 1-6
	// 2-4
	// 3-5
	
	static{
		diefaceOrientations[0] = Solver.getRotationMatrix(-Math.PI/2.0, new Matrix(new double[][]{{1.0,0.0,0.0}}).transpose());
		diefaceOrientations[1] = Solver.getRotationMatrixZ(-Math.PI/2.0);
		diefaceOrientations[2] = Matrix.identity(3, 3);
		
		diefaceOrientations[3] = Solver.getRotationMatrixZ(Math.PI/2.0);
		diefaceOrientations[4] = Solver.getRotationMatrix(Math.PI, new Matrix(new double[][]{{1.0,0.0,0.0}}).transpose());
		diefaceOrientations[5] = Solver.getRotationMatrix(Math.PI/2.0, new Matrix(new double[][]{{1.0,0.0,0.0}}).transpose());
	}
	
	
	public void cameraLookAt(GL10 gl){
		double[][] pos = cam.getWorldCoordinates().getArray();
		double[][] target = cam.getTarget().getArray();
		
		GLU.gluLookAt(gl,
				(float)pos[0][0],	 (float)pos[1][0], 		(float)pos[2][0], 
				(float)target[0][0], (float)target[1][0], 	(float)target[2][0],
				0, 1, 0);
	}

	public Mesh createGround(){
		
		Mesh groundMesh = new Mesh();
		groundMesh.init(121, 121, 200);
		groundMesh.setName("table-top-model");
		
		for(int i = 0; i < 11; i++){
			for(int j = 0; j < 11; j++){
				groundMesh.getVertices()[i*11+j][0] = (float)(4.0*(i - 5.0));
				groundMesh.getVertices()[i*11+j][1] = (float)0.0;
				groundMesh.getVertices()[i*11+j][2] = (float)(4.0*(j - 5.0));
				
				groundMesh.getTVertices()[i*11+j][0] = (float)i;
                groundMesh.getTVertices()[i*11+j][1] = (float)j;
				
                groundMesh.getNormals()[i*11+j][0] = 0.0f;
                groundMesh.getNormals()[i*11+j][1] = 1.0f;
                groundMesh.getNormals()[i*11+j][2] = 0.0f;
			}
		}
		
		for(int i = 0; i < 10; i++){
			for(int j = 0; j < 10; j++){
				int index = 6 * (i*10+j);
				groundMesh.getFaces()[index]   = (short)((i+1)*11+j+1);
				groundMesh.getFaces()[index+1] = (short)((i+1)*11+j);
				groundMesh.getFaces()[index+2] = (short)(i*11+j);
				
				groundMesh.getFaces()[index+3] = (short)(i*11+j);
				groundMesh.getFaces()[index+4] = (short)(i*11+j+1);
				groundMesh.getFaces()[index+5] = (short)((i+1)*11+j+1);
				
				groundMesh.getTFaces()[index]   = (short)((i+1)*11+j+1);
				groundMesh.getTFaces()[index+1] = (short)((i+1)*11+j);
				groundMesh.getTFaces()[index+2] = (short)(i*11+j);
				
				groundMesh.getTFaces()[index+3] = (short)(i*11+j);
				groundMesh.getTFaces()[index+4] = (short)(i*11+j+1);
				groundMesh.getTFaces()[index+5] = (short)((i+1)*11+j+1);
				
			}
		}
		
		return groundMesh;
	}
	
	public void showNumber(int t){
		target = t;
	}
	
	public void init(GL10 gl)
	{
		gl.glClearColor(0.3f, 0.3f, 0.3f, 0f); // set to non-transparent black
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		//gl.glPolygonMode(GL10.GL_FRONT, GL10.GL_);
		gl.glEnable(GL10.GL_CULL_FACE);
		
		//gl.glColorMaterial ( GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE ) ;
	    gl.glEnable ( GL10.GL_COLOR_MATERIAL ) ;
	    
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, FloatBuffer.wrap(new float[]{1.0f,1.0f,1.0f}));
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, FloatBuffer.wrap(new float[]{0.5f,0.5f,0.5f}));
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, FloatBuffer.wrap(new float[]{3.0f,3.0f,0.0f}));
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		
		
		try{
			
			List<Mesh> objects = Mesh.loadASE(pContext.getResources().getAssets().open("dice.ASE"));
			
			diefaces = new SceneNode("die-faces");
			die = new RigidBody("die");
			
			for(Mesh object : objects){
				System.out.println("Object: " + object.getName() + " -- ["+ object.getVertexCount() +", "+ object.getFaceCount()+"]");
				if(object.getName().startsWith("f")){
					diefaces.addMesh(object);
					int texid=-1;
					
					if(object.getName().equals("f1"))
						texid = TextureUtils.loadTexture(gl, pContext, R.drawable.f1);
					if(object.getName().equals("f2"))
						texid = TextureUtils.loadTexture(gl, pContext, R.drawable.f2);
					if(object.getName().equals("f3"))
						texid = TextureUtils.loadTexture(gl, pContext, R.drawable.f3);
					if(object.getName().equals("f4"))
						texid = TextureUtils.loadTexture(gl, pContext, R.drawable.f4);
					if(object.getName().equals("f5"))
						texid = TextureUtils.loadTexture(gl, pContext, R.drawable.f5);
					if(object.getName().equals("f6"))
						texid = TextureUtils.loadTexture(gl, pContext, R.drawable.f6);
					
					object.setTexture(texid);
				}else{
					die.addMesh(object);
				}
				object.scale(0.01);
				object.createGLArray();
			}
			
			surfacetex = TextureUtils.loadTexture(gl, pContext, R.drawable.grass);
			
			table = new SceneNode("table-top");
			Mesh tabletop = createGround();
			tabletop.setTexture(surfacetex);
			tabletop.createGLArray();
			table.addMesh(tabletop);
			
			table.setRenderState(new RenderState(){
				public void start(Object o){
					GL10 gl = (GL10)o;
					gl.glEnable(GL10.GL_TEXTURE_2D);
					//gl.glColor4f(0.1f, 0.5f, 0.2f, 1.0f);
				}
				public void stop(Object o){
					
				}
			});
			
			root.addChild(table);
			root.addChild(die);
			
			die.addChild(diefaces);
			die.setPosition(new Matrix(new double[][]{{0.0},{0.82},{0.0}}));
			die.setOrientation(Solver.getRotationMatrixY(rotation * Math.PI / 180.0));
			
			die.setRenderState(new RenderState(){
				public void start(Object o) {
					GL10 gl = (GL10)o;
					gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
					gl.glDisable(GL10.GL_TEXTURE_2D);
				}
				public void stop(Object o) {
					
				}
			});
			
			
			diefaces.setRenderState(new RenderState(){
				public void start(Object o) {
					GL10 gl = (GL10)o;
					gl.glEnable(GL10.GL_BLEND);
					gl.glEnable(GL10.GL_TEXTURE_2D);
					gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
					//gl.glColor4f(0.6f, 0.0f, 0.6f, 0.2f);
				}
				public void stop(Object o) {
					GL10 gl = (GL10)o;
					gl.glDisable(GL10.GL_BLEND);
				}
			});
			diefaces.setOrientation(diefaceOrientations[target-1]);
			
			cam = new Camera();
			
			cam.setTarget(new Matrix(new double[][]{{0.0},{0.0},{0.0}}));
			cam.setPosition(new Matrix(new double[][]{{0.0},{10.0*Math.sqrt(3)/2.0},{10.0/2.0}}));
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	float [] matrix = new float[16];
	
	private void copyState(Matrix m, Matrix v){
		double [][]arr = m.getArray();
		double [][]vec = v.getArray();
		
		matrix[0] = (float)arr[0][0];
		matrix[1] = (float)arr[1][0];
		matrix[2] = (float)arr[2][0];
		matrix[3] = (float)0.0;
		
		matrix[4] = (float)arr[0][1];
		matrix[5] = (float)arr[1][1];
		matrix[6] = (float)arr[2][1];
		matrix[7] = (float)0.0;
		
		matrix[8] = (float)arr[0][2];
		matrix[9] = (float)arr[1][2];
		matrix[10] = (float)arr[2][2];
		matrix[11] = (float)0.0;
		
		matrix[12] = (float)vec[0][0];
		matrix[13] = (float)vec[1][0];
		matrix[14] = (float)vec[2][0];
		matrix[15] = (float)1.0;
	}
	
	public void recursiveDraw(GL10 gl, SceneNode node){
		
		copyState(node.getOrientation(), node.getTranslation());
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
	

}
