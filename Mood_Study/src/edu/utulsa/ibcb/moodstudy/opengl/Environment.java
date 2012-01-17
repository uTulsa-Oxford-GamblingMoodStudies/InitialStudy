package edu.utulsa.ibcb.moodstudy.opengl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.util.ObjectArrayList;

import edu.utulsa.ibcb.moodstudy.opengl.Animator;
import edu.utulsa.ibcb.moodstudy.opengl.Camera;
import edu.utulsa.ibcb.moodstudy.opengl.Mesh;
import edu.utulsa.ibcb.moodstudy.opengl.SceneNode;
import edu.utulsa.ibcb.moodstudy.opengl.Animator.KeyFrame;

public abstract class Environment {
	private class Animation{
		public ArrayList<Animator> animators;
		public String name;
		
		public Animation(String name){
			this.name = name;
			animators = new ArrayList<Animator>();
		}
	}
	
	// 	JBullet simulator variables
	
	protected DefaultCollisionConfiguration collisionConfiguration;
	protected CollisionDispatcher dispatcher;
	
	protected ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	protected BroadphaseInterface broadphase;
	protected ConstraintSolver solver;
	protected DynamicsWorld dynamicsWorld;
	
	// Camera node and scenegraph
	
	protected Camera maincam;
	protected SceneNode root;
	protected ArrayList<SceneNode> objects = new ArrayList<SceneNode>();
	protected HashMap<String, Animation> animations = new HashMap<String, Animation>();
	
	
	// animation variables
	
	Animation current = null;
	boolean isAnimated = false;
	boolean play = false;
	float animation_time = 0;
	
	boolean buildAnimation = false;
	Animation saveDestination = null;
	float build_time = 0;
	float build_length = 0;
	FileOutputStream buildFile;
	
	boolean isSimulated = false;
	boolean simPaused = false;
	
	float mintimestep = 1.0f/60.0f;
	
	boolean initialized = false;
	
	public boolean initialized(){
		return initialized;
	}
	
	public void setInitialized(){
		initialized=true;
	}
	
	public abstract void initPhysicalProperties();
	
	public abstract void setupEnvironment(GL10 gl);
	
	public abstract void reloadGLContent(GL10 gl);
	
	public abstract void setupGLProperties(GL10 gl);
	
	public abstract void updateForces(float dt);
	
	public void pauseSimulation(){
		simPaused = true;
	}
	
	public void resumeSimulation(){
		simPaused = false;
	}
	
	public Environment(){
		root = new SceneNode("root");
	}
	
	public void setSimulated(boolean simulated){
		isSimulated = simulated;
	}
	
	public void setAnimation(String name){
		isAnimated=true;
		animation_time = 0;
		current = animations.get(name);
		
		rewind();
	}
	
	public boolean isEnded(){
		boolean ended=true;
		for(Animator a : current.animators){
			ended &= a.atEnd();
		}
		return ended;
	}
	
	public void play(){
		play = true;
	}
	public void stop(){
		play = false;
	}
	public void rewind(){
		if(isAnimated){
			for(Animator a : current.animators){
				a.resetInterpolator();
			}
		}
	}
	
	public void buildAnimation(String name, float length, FileOutputStream bf){
		buildAnimation = true;
		saveDestination = new Animation(name);
		animations.put(name, saveDestination);
		build_time = 0;
		build_length = length;
		
		buildFile=bf;
		
		for(SceneNode n : objects){
			if(n.isSimulated()){
				Animator a = new Animator(n);
				saveDestination.animators.add(a);
			}
		}
	}
	
	public void saveAnimation(String name, FileOutputStream fos) throws IOException{
		
		System.out.println("Saving animation: " + name);
		Animation anim = animations.get(name);
		
		BufferedWriter dataOut = new BufferedWriter(new OutputStreamWriter(fos));
		
		dataOut.write("Name " + name + "\n");
		
		for(Animator a : anim.animators){
			dataOut.write("Animate " + a.getNode().getName() + "\n");
			
			for(KeyFrame kf : a.getKeyFrames()){
				dataOut.write("Frame " + kf.t + "\n" + kf.orientation.toString() + kf.translation.toString() + "\n");
			}
		}
		
		dataOut.close();
	}
	
