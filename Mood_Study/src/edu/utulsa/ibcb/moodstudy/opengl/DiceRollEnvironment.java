package edu.utulsa.ibcb.moodstudy.opengl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import edu.utulsa.ibcb.moodstudy.R;
import edu.utulsa.ibcb.moodstudy.opengl.FollowCamera;
import edu.utulsa.ibcb.moodstudy.opengl.Mesh;
import edu.utulsa.ibcb.moodstudy.opengl.SceneNode;
import edu.utulsa.ibcb.moodstudy.opengl.Solver;
import edu.utulsa.ibcb.moodstudy.opengl.SceneNode.RenderState;

public class DiceRollEnvironment extends Environment {

	FollowCamera cam;
	
	int surfacetex = 0;
	static int target = 1;
	static String[] anim_names = new String[]{"WeakThrow", "MedThrow","StrongThrow"};
	static Matrix3f diefaceOrientations[][] = new Matrix3f[anim_names.length][6];
	
	// opposite faces:
	// 1-6
	// 2-4
	// 3-5
	
	static{
		diefaceOrientations[1][0] = Solver.getRotationMatrix((float)(-Math.PI/2.0), new Vector3f(0,1,0));
		diefaceOrientations[1][1] = new Matrix3f(Solver.identity);
		diefaceOrientations[1][2] = Solver.getRotationMatrixZ((float)(Math.PI/2.0));
		
		diefaceOrientations[1][3] = Solver.getRotationMatrix((float)(Math.PI), new Vector3f(0,1,0));
		diefaceOrientations[1][4] = Solver.getRotationMatrixZ((float)(-Math.PI/2.0));
		diefaceOrientations[1][5] = Solver.getRotationMatrix((float)(Math.PI/2.0), new Vector3f(0,1,0));
		
		diefaceOrientations[0][0] = new Matrix3f(Solver.identity);
		diefaceOrientations[0][1] = Solver.getRotationMatrix((float)(Math.PI/2.0), new Vector3f(0,1,0));
		diefaceOrientations[0][2] = Solver.getRotationMatrix((float)(Math.PI/2.0), new Vector3f(1,0,0));
		
		diefaceOrientations[0][3] = Solver.getRotationMatrix((float)(-Math.PI/2.0), new Vector3f(0,1,0));
		diefaceOrientations[0][4] = Solver.getRotationMatrix((float)(-Math.PI/2.0), new Vector3f(1,0,0));
		diefaceOrientations[0][5] = Solver.getRotationMatrix((float)(Math.PI), new Vector3f(0,1,0));
		
		diefaceOrientations[2][0] = Solver.getRotationMatrix((float)(Math.PI), new Vector3f(0,1,0));
		diefaceOrientations[2][1] = Solver.getRotationMatrix((float)(-Math.PI/2.0), new Vector3f(0,1,0));
		diefaceOrientations[2][2] = Solver.getRotationMatrix((float)(-Math.PI/2.0), new Vector3f(1,0,0));
		
		diefaceOrientations[2][3] = Solver.getRotationMatrix((float)(Math.PI/2.0), new Vector3f(0,1,0));
		diefaceOrientations[2][4] = Solver.getRotationMatrix((float)(Math.PI/2.0), new Vector3f(1,0,0));
		diefaceOrientations[2][5] = new Matrix3f(Solver.identity);
	}
	
	
	float throwStrength = 5;
	boolean anim_mode = false;
	int anim_num = 0;
	
	// SceneGraph variables
	SceneNode diefaces;
	SceneNode die;
	SceneNode table;
	
	public synchronized void updateForces(float dt){
		
	}
	
	int animToPlay = -1;
	int diceTarget = -1;
	
	public synchronized void initPhysicalProperties(){
		if(!anim_mode)
			die.getRigidBody().applyImpulse(new Vector3f(-throwStrength,0,0), new Vector3f(0,0.8f,0));
		else{
			playAnimation(animToPlay);
			setTarget(diceTarget);
		}
	}
	
	public synchronized void setupPlay(int force_level, int actual_roll){
		animToPlay = force_level;
		diceTarget = actual_roll;
	}
	
	
	public synchronized void setTarget(int target){
		Matrix3f orient = new Matrix3f(Solver.identity);
		
		orient.mul(diefaceOrientations[anim_num][target-1]);
		
		diefaces.setOrientation(orient);
	}
	
	public void setThrow(float strength){
		throwStrength = strength;
	}
	
	public synchronized void playAnimation(int num){
		anim_num = num;
		super.setAnimation(anim_names[num]);
		super.play();
	}
	
