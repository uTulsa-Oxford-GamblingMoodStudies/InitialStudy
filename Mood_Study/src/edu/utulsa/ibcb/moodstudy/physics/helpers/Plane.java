package edu.utulsa.ibcb.moodstudy.physics.helpers;

import Jama.Matrix;

public class Plane {
	Matrix normal;
	Matrix origin;
	
	public double distance(Matrix p){
		Matrix v = p.minus(origin);
		return v.transpose().times(normal).getArray()[0][0];
	}
}
