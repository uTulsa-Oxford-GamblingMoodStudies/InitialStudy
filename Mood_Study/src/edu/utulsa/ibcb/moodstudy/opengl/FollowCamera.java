package edu.utulsa.ibcb.moodstudy.opengl;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;

public class FollowCamera extends Camera {

	SceneNode node;
	Vector3f relPosition;
	
	@Override
	public Vector3f getTarget() {
		if(node.isSimulated()){
			DefaultMotionState ms = (DefaultMotionState)node.getRigidBody().getMotionState();
			return new Vector3f(ms.graphicsWorldTrans.origin);
		}else{
			return new Vector3f(node.getTranslation());
		}
	}

	@Override
	public Vector3f getPosition() {
		Vector3f pos = getTarget();
		pos.add(relPosition);
		return pos;
	}

	public void setTarget(SceneNode node){
		this.node=node;
	}
	
	public void setRelPosition(Vector3f m){
		relPosition = m;
	}
}