	public void loadAnimation(InputStream fis) throws IOException{
		BufferedReader dataIn = new BufferedReader(new InputStreamReader(fis));
		
		Animation newAnim = null;
		Animator curAnimator = null;
		
		String matrixlines = "";
		int matrix_read = -1;
		float ktime=0;
		
		String line = "";
		while( (line = dataIn.readLine()) != null){
			if(line.endsWith("\n")){
				line=line.substring(0, line.length()-1);
			}
			
			if(line.startsWith("Name")){
				String [] items = line.split("\\s+");
				newAnim = new Animation(items[1]);
			}
			else if(line.startsWith("Animate")){
				String [] items = line.split("\\s+");
				
				SceneNode node = null;
				for(SceneNode n : objects){
					if(n.getName().equals(items[1])){
						node=n;
						break;
					}
				}
				
				if(node == null){
					System.err.println("WARNING: Node '"+ items[1] + "' does not exist in the scene");
					curAnimator = null;
				}
				else{
					curAnimator = new Animator(node);
					newAnim.animators.add(curAnimator);
				}
			}
			else if(line.startsWith("Frame")){
				String [] items = line.split("\\s+");
				
				ktime = Float.parseFloat(items[1]);
				matrix_read = 0;
				matrixlines="";
			}
			else{
				if(matrix_read < 3){
					matrixlines += line + (matrix_read < 2 ? "," : "");
					matrix_read++;
				}else{
					matrixlines = matrixlines.replaceAll("\\s+", "");
					String vectorline = line.substring(1, line.length()-1).replaceAll("\\s+", "");
					
					String[] mcomponents = matrixlines.split(",");
					String[] vcomponents = vectorline.split(",");
					
					Matrix3f orientation = new Matrix3f();
					Vector3f translation = new Vector3f();
					
					orientation.m00 = Float.parseFloat(mcomponents[0]);
					orientation.m01 = Float.parseFloat(mcomponents[1]);
					orientation.m02 = Float.parseFloat(mcomponents[2]);
					
					orientation.m10 = Float.parseFloat(mcomponents[3]);
					orientation.m11 = Float.parseFloat(mcomponents[4]);
					orientation.m12 = Float.parseFloat(mcomponents[5]);
					
					orientation.m20 = Float.parseFloat(mcomponents[6]);
					orientation.m21 = Float.parseFloat(mcomponents[7]);
					orientation.m22 = Float.parseFloat(mcomponents[8]);
					
					translation.x = Float.parseFloat(vcomponents[0]);
					translation.y = Float.parseFloat(vcomponents[1]);
					translation.z = Float.parseFloat(vcomponents[2]);
					
					curAnimator.addKeyFrame(ktime, orientation, translation);
				}
			}
		}
		System.out.println("Loaded Animation: " + newAnim.name + " " + newAnim.animators.size());
		animations.put(newAnim.name, newAnim);
	}
	