	public void animationMode(){
		anim_mode = true;
	}
	
	public synchronized void setupGLProperties(GL10 gl){
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, FloatBuffer.wrap(new float[]{1.0f,1.0f,1.0f}));
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, FloatBuffer.wrap(new float[]{0.5f,0.5f,0.5f}));
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, FloatBuffer.wrap(new float[]{3.0f,3.0f,0.0f}));
	}
	
	public synchronized void reloadGLContent(GL10 gl){
		for(Mesh object : diefaces.getObjects()){
			int texid=0;
			Context pContext = DiceRenderer.getContext();
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
		}
		
		surfacetex = TextureUtils.loadTexture(gl, DiceRenderer.getContext(), R.drawable.grass);
		
		table.getObjects().get(0).setTexture(surfacetex);
	}
	
	public synchronized void setupEnvironment(GL10 gl){
		try{
			
			List<Mesh> mobjects = Mesh.loadASE(DiceRenderer.getContext().getResources().getAssets().open("dice.ASE"));
			
			diefaces = new SceneNode("die-faces");
			die = new SceneNode("die");
			
			for(Mesh object : mobjects){
				System.out.println("Object: " + object.getName() + " -- ["+ object.getVertexCount() +", "+ object.getFaceCount()+"]");
				if(object.getName().startsWith("f")){
					diefaces.addMesh(object);
					
					int texid=0;
					Context pContext = DiceRenderer.getContext();
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
			surfacetex = TextureUtils.loadTexture(gl, DiceRenderer.getContext(), R.drawable.grass);
			
			table = new SceneNode("table-top");
			Mesh tabletop = createGround();
			
			tabletop.setTexture(surfacetex);
			tabletop.createGLArray();
			
			table.addMesh(tabletop);
			
			table.setRenderState(new RenderState(){
				
				public void start(Object o){
					GL10 gl = (GL10)o;
					gl.glEnable(GL10.GL_TEXTURE_2D);
				}
				public void stop(Object o){
					GL10 gl = (GL10)o;
				}
			});
			
			root.addChild(table);
			root.addChild(die);
			
			if(isSimulated && !anim_mode)
				dynamicsWorld.setGravity(new Vector3f(0f, -20f, 0f));
			
			if(isSimulated && !anim_mode)
			{
				StaticPlaneShape sps = new StaticPlaneShape(new Vector3f(0,1,0), 1);
				collisionShapes.add(sps);
				
				Transform groundTransform = new Transform();
				groundTransform.setIdentity();
				
				float mass = 0f;
				Vector3f localInertia = new Vector3f(0, 0, 0);

				// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
				DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, sps, localInertia);
				RigidBody body = new RigidBody(rbInfo);

				// add the body to the dynamics world
				dynamicsWorld.addRigidBody(body);
				table.setRigidBody(body);
			}
			
			die.addChild(diefaces);
			
			die.setRenderState(new RenderState(){
				public void start(Object o) {
					GL10 gl = (GL10)o;
					gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
					gl.glDisable(GL10.GL_TEXTURE_2D);
				}
				public void stop(Object o) {
					GL10 gl = (GL10)o;
				}
			});
			
			if(isSimulated && !anim_mode)
			{
				List<Mesh> colliders = Mesh.loadASE(DiceRenderer.getContext().getResources().getAssets().open("dice_hull.ASE"));
				
				Mesh diecollider = colliders.get(0);
				diecollider.scale(0.01);
				
				float mass = 1f;
				
				ObjectArrayList<Vector3f> points = new ObjectArrayList<Vector3f>();
				
				for(float[] v : diecollider.getVertices()){
					Vector3f pt = new Vector3f(v);
					
					points.add(pt);
				}
				
				ConvexHullShape dieshape = new ConvexHullShape(points);
				
				Vector3f localInertia = new Vector3f(0, 0, 0);
				dieshape.calculateLocalInertia(mass, localInertia);
					
				// die.setPosition(new Vector3f(0,0.82f,0));
				// die.setOrientation(Solver.getRotationMatrixY((float)(rotation * Math.PI / 180.0)));
				
				collisionShapes.add(dieshape);
				
				Transform startTransform = new Transform();
				startTransform.setIdentity();
				
				double theta = 65;
				float s = (float)Math.sin(theta/2.0);
		    	float c = (float)Math.cos(theta/2.0);
				
				float x = s*(float)(Math.sqrt(2)/2.0);
				float y = 0;
				float z = s*(float)(Math.sqrt(2)/2.0);
				float w = c;
				
				Quat4f q = new Quat4f(w,x,y,z);
				startTransform.setRotation(q);
				
				startTransform.origin.set(20, 50, 0);
				
				// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
				DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, dieshape, localInertia);
				RigidBody body = new RigidBody(rbInfo);
				body.setActivationState(RigidBody.ISLAND_SLEEPING);
				
				dynamicsWorld.addRigidBody(body);
				body.setActivationState(RigidBody.ISLAND_SLEEPING);
				
				die.setRigidBody(body);
			}
			
			if(isSimulated && !anim_mode){
				table.getRigidBody().setRestitution(0.025f);
				die.getRigidBody().setRestitution(0.025f);
				table.getRigidBody().setFriction(2.5f);
			}
			
			
			
			cam = new FollowCamera();
			cam.setRelPosition(new Vector3f(5,10,5));
			cam.setTarget(die);
			maincam = cam;
			
			diefaces.setRenderState(new RenderState(){
				public void start(Object o) {
					GL10 gl = (GL10)o;
					gl.glEnable(GL10.GL_BLEND);
					gl.glEnable(GL10.GL_TEXTURE_2D);
					gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				}
				public void stop(Object o) {
					GL10 gl = (GL10)o;
					gl.glDisable(GL10.GL_BLEND);
				}
			});
			
			// add all created objects to the object list
			
			objects.add(diefaces);
			objects.add(die);
			objects.add(table);
			
			// load the animations or set anim builders
			
			
			
			if(isSimulated && !anim_mode){
				setThrow(20);
				this.buildAnimation("StrongThrow", 10.0f, new FileOutputStream("data\\StrongThrow.anim"));
			}else{
				loadAnimation(DiceRenderer.getContext().getResources().getAssets().open("StrongThrow.anim"));
				loadAnimation(DiceRenderer.getContext().getResources().getAssets().open("MedThrow.anim"));
				loadAnimation(DiceRenderer.getContext().getResources().getAssets().open("WeakThrow.anim"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Mesh createGround(){
		int units = 50;
		float facesize = 10.0f;
		
		float start = facesize*(units/2);
		
		Mesh groundMesh = new Mesh();
		int verts = (units+1)*(units+1);
		int faces = units*units*2;
		
		groundMesh.init(verts, verts, faces);
		groundMesh.setName("table-top-model");
		
		for(int i = 0; i < units+1; i++){
			for(int j = 0; j < units+1; j++){
				
				groundMesh.getVertices()[i*(units+1)+j][0] = facesize*i - start;
				groundMesh.getVertices()[i*(units+1)+j][1] = (float)0.0;
				groundMesh.getVertices()[i*(units+1)+j][2] = facesize*j - start;
				
				groundMesh.getTVertices()[i*(units+1)+j][0] = (float)i;
                groundMesh.getTVertices()[i*(units+1)+j][1] = (float)j;
				
                groundMesh.getNormals()[i*(units+1)+j][0] = 0.0f;
                groundMesh.getNormals()[i*(units+1)+j][1] = 1.0f;
                groundMesh.getNormals()[i*(units+1)+j][2] = 0.0f;
			}
		}
		
		for(int i = 0; i < units; i++){
			for(int j = 0; j < units; j++){
				int index = 6 * (i*units+j);
				groundMesh.getFaces()[index]   = (short)((i+1)*(units+1)+j+1);
				groundMesh.getFaces()[index+1] = (short)((i+1)*(units+1)+j);
				groundMesh.getFaces()[index+2] = (short)(i*(units+1)+j);
				
				groundMesh.getFaces()[index+3] = (short)(i*(units+1)+j);
				groundMesh.getFaces()[index+4] = (short)(i*(units+1)+j+1);
				groundMesh.getFaces()[index+5] = (short)((i+1)*(units+1)+j+1);
				
				groundMesh.getTFaces()[index]   = (short)((i+1)*(units+1)+j+1);
				groundMesh.getTFaces()[index+1] = (short)((i+1)*(units+1)+j);
				groundMesh.getTFaces()[index+2] = (short)(i*(units+1)+j);
				
				groundMesh.getTFaces()[index+3] = (short)(i*(units+1)+j);
				groundMesh.getTFaces()[index+4] = (short)(i*(units+1)+j+1);
				groundMesh.getTFaces()[index+5] = (short)((i+1)*(units+1)+j+1);
				
			}
		}
		groundMesh.computeFaceNormals();
		
		return groundMesh;
	}
}
