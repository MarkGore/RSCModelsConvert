package org.mark.rsc.cache;

import java.util.Vector;

import org.mark.rsc.models.obj.Triangle;
import org.mark.rsc.models.obj.Vertice;

public class Model {

	private Vector<Vertice> vert = new Vector<Vertice>();
	private Vector<Triangle> triangles = new Vector<Triangle>();
	public void setVertices(Vector<Vertice> vert) {
		this.vert = vert;
	}
	public Vector<Vertice> getVertices() {
		return vert;
	}
	public void setTriangles(Vector<Triangle> triangles) {
		this.triangles = triangles;
	}
	public Vector<Triangle> getTriangles() {
		return triangles;
	}
	public Triangle getTriangle(int i) {
		return triangles.get(i);
	}
	public Vertice getVert(int i) {
		return vert.get(i);
	}

}