	protected Mesh createPlane(Vector3f P0, Vector3f P1, Vector3f P2){
		Mesh groundMesh = new Mesh();
		groundMesh.init(4, 4, 2);
		groundMesh.setName("table-plane-collider");
		
		Vector3f e1 = new Vector3f();
		e1.sub(P1, P0);
		Vector3f e2 = new Vector3f();
		e2.sub(P2,P0);
		
		Vector3f n = new Vector3f();
		n.cross(e2, e1);
		n.normalize();
		
		Vector3f P3 = new Vector3f();
		P3.add(e2,e1);
		P3.add(P0);
		
		groundMesh.getVertices()[0][0] = (float)P0.x;
		groundMesh.getVertices()[0][1] = (float)P0.y;
		groundMesh.getVertices()[0][2] = (float)P0.z;
		
		groundMesh.getVertices()[1][0] = (float)P1.x;
		groundMesh.getVertices()[1][1] = (float)P1.y;
		groundMesh.getVertices()[1][2] = (float)P1.z;
		
		groundMesh.getVertices()[2][0] = (float)P2.x;
		groundMesh.getVertices()[2][1] = (float)P2.y;
		groundMesh.getVertices()[2][2] = (float)P2.z;
		
		groundMesh.getVertices()[3][0] = (float)P3.x;
		groundMesh.getVertices()[3][1] = (float)P3.y;
		groundMesh.getVertices()[3][2] = (float)P3.z;
		
		for(int i = 0; i < 4; i++){
			groundMesh.getNormals()[i][0] = (float)n.x;
			groundMesh.getNormals()[i][1] = (float)n.y;
			groundMesh.getNormals()[i][2] = (float)n.z;
		}
		
		groundMesh.getFaces()[0] = 3;
		groundMesh.getFaces()[1] = 1;
		groundMesh.getFaces()[2] = 0;
		
		groundMesh.getFaces()[3] = 0;
		groundMesh.getFaces()[4] = 2;
		groundMesh.getFaces()[5] = 3;
		
		groundMesh.computeFaceNormals();
		
		return groundMesh;
	}
	
	
	public void resetScene() {
		if(isSimulated){
			//#ifdef SHOW_NUM_DEEP_PENETRATIONS
			BulletStats.gNumDeepPenetrationChecks = 0;
			BulletStats.gNumGjkChecks = 0;
			//#endif //SHOW_NUM_DEEP_PENETRATIONS
	
			int numObjects = 0;
			if (dynamicsWorld != null) {
				dynamicsWorld.stepSimulation(1f / 60f, 0);
				numObjects = dynamicsWorld.getNumCollisionObjects();
			}
	
			for (int i = 0; i < numObjects; i++) {
				CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
				RigidBody body = RigidBody.upcast(colObj);
				if (body != null) {
					if (body.getMotionState() != null) {
						DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
						myMotionState.graphicsWorldTrans.set(myMotionState.startWorldTrans);
						colObj.setWorldTransform(myMotionState.graphicsWorldTrans);
						colObj.setInterpolationWorldTransform(myMotionState.startWorldTrans);
						colObj.activate();
					}
					// removed cached contact points
					dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(colObj.getBroadphaseHandle(), dynamicsWorld.getDispatcher());
	
					body = RigidBody.upcast(colObj);
					if (body != null && !body.isStaticObject()) {
						RigidBody.upcast(colObj).setLinearVelocity(new Vector3f(0f, 0f, 0f));
						RigidBody.upcast(colObj).setAngularVelocity(new Vector3f(0f, 0f, 0f));
					}
				}
			}
		}
		else if(current != null){
			for(Animator a : current.animators){
				a.resetInterpolator();
			}
		}
	}
	
	public void initJBullet(){
		// jbullet initialization
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		broadphase = new DbvtBroadphase();
		
		SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
		solver = sol;
		
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
	}
	
	public void updateTime(float elapsed){
		if(buildAnimation){
			if(build_time < build_length){
				for(Animator a : saveDestination.animators){
					SceneNode node = a.getNode();
					DefaultMotionState ms = (DefaultMotionState) node.getRigidBody().getMotionState();
					Matrix4f transform = new Matrix4f();
					ms.graphicsWorldTrans.getMatrix(transform);
					
					a.addKeyFrame(build_time, transform);
				}
				build_time += elapsed;
			}else{
				buildAnimation=false;
				if(buildFile!=null){
					try{
						saveAnimation(saveDestination.name, buildFile);
					}catch(IOException ioe){
						ioe.printStackTrace();
					}
				}
			}
		}
		
		if(isAnimated){
			if(play){
				animation_time += elapsed;
			}
			
			for(Animator a : current.animators)
				a.interpolate(animation_time);
		}
		else if(!simPaused){
			updateForces(elapsed);
			
			// step the simulation
			if (dynamicsWorld != null){
				dynamicsWorld.stepSimulation(elapsed, 10, mintimestep);
			}
		}
	}
}
