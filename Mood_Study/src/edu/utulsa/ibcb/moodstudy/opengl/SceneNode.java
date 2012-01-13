package edu.utulsa.ibcb.moodstudy.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.dynamics.RigidBody;

public class SceneNode {
	
	// change Object to GL10
	public interface RenderState {
		public void start(Object o);
		public void stop(Object o);
	}
	
	protected Vector3f x;
	protected Matrix3f R;
	
	protected RigidBody rb = null;
	
	String name;
	
	SceneNode parent = null;
	ArrayList<SceneNode> children = new ArrayList<SceneNode>();
	
	ArrayList<Mesh> meshes = new ArrayList<Mesh>();
	
	RenderState rs = null;
	
	public boolean isSimulated(){
		return rb != null;
	}
	
	public RigidBody getRigidBody(){
		return rb;
	}
	
	public void setRigidBody(RigidBody rb){
		this.rb = rb;
	}
	
	public void setOrientation(Matrix3f m){
		R = m;
	}
	public void setPosition(Vector3f v){
		x = v;
	}
	public void orient(Matrix3f m){
		R.mul(m);
	}
	public void translate(Vector3f v){
		x.add(v);
	}
	public Matrix3f getOrientation(){
		return R;
	}
	public Vector3f getTranslation(){
		return x;
	}
	
	public void setParent(SceneNode n){
		parent = n;
	}
	
	public String getName(){
		return name;
	}
	
	public void setRenderState(RenderState rs){
		this.rs=rs;
	}
	
	public RenderState getRenderState(){
		return rs;
	}
	
	public void addChild(SceneNode n){
		n.setParent(this);
		children.add(n);
	}
	
	public SceneNode(String n){
		name=n;
		
		R = new Matrix3f(Solver.identity);
		x = new Vector3f(0,0,0);
	}
	
	
	public List<Mesh> getObjects(){
		return meshes;
	}
	
	public void init(){
		// create display lists using the meshes
	}
	
	public void addMesh(Mesh object){
		meshes.add(object);
	}
	
	public List<SceneNode> getChildren(){
		return children;
	}
	
}
