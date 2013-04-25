package org.mark.rsc.models; 

/**
 * @author Mark
 * 
 */
public class Vertice {

	public Vertice(float vx, float vy, float vz) {
		setX(vx);
		setY(vy);
		setZ(vz);
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getZ() {
		return z;
	}

	private float x;
	private float y;
	private float z;
}
