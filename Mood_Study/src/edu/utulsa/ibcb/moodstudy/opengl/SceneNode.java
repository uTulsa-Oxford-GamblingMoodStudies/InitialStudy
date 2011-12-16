package edu.utulsa.ibcb.moodstudy.opengl;

import java.util.LinkedList;
import java.util.List;


import Jama.Matrix;

public class SceneNode {
	
	// change Object to GL10
	public interface RenderState {
		public void start(Object o);
		public void stop(Object o);
	}
	
	Matrix orientation;
	Matrix translation;
	
	String name;
	
	protected boolean collider;
	
	LinkedList<Mesh> meshes = new LinkedList<Mesh>();
	
	SceneNode parent = null;
	LinkedList<SceneNode> children = new LinkedList<SceneNode>();
	
	RenderState rs = null;
	
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
		
		orientation = Matrix.identity(3, 3);
		translation = new Matrix(3, 1);
	}
	public void setOrientation(Matrix m){
		orientation = m;
	}
	public void setPosition(Matrix v){
		translation = v;
	}
	public void orient(Matrix m){
		orientation = orientation.times(m);
	}
	public void translate(Matrix v){
		translation.plusEquals(v);
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
	
	public Matrix getOrientation(){
		return orientation;
	}
	public Matrix getTranslation(){
		return translation;
	}
}
