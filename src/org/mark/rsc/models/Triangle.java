package org.mark.rsc.models; 

import java.awt.Color;

/**
 * @author Mark
 * 
 */
public class Triangle {

	public Triangle(float[] tri, final int txt) {

		setPoints(new float[tri.length]);

		for (int i = 0; i < tri.length; i++) {
			getPoints()[i] = tri[i];
		}

		setTexture(txt);
	}

	public final Color getRGBColor() {
		return getColor(getTexture());
	}

	public final int getRsColor(final Color c) {
		int rgbValue = c.getRGB();
		int red = (rgbValue >> 16) & 0xff;
		int green = (rgbValue >> 8) & 0xff;
		int blue = rgbValue & 0xff;
		int multiplier = 255 * 255;
		int red_raw = (red * 0x10000) / multiplier;
		int green_raw = (green * 0x10000) / multiplier;
		int blue_raw = (blue * 0x10000) / multiplier;
		int rscColor = (((red_raw / 8) & 0x1f) << 10)
				| (((green_raw / 8) & 0x1f) << 5) | ((blue_raw / 8) & 0x1f);
		return -rscColor - 1;
	}

	public Color getColor(int rscColor) {
		rscColor = -1 - rscColor;
		int k2 = (rscColor >> 10 & 0x1f) * 8;
		int j3 = (rscColor >> 5 & 0x1f) * 8;
		int l3 = (rscColor & 0x1f) * 8;
		int rgbValue = 0;
		int j6 = 255 * 255;
		int red = (k2 * j6) / 0x10000;
		int green = (j3 * j6) / 0x10000;
		int blue = (l3 * j6) / 0x10000;
		rgbValue = ((red << 16) + (green << 8) + blue);
		return new Color(rgbValue);
	}

	public void setTexture(int texture) {
		this.texture = texture;
	}

	public int getTexture() {
		return texture;
	}

	public void setPoints(float[] points) {
		this.points = points;
	}

	public float[] getPoints() {
		return points;
	}

	private int texture;
	private float[] points;

}
