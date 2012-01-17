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

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import edu.utulsa.ibcb.moodstudy.R;
import edu.utulsa.ibcb.moodstudy.opengl.FollowCamera;
import edu.utulsa.ibcb.moodstudy.opengl.Mesh;
import edu.utulsa.ibcb.moodstudy.opengl.SceneNode;
import edu.utulsa.ibcb.moodstudy.opengl.Solver;
import edu.utulsa.ibcb.moodstudy.opengl.SceneNode.RenderState;
import edu.utulsa.ibcb.moodstudy.opengl.TargetCamera;

public class CupEnvironment extends Environment {
	
	TargetCamera cam;
	
	SceneNode die;
	SceneNode diefaces;
	SceneNode cup;
	
	SceneNode left;
	SceneNode right;
	SceneNode bottom;
	SceneNode front;
	SceneNode back;
	
	public synchronized void initPhysicalProperties(){
		Transform t = new Transform();
		t.setIdentity();
		
		float w = (float)Math.random();
		float x = (float)Math.random();
		float y = (float)Math.random();
		float z = (float)Math.random();
		Quat4f q = new Quat4f(w,x,y,z);
		q.normalize();
		t.setRotation(q);
		
		t.origin.set(0,7,0);
		
		die.getRigidBody().setWorldTransform(t);
		
		dynamicsWorld.setGravity(new Vector3f(0,-20f,0));
		
		die.getRigidBody().applyCentralImpulse(new Vector3f(10,0,0));
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
	}
	
	public synchronized  void setupEnvironment(GL10 gl){
		
		try{
			List<Mesh> mobjects = Mesh.loadASE(DiceRenderer.getContext().getResources().getAssets().open("dice.ASE"));
			
			
			bottom = new SceneNode("cup-bottom");
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
				bottom.setRigidBody(body);
			}
			
			left = new SceneNode("cup-left");
			{
				BoxShape sps = new BoxShape(new Vector3f(1,4,4));
				collisionShapes.add(sps);
				
				Transform groundTransform = new Transform();
				groundTransform.setIdentity();
				
				groundTransform.origin.set(-3.5f,3.5f,0);
				
				float mass = 0f;
				Vector3f localInertia = new Vector3f(0, 0, 0);

				// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
				DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, sps, localInertia);
				RigidBody body = new RigidBody(rbInfo);
				
				// add the body to the dynamics world
				dynamicsWorld.addRigidBody(body);
				left.setRigidBody(body);
			}
			
			right = new SceneNode("cup-right");
			{
				BoxShape sps = new BoxShape(new Vector3f(1,4,4));
				collisionShapes.add(sps);
				
				Transform groundTransform = new Transform();
				groundTransform.setIdentity();
				
				groundTransform.origin.set(3.5f,3.5f,0);
				
				float mass = 0f;
				Vector3f localInertia = new Vector3f(0, 0, 0);

				// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
				DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, sps, localInertia);
				RigidBody body = new RigidBody(rbInfo);
				
