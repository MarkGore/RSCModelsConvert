package org.mark.rsc.models;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.mark.rsc.models.obj.Triangle;
import org.mark.rsc.models.obj.Vertice;

public class X3DConvertion {

	public ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	public ArrayList<Vertice> vertices = new ArrayList<Vertice>();

	public X3DConvertion(File model) {
		try {
			File inp = new File(model.toString());
			if (!inp.exists())
				throw new RuntimeException("Input file not found: ");
			BufferedReader br = new BufferedReader(new FileReader(inp));
			convert(br);
			saveToFile(model+".ob3");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void convert(BufferedReader br) {
		try {
			int currentTexture = -1;

			while (true) {
				String line = br.readLine();

				if (line == null)
					break;

				String head = line;
				if (head.startsWith("   <IndexedFaceSet")) {
					head = head.replace("   <IndexedFaceSet coordIndex=\"", "");
					head = head
							.replace(
									"\" colorPerVertex=\"false\" solid=\"false\" normalPerVertex=\"false\">",
									"");
					String[] splitter = head.split(" -1 ");
					for (String split : splitter) {
						String[] splits = split.split(" ");
						float[] tri = new float[splits.length];
						int count = 0;
						for (String length : splits) {
							tri[count] = Float.parseFloat(length.replaceAll(
									" ", ""));
							count++;
						}
						triangles.add(new Triangle(tri, currentTexture));
					}
				}
				if (head.startsWith("	<Coordinate point=\"")) {
					head = head.replace("	<Coordinate point=\"", "");
					head = head.replace(" \"/>", "");
					String[] splitter = head.split(" ");
					int count = 0;
					float[] vert = new float[3];
					for (String split : splitter) {
						if (count == 2) {
							vert[count] = Float.parseFloat(split.replaceAll(
									" ", ""));
							vertices.add(new Vertice(-vert[0], -vert[1],
									vert[2]));
							count = 0;
						} else {
							vert[count] = Float.parseFloat(split.replaceAll(
									" ", ""));
							count++;
						}
					}
				}
				if (head.startsWith("	<ColorRGBA color=\"")) {
					head = head.replace("	<ColorRGBA color=\"", "");
					head = head.replace(" \"/>", "");
					String[] splitter = head.split(" ");
					int count = 0;
					int pos = 0;
					float[] color = new float[4];
					for (String split : splitter) {
						if (count == 3) {
							color[count] = Float.parseFloat(split);
							boolean texture = true;
							for (float colorCheck : color) {
								if (!(colorCheck >= 1)) {
									texture = false;
								}
							}
							if (texture) {
								triangles.get(pos).texture = (int) color[count];
							} else {
								triangles.get(pos).texture = triangles
										.get(pos)
										.getRsColor(
												new Color(
														(int) (color[0] * 255.0f),
														(int) (color[1] * 255.0f),
														(int) (color[2] * 255.0f),
														(int) (color[3] * 255.0f)));
							}
							count = 0;
							pos++;
						} else {
							color[count] = Float.parseFloat(split);
							count++;
						}
					}
				}
			}

		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	public boolean saveToFile(String file) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					file));

			boolean ret = saveToFile(dos);
			dos.close();
			return ret;

		} catch (IOException ioex) {
			ioex.printStackTrace();
			return false;
		}

	}

	public boolean saveToFile(DataOutputStream dos) {
		try {
			int v = vertices.size();
			int t = triangles.size();

			dos.writeChar(v);
			dos.writeChar(t);

			for (int i = 0; i < v; i++) {
				dos.writeChar((int) (vertices.get(i).x));
			}

			for (int i = 0; i < v; i++) {
				dos.writeChar((int) (vertices.get(i).y));
			}

			for (int i = 0; i < v; i++) {
				dos.writeChar((int) (vertices.get(i).z));
			}

			for (int i = 0; i < t; i++) {
				dos.writeByte(triangles.get(i).points.length);
			}

			for (int i = 0; i < t; i++) {
				dos.writeChar(triangles.get(i).texture);
			}

			for (int i = 0; i < t; i++) {
				dos.writeChar(triangles.get(i).texture);
			}

			for (int i = 0; i < t; i++) {
				dos.writeByte(0);
			}

			if (v < 256) {
				for (int i = 0; i < t; i++) {
					for (float s : triangles.get(i).points)
						dos.writeByte((int) s);
				}
			} else {
				for (int i = 0; i < t; i++) {
					for (float s : triangles.get(i).points)
						dos.writeChar((int) s);
				}
			}
			dos.flush();
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return false;
		}
		return true;
	}

}
