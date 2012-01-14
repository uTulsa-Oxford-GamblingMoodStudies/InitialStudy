package edu.utulsa.ibcb.moodstudy.physics;

import edu.utulsa.ibcb.moodstudy.opengl.Mesh;
import edu.utulsa.ibcb.moodstudy.opengl.SceneNode;
import edu.utulsa.ibcb.moodstudy.physics.helpers.Plane;


// implement rigid body animation using this class
public class RigidBody extends SceneNode {
	
	// Matrix Ibody;
	// double mass;
	// Matrix v
	// Matrix 
	
	boolean axisLock[] = new boolean[]{false,false,false};
	
	public RigidBody(String n){
		super(n);
//		super.collider=true;
	}
	
	public void setParent(SceneNode n){
		super.setParent(n);
		if(!n.getName().equals("root")){
			System.err.println("WARNING: Rigid body objects should be in world-space (children of 'root')");
		}
	}
	
	public void planeCollision(Plane p){
		
	}
	
	public void init(){
		super.init();
		
		
	}
	
	public int getDrawable(){
		return 1;
	}
}
