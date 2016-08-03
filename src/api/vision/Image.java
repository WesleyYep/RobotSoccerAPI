package api.vision;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Consists of static utility classes when working with images</p>
 * @author Chang Kon, Wesley, John
 *
 */

public class Image {

	/**
	 * <p>Takes a source image and resizes it to the given width and height value</p>
	 * @param image
	 * @param width
	 * @param height
	 * @return resized bufferedimage
	 */

	public static BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resize = new BufferedImage(width, height, image.getType());
		Graphics2D g2d = resize.createGraphics();
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return resize;
	}
    
    /**
     * <p>Transforms mat matrix into bufferedImage</p>
     * @param Mat matrix
     * @return bufferedImage
     */
    
    public static BufferedImage toBufferedImage(Mat matrix) {
        MatOfByte bytemat = new MatOfByte();

        Highgui.imencode(".jpg", matrix, bytemat);

        byte[] bytes = bytemat.toArray();

        InputStream in = new ByteArrayInputStream(bytes);

        try {
            BufferedImage img = ImageIO.read(in);
            return  img;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        int type = BufferedImage.TYPE_BYTE_GRAY;
//
//        if ( matrix.channels() > 1 ) {
//            type = BufferedImage.TYPE_3BYTE_BGR;
//        }
//
//        int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
//        byte [] b = new byte[bufferSize];
//
//        matrix.get(0, 0, b); // get all the pixels
//        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
//        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//
//        System.arraycopy(b, 0, targetPixels, 0, b.length);
//        return image;
    }
    
    /**
     * <p>Transforms bufferedimage to Mat</p>
     * @param BufferedImage image
     * @return Mat
     */
    
    public static Mat toMat(BufferedImage image) {
    	
    	byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    	Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
    	mat.put(0, 0, data);
    	
    	
    	return mat;
    }
    
}