				// add the body to the dynamics world
				dynamicsWorld.addRigidBody(body);
				right.setRigidBody(body);
			}
			
			front = new SceneNode("cup-front");
			{
				BoxShape sps = new BoxShape(new Vector3f(4,4,1));
				collisionShapes.add(sps);
				
				Transform groundTransform = new Transform();
				groundTransform.setIdentity();
				
				groundTransform.origin.set(0,3.5f,3.5f);
				
				float mass = 0f;
				Vector3f localInertia = new Vector3f(0, 0, 0);

				// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
				DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, sps, localInertia);
				RigidBody body = new RigidBody(rbInfo);
				
				// add the body to the dynamics world
				dynamicsWorld.addRigidBody(body);
				front.setRigidBody(body);
			}
			
			back = new SceneNode("cup-back");
			{
				BoxShape sps = new BoxShape(new Vector3f(4,4,1));
				collisionShapes.add(sps);
				
				Transform groundTransform = new Transform();
				groundTransform.setIdentity();
				
				groundTransform.origin.set(0,3.5f,-3.5f);
				
				float mass = 0f;
				Vector3f localInertia = new Vector3f(0, 0, 0);

				// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
				DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, sps, localInertia);
				RigidBody body = new RigidBody(rbInfo);
				
				// add the body to the dynamics world
				dynamicsWorld.addRigidBody(body);
				back.setRigidBody(body);
			}
			
			cup = new SceneNode("cup");
			
			List<Mesh> cobjects = Mesh.loadASE(DiceRenderer.getContext().getResources().getAssets().open("cup.ASE"));
			
			for(Mesh m : cobjects){
				cup.addMesh(m);
				//m.scale(0.01);
				
				for(float[] v : m.getVertices()){
					System.out.println(" " + v[0] + " " + v[1] + " " + v[2]);
				}
				
				m.createGLArray();
			}
			
			
			Matrix3f rot = Solver.getRotationMatrixZ((float)(-Math.PI/2.0));
			rot.mul(Solver.getRotationMatrix((float)(-Math.PI/2.0), new Vector3f(0,1,0)));
			rot.mul(Solver.getRotationMatrixZ((float)Math.PI));
			cup.setOrientation(rot);
			
			cup.setRenderState(new RenderState(){
				public void start(Object o) {
					GL10 gl = (GL10)o;
					gl.glColor4f(0.2f, 0.2f, 0.4f, 1.0f);
					gl.glDisable(GL10.GL_TEXTURE_2D);
				}
				public void stop(Object o) {
					GL10 gl = (GL10)o;
				}
			});
			
			cup.setPosition(new Vector3f(0,0.55f,0));
			
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
			
			die.setRenderState(new RenderState(){
				public void start(Object o) {
					GL10 gl = (GL10)o;
					gl.glColor4f(0.8f, 0.8f, 0.8f,1.0f);
					gl.glDisable(GL10.GL_TEXTURE_2D);
				}
				public void stop(Object o) {
					GL10 gl = (GL10)o;
				}
			});
			
			
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
				
				// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
				DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, dieshape, localInertia);
				RigidBody body = new RigidBody(rbInfo);
				body.setActivationState(RigidBody.ISLAND_SLEEPING);
				
				dynamicsWorld.addRigidBody(body);
				body.setActivationState(RigidBody.ISLAND_SLEEPING);
				
				die.setRigidBody(body);
			}
			
			float restitution = 0.025f;
			float friction = 0.5f;
				
			die.getRigidBody().setRestitution(restitution);
			bottom.getRigidBody().setRestitution(restitution);
			bottom.getRigidBody().setFriction(friction);
			left.getRigidBody().setFriction(friction);
			left.getRigidBody().setRestitution(restitution);
			right.getRigidBody().setFriction(friction);
			right.getRigidBody().setRestitution(restitution);
			front.getRigidBody().setFriction(friction);
			front.getRigidBody().setRestitution(restitution);
			back.getRigidBody().setFriction(friction);
			back.getRigidBody().setRestitution(restitution);
			
			cam = new TargetCamera();
			cam.setRelPosition(new Vector3f(0,0,12));
			cam.setTarget(new Vector3f(0,5,0));
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
			objects.add(cup);
			
			//objects.add(bottom);
			//objects.add(left);
			//objects.add(right);
			//objects.add(front);
			//objects.add(back);
			
			root.addChild(cup);
			root.addChild(die);
			die.addChild(diefaces);
			
			//root.addChild(bottom);
			//root.addChild(left);
			//root.addChild(right);
			//root.addChild(front);
			//root.addChild(back);
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public synchronized boolean diceLeftCup(){
		DefaultMotionState ms = (DefaultMotionState)die.getRigidBody().getMotionState();
		Transform t = new Transform();
		ms.getWorldTransform(t);
		
		return t.origin.y > 8;
	}
	
	public synchronized float diceVerticalVelocity(){
		Vector3f vel = new Vector3f();
		die.getRigidBody().getLinearVelocity(vel);
		
		return vel.y;
	}
	
	float gx=0,gy=-20f,gz=0;
	
	public synchronized void throwDie(){
		die.getRigidBody().applyImpulse(new Vector3f(0,50,0), new Vector3f(0,-0.8f,-0.5f));
	}
	
	public synchronized void updateForces(float dt){
		dynamicsWorld.setGravity(new Vector3f(gx,gy,gz));
	}
	
	public synchronized void setGravity(float x, float y, float z){
		gx=x;
		gy=y;
		gz=z;
	}
}
