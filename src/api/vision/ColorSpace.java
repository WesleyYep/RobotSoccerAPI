package api.vision;

import java.awt.*;

/**
 * <p>Contains static methods which have conversions from one colour space to another</p>
 * 
 * @see <a href="http://www.equasys.de/colorconversion.html">Conversion matrix</a>
 * @author Chang Kon, Wesley, John
 *
 */

public class ColorSpace {

	/**
	 * Clip values so that it returns values within 0-255 range.
	 * @param value
	 * @return pixel value
	 */
	
	public static double clip(double value) {
		
		if (value < 0) {
			return 0;
		}
		
		if (value > 255) {
			return 255;
		}
		
		return value;
	}
	
	/**
	 * <p>Convert RGB(0-255) values to YUV(0-255)</p>
	 * <p><strong>Note: </strong>array is in double so precision is not lost when doing extra conversions</p>
	 * @param r
	 * @param g
	 * @param b
	 * @return Array of size 3. [0] = y, [1] = u, [2] = v
	 */
	
	public static double[] RGBToYUV(double r, double g, double b) {
		double[] yuv = new double[3];
		
		yuv[0] = clip((0.299 * r + 0.587 * g + 0.114 * b));
		yuv[1] = clip(((-0.169 * r + -0.331 * g + 0.5 * b) + 128));
		yuv[2] = clip(((0.5 * r + -0.419 * g + -0.081 * b) + 128));
		
		return yuv;
	}
	
	/**
	 * <p>Convert YUV(0-255) values to RGB(0-255)</p>
	 * <p><strong>Note: </strong>array is in double so precision is not lost when doing extra conversions</p>
	 * @param y
	 * @param u
	 * @param v
	 * @return Array of size 3. [0] = r, [1] = g, [2] = b
	 */
	
	public static double[] YUVToRGB(double y, double u, double v) {
		double[] rgb = new double[3];
		
		rgb[0] = clip((y + 1.4 * (v - 128)));
		rgb[1] = clip(((y + -0.343 * (u - 128) + -0.711 * (v - 128))));
		rgb[2] = clip(((y + 1.765 * (u - 128))));
		
		return rgb;
	}
	
	/**
	 * <p>This returns a hsv array of size 3. The elements have been scaled to be within 0-255</p>
	 * <p>0 = H</p>
	 * <p>1 = S</p>
	 * <p>2 = V</p>
	 * @param r
	 * @param g
	 * @param b
	 * @return hsv array. size 3.
	 * @see Color
	 */
	
	public static float[] RGBToHSV(int r, int g, int b) {
		float[] hsv = new float[3];
		Color.RGBtoHSB(r, g, b, hsv);
		
		hsv[0] = hsv[0] * 255;
		hsv[1] = hsv[1] * 255;
		hsv[2] = hsv[2] * 255;
		
		return hsv;
	}
	
	/**
	 * <p>This returns rgb array, size 3. The elements are 0-255</p>
	 * <p>0 = R</p>
	 * <p>1 = G</p>
	 * <p>2 = B</p>
	 * @param h
	 * @param s
	 * @param v
	 * @return rgb array. size 3
	 */
	
	public static int[] HSVToRGB(int h, int s, int v) {
		int rgb = Color.HSBtoRGB(h, s, v);
		Color c = new Color(rgb);
		
		int[] rgbArray = new int[3];
		rgbArray[0] = c.getRed();
		rgbArray[1] = c.getGreen();
		rgbArray[2] = c.getBlue();
		
		return rgbArray;
	}
	
}
