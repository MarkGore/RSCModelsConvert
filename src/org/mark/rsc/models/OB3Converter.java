package org.mark.rsc.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.mark.rsc.cache.Model;
import org.mark.rsc.cache.Models;
import org.mark.rsc.utils.Type;

public class OB3Converter {

	public OB3Converter(final Models models, final Type type, final File outputFile) {
		if (type.equals(Type.X3D)) {
			try {
				PrintWriter writer = new PrintWriter(outputFile.toString()
						+ ".x3d", "UTF-8");
				Model model = models.buildRSCModel();
				writer = buildHeader(model, writer, outputFile.toString()
						.replace(".\\tester\\", ""));
				writer.println("  <Shape>");
				writer.print("   <IndexedFaceSet coordIndex=\"");
				for (int i = 0; i < model.getTriangles().size(); i++) {
					for (float tri : model.getTriangle(i).points) {
						writer.print((int)tri + " ");
					}
					writer.print("-1 ");
				}
				writer.print("\" colorPerVertex=\"false\" solid=\"false\" normalPerVertex=\"false\">");
				writer.println();
				writer.print("	<Coordinate point=\"");
				for (int i = 0; i < model.getVertices().size(); i++) {
					writer.print((model.getVert(i).x) + " "
							+ (model.getVert(i).y) + " "
							+ (model.getVert(i).z) + " ");
				}
				writer.print("\"/>");
				writer.println();
				writer.print("	<ColorRGBA color=\"");
				for (int i = 0; i < model.getTriangles().size(); i++) {
					if (model.getTriangle(i).texture < 0)
						writer.print(model.getTriangle(i).getRGBColor()
								.getRed()
								/ 255.0f
								+ " "
								+ model.getTriangle(i).getRGBColor().getGreen()
								/ 255.0f
								+ " "
								+ model.getTriangle(i).getRGBColor().getBlue()
								/ 255.0f
								+ " "
								+ model.getTriangle(i).getRGBColor().getAlpha()
								/ 255.0f + " ");
					else {
						writer.print(model.getTriangle(i).texture + " "
								+ model.getTriangle(i).texture + " "
								+ model.getTriangle(i).texture + " "
								+ model.getTriangle(i).texture + " ");
					}
				}
				writer.print("\"/>");
				writer.println();
				writer.println("  </IndexedFaceSet>");
				writer.println("  </Shape>");
				writer = buildFooter(writer);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private PrintWriter buildHeader(Model model, PrintWriter writer, String name) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.1//EN\" \"http://www.web3d.org/specifications/x3d-3.1.dtd\">");
		writer.println("<X3D profile=\"Immersive\" version=\"3.1\" xsd:noNamespaceSchemaLocation=\"http://www.web3d.org/specifications/x3d-3.1.xsd\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema-instance\">");
		writer.println(" <head>");
		writer.println("  <meta content=\"" + name + ".x3d\" name=\"title\"/>");
		writer.println("  <meta content=\"Generated from Mark Gores X3D Exported\" name=\"description\"/>");
		writer.println("  <meta content=\"18 April 2013\" name=\"created\"/>");
		writer.println("  <meta content=\"Mark Gore X3D Exported, https://www.facebook.com/mark.gore.5?ref=tn_tnmn\" name=\"generator\"/>");
		writer.println(" </head>");
		writer.println(" <Scene>");
		return writer;
	}

	private PrintWriter buildFooter(PrintWriter writer) {
		writer.println(" </Scene>");
		writer.println("</X3D>");
		return writer;
	}
}
