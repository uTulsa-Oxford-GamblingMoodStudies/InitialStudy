package edu.utulsa.ibcb.moodstudy.opengl;

import android.opengl.GLU;
import Jama.Matrix;

public class Camera {
	
	Matrix target;
	Matrix position;
	
	public Camera(){
		target = new Matrix(3,1);
		position = new Matrix(3,1);
	}
	
	public void lookAt(GLU glu){
		
	}
	
	public Matrix getWorldCoordinates(){
		return target.plus(position);
	}
	public Matrix getTarget(){
		return target;
	}
	public void setTarget(Matrix m){
		target.setMatrix(0, 2, 0, 0, m);
	}
	public void setPosition(Matrix m){
		position.setMatrix(0, 2, 0, 0, m);
	}
}
