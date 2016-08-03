package ui;

import api.data.BoardProperties;
import api.data.Point;
import api.listeners.WebcamDisplayPanelListener;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Displays the webcam on the JPanel.</p>
 * <p>{@link ui.RobotSoccerMain}</p>
 * @author Chang Kon, Wesley, John
 *
 */

@SuppressWarnings("serial")
public class WebcamDisplayPanel extends JPanel {

	private ViewState currentViewState;
	private JLabel webcamImageLabel = new JLabel();
    private ArrayList<WebcamDisplayPanelListener> wdpListeners;
    private SamplingPanel samplingPanel;
    private boolean isFiltering = false;
    private BufferedImage zoomCursorImg;
    private Cursor zoomCursor;
    private List<MatOfPoint> ballContour;
	private List<MatOfPoint> greenContour;
	private List<MatOfPoint> teamContour;
	private List<MatOfPoint> opponentContour;
	private ColourPanel colourPanel = null;
	private BoardProperties boardProperties;
    
	public WebcamDisplayPanel() {
		super();
		// Initially not connected to anything.
		currentViewState = ViewState.UNCONNECTED;
		wdpListeners = new ArrayList<WebcamDisplayPanelListener>();
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		
		ballContour = null;
		try {
			zoomCursorImg = ImageIO.read(getClass().getClassLoader().getResourceAsStream("zoom.png"));
			zoomCursor = Toolkit.getDefaultToolkit().createCustomCursor(zoomCursorImg, new java.awt.Point(zoomCursorImg.getWidth() / 2, zoomCursorImg.getHeight() / 2), "Zoom cursor");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("Could not find zoom.png file");
		}


        webcamImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (WebcamDisplayPanelListener listener : wdpListeners) {
                    if (listener instanceof ColourPanel) {
                        ColourPanel cp = (ColourPanel) listener;
                        
                        // Get the current bufferedimage.
                        BufferedImage image = getWebcamBufferedImage();

                        int x = e.getX() - zoomCursorImg.getWidth() / 2;
                        int y = e.getY() - zoomCursorImg.getHeight() / 2;

                        // Crop the image
                        BufferedImage crop = image.getSubimage(x, y, zoomCursorImg.getWidth(), zoomCursorImg.getHeight());
                        cp.setZoomLabelIcon(crop);
                        
                        if (cp.getIsGettingRobotDimension()) {
                            cp.setRobotDimension(e.getX(), e.getY());
                        }
                        
                    } else if (listener instanceof VisionPanel) {
                    	/*
                        VisionPanel panel = (VisionPanel) listener;
                        if (panel.isSelectedTab()) {
             //               panel.updateMousePoint(e.getX(), e.getY(), img.getBufferedImage());
                        	System.out.println(VisionController.imagePosToActualPos(new org.opencv.core.Point(e.getX(), e.getY())));
                        } */
                    }
                }
            }
        });
	}
	
	/**
	 * <p>Receives webcam and updates view.</p>
	 * <p>Notifies all listeners of view state change</p>
	 */
	
	public void update(final Mat matToShow) {
		long start = System.currentTimeMillis();
		ViewState oldViewState = currentViewState;
		
		if (matToShow == null) {
			
			/* 
			 * This assumes that you cannot have a connection fail if you're already connected hence you are disconnecting.
			 * If you are unconnected and you get a null image, connection has failed.
			 */

			if (currentViewState == ViewState.UNCONNECTED) {
				currentViewState = ViewState.connectionFail();
			} else if (currentViewState == ViewState.CONNECTED) {
				currentViewState = ViewState.disconnect();
			}
			
		} else {
			currentViewState = ViewState.connectionSuccess();

//            final BufferedImage image = Image.toBufferedImage(mat);
            final Mat matToProcess;

            if (colourPanel.isContourActive()) {
               	maskCameraImage(matToShow);
				matToProcess = matToShow.clone();
            } else {
                matToProcess = matToShow.clone();
				maskCameraImage(matToProcess);
                //
            }

			if (colourPanel.isGroundMask()) {
				//maskCameraImage(matToShow);
				applyGroundMask(matToShow);
			}

            notifyImageUpdate(matToProcess);
            
            if (colourPanel.isContourActive()) {
            	if (ballContour != null) {
            		for (int i = 0; i<ballContour.size(); i++) {
            			Imgproc.drawContours(matToShow, ballContour, i, new Scalar(0, 255, 128));
            		}
            	}
            	
            	
            	if (greenContour != null) {
            		for (int i = 0; i<greenContour.size(); i++) {
            			Imgproc.drawContours(matToShow, greenContour, i, new Scalar(0, 255, 128));
            		}
            	}
            	
            	if (teamContour != null) {
            		for (int i = 0; i<teamContour.size(); i++) {
            			Imgproc.drawContours(matToShow, teamContour, i, new Scalar(0, 255, 128));
            		}
            	} 

            	if (opponentContour != null) {
            		for (int i = 0; i<opponentContour.size(); i++) {
            			Imgproc.drawContours(matToShow, opponentContour, i, new Scalar(0, 255, 128));
            		}
            	}
            	
            } 

            /*
             * This method is not being called EDT thread so to update the GUI use invokeLater.
             */
            SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// Update the image.
					webcamImageLabel.setIcon(new ImageIcon(api.vision.Image.toBufferedImage(matToShow)));
					
					if (webcamImageLabel.getParent() == null) {
						add(webcamImageLabel, BorderLayout.CENTER);
					}
				}
            });
		}

		// Thread safe call.
		repaint();
	}


    private Mat maskCameraImage(Mat image) {
        //clone the original image so we can subtract the mask from the actual image
        Mat original = image.clone();
        //get the points of the region of interest
        MatOfPoint mop = new MatOfPoint(toPoint(boardProperties.topLeft), toPoint(boardProperties.leftGoalTopRight), toPoint(boardProperties.leftGoalTopLeft), toPoint(boardProperties.leftGoalBottomLeft),
                            toPoint(boardProperties.leftGoalBottomRight), toPoint(boardProperties.bottomLeft), toPoint(boardProperties.bottomRight), toPoint(boardProperties.rightGoalBottomLeft),
                            toPoint(boardProperties.rightGoalBottomRight), toPoint(boardProperties.rightGoalTopRight), toPoint(boardProperties.rightGoalTopLeft), toPoint(boardProperties.topRight));
        //fill the region of interest black  - rgb(0,0,0)
        Core.fillConvexPoly(image, mop, new Scalar(0.0));
        //subtract the mask from original, everywhere will become black apart from the black region that you filled,
        // which will remain the original image
        Core.subtract(original, image, image);

        return image;

    }

	private Mat applyGroundMask(Mat image) {

		if (colourPanel != null) {
			SamplingPanel panel = colourPanel.groundSamplingPanel;
			Scalar hsv_l = new Scalar(panel.getLowerBoundForH(),panel.getLowerBoundForS(), panel.getLowerBoundForV());
			Scalar hsv_h = new Scalar(panel.getUpperBoundForH(),panel.getUpperBoundForS(),panel.getUpperBoundForV());
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV_FULL);
			Core.inRange(image,hsv_l,hsv_h,image);
		}
		return image;
	}

	private org.opencv.core.Point toPoint(Point p) {
        return new org.opencv.core.Point(p.x, p.y);
    }

    @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Get the current state of the displayPanel. Draw text onto screen.
		switch(currentViewState) {
		case CONNECTED:
			break;
		default:
			g.setColor(Color.WHITE);

			// Find width and height of the display panel.
			int width = getWidth();
			int height = getHeight();
			
			String displayMessage = currentViewState.getMessage();
			
			FontMetrics fm = g.getFontMetrics();
			int displayMessageX = (width - fm.stringWidth(displayMessage)) / 2;
			int displayMessageY = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);
			
			g.drawString(displayMessage, displayMessageX, displayMessageY);
		}
	}

    public boolean isDetected(int h, int s, int v) {
        if (!((h >= samplingPanel.getLowerBoundForH()) && (h <= samplingPanel.getUpperBoundForH()))) {
            return false;
        }

        if (!((s >= samplingPanel.getLowerBoundForS()) && (s <= samplingPanel.getUpperBoundForS()))) {
            return false;
        }

        if (!((v >= samplingPanel.getLowerBoundForV()) && (v <= samplingPanel.getUpperBoundForV()))) {
            return false;
        }

        return true;
    }

    public void setIsFiltering(boolean bool) {
        isFiltering = bool;
    }

    public void setSamplingPanel(SamplingPanel sp) {
        this.samplingPanel = sp;
    }
    
    /**
     * <p>Gets the bufferedimage used in display</p>
     * @return BufferedImage of webcam
     */
    
    public BufferedImage getWebcamBufferedImage() {
    	ImageIcon icon = (ImageIcon)webcamImageLabel.getIcon();
    	return (BufferedImage)icon.getImage();
    }
    
	/**
	 * <p>Add instance to be an observer</p>
	 */
	
	public void addWebcamDisplayPanelListener(WebcamDisplayPanelListener l) {
		if (l instanceof ColourPanel) {
			colourPanel = (ColourPanel) l;
		}
		wdpListeners.add(l);
	}
	
	/**
	 * <p>Remove the instance from observer list</p>
	 */
	
	public void removeWebcamDisplayPanelListener(WebcamDisplayPanelListener l) {
		wdpListeners.remove(l);
	}
	
	/**
	 * <p>Notify all observers of image update on webcamImageLabel</p>
     * @param image
     */
	
	public void notifyImageUpdate(Mat image) {
		for (WebcamDisplayPanelListener l : wdpListeners) {
			l.imageUpdated(image.clone());
		}
	}
	
	public void setZoomCursor() {
		setCursor(zoomCursor);
	}
	
	public void setDefaultCursor() {
		setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * <p>Returns the current view state of the WebcamDisplayPanel</p>
	 * @return currentViewState
	 */
	
    public ViewState getViewState() {
        return currentViewState;
    }
    
    /**
	 * <p>Defines the <strong>state</strong> of the display.</p>
	 * <p>Each state has a <strong>display message</strong></p>
	 * @author Chang Kon, Wesley, John
	 *
	 */
	
	public enum ViewState {
		
		UNCONNECTED("Software is not connected to a webcam device"),
		CONNECTED("Connection success"),
		ERROR("An error has occurred! Please fix");
		
		private String displayMessage;
		
		private ViewState(String displayMessage) {
			this.displayMessage = displayMessage;
		}
		
		private String getMessage() {
			return displayMessage;
		}
		
		private static ViewState connectionSuccess() {
			return CONNECTED;
		}
		
		private static ViewState connectionFail() {
			return ERROR;
		}
		
		private static ViewState disconnect() {
			return UNCONNECTED;
		}
		
	}


}
