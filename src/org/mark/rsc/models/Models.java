package org.mark.rsc.models;

import java.util.Vector;


public class Models {

	public int triangle_cnt;
	public int[][] triangle_verts;
	public int[] triangle_vert_cnt;
	public int use_gourad;
	public int[] triangle_shade;
	public int[] texture_back;
	public int[] texture_front;
	public int vert_cnt;
	public int[] vert_z;
	public int[] vert_y;
	public int[] vert_x;
	public int name;

	public final Model buildRSCModel() {
		Model model = new Model();
		Vector<Vertice> vert = new Vector<Vertice>();
		Vector<Triangle> triangles = new Vector<Triangle>();
		for (int l = 0; l < vert_cnt; l++) {
			vert.add(new Vertice(vert_x[l], vert_y[l], vert_z[l]));
		}
		for (int f = 0; f < triangle_cnt; f++) {
			int textureID = -1;
			/*if (texture_front[f] < 0 || texture_front[f] > 0) {
				textureID = texture_front[f];
			}
			if (texture_back[f] < 0 || texture_back[f] > 0) {
				textureID = texture_back[f];
			}*/
			if(texture_front[f] < 0) {
				textureID = texture_front[f];
			}
			if(texture_front[f] > 0) {
				textureID = texture_front[f];
			}
			if(texture_back[f] < 0 && (!(texture_front[f] < 0))) {
				textureID = texture_back[f];
			}
			if(texture_back[f] > 0 && (!(texture_front[f] > 0))) {
				textureID = texture_back[f];
			}
			float[] tester = new float[triangle_vert_cnt[f]];
			for (int i3 = 0; i3 < triangle_vert_cnt[f]; i3++) {
				tester[i3] = triangle_verts[f][i3];
			}
			triangles.add(new Triangle(tester, textureID));
		}
		model.setVertices(vert);
		model.setTriangles(triangles);
		return model;
	}

}
