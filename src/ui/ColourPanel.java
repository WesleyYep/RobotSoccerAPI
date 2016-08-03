package ui;

import api.listeners.WebcamDisplayPanelListener;
import com.jidesoft.swing.RangeSlider;
import api.data.Point;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Mat;
import ui.WebcamDisplayPanel.ViewState;
import api.vision.ColourRangeListener;
import api.vision.LookupTable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Created by Wesley on 2/02/2015.
 */
public class ColourPanel extends JPanel implements ColourRangeListener, WebcamDisplayPanelListener {
    public SamplingPanel ballSamplingPanel;
    public SamplingPanel teamSamplingPanel;
    public SamplingPanel groundSamplingPanel;
    public SamplingPanel opponentSamplingPanel;
    public SamplingPanel greenSamplingPanel;
    private SamplingPanel[] samplingPanels;

    private JLabel robotMinSizeLabel = new JLabel("10");
    private JLabel ballMinSizeLabel = new JLabel("10");
    private JLabel greenMinSizeLabel = new JLabel("10");
    
    private JLabel robotMaxSizeLabel = new JLabel("100");
    private JLabel ballMaxSizeLabel = new JLabel("100");
    private JLabel greenMaxSizeLabel = new JLabel("100");
    
    private RangeSlider robotSizeSlider = new RangeSlider(0,500);
    private RangeSlider greenSizeSlider = new RangeSlider(0,500);
    private RangeSlider ballSizeSlider = new RangeSlider(0,500);
    private RangeSlider opponentSizeSlider = new RangeSlider(0,500);

    private JTabbedPane tabPane = new JTabbedPane();
    
    private JCheckBox autoRangeCheckBox;
    private JCheckBox contourCheckBox;
    private JCheckBox blackMaskBox;
    private JCheckBox newYellowVisionCheckBox = new JCheckBox("New Yellow Vision");
    private JCheckBox newNewYellowVisionCheckBox = new JCheckBox("New (New) Yellow Vision");
    private JTextField robotNotPresentField = new JTextField("", 20);
    private JButton robotNotPresentSaveButton = new JButton("Save");
    private boolean robotNotPresentUpdated = false;

    private boolean isAutoRange = false;
    private boolean isContour = false;
    private boolean isGroundMask = false;

    private JLabel zoomLabel;
    private JButton setRobotDimensionButton = new JButton("Click to set robot dimension");
    private JTextField robotDimensionField = new JTextField("8");
    private boolean isGettingRobotDimension = false;
    private Point middleOfRobot;
    private int clickNumber = 1;
    
    private BufferedImage originalImage = null;
    private int selectRadius = 5;
    
    private WebcamDisplayPanel wcPanel = null;

    public ColourPanel() {
        this.setLayout(new MigLayout());
        
        autoRangeCheckBox = new JCheckBox("Auto Range");
        contourCheckBox = new JCheckBox("Contours");
        blackMaskBox = new JCheckBox("ground mask");
        
        robotSizeSlider.setLowValue(10);
        ballSizeSlider.setLowValue(10);
        greenSizeSlider.setLowValue(10);
        
        robotSizeSlider.setHighValue(100);
        ballSizeSlider.setHighValue(100);
        greenSizeSlider.setHighValue(100);
        
        ballSamplingPanel = new SamplingPanel();
        teamSamplingPanel = new SamplingPanel();
        greenSamplingPanel = new SamplingPanel();
        groundSamplingPanel = new SamplingPanel();
        opponentSamplingPanel = new SamplingPanel();
        teamSamplingPanel.setMask(LookupTable.TEAM_COLOUR);
        ballSamplingPanel.setMask(LookupTable.BALL_COLOUR);
        groundSamplingPanel.setMask(LookupTable.GROUND_COLOUR);
        greenSamplingPanel.setMask(LookupTable.GREEN_COLOUR);
        opponentSamplingPanel.setMask(LookupTable.OPPONENT_COLOUR);
        samplingPanels = new SamplingPanel[] { ballSamplingPanel, teamSamplingPanel, greenSamplingPanel, groundSamplingPanel, opponentSamplingPanel };
        tabPane.addTab("Ball", ballSamplingPanel);
        tabPane.addTab("Team", teamSamplingPanel);
        tabPane.addTab("Green", greenSamplingPanel);
        tabPane.addTab("Ground", groundSamplingPanel);
        tabPane.addTab("Opponent", opponentSamplingPanel);
        
        tabPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				for (int i = 0; i < tabPane.getComponentCount(); i++) {
					SamplingPanel sp = (SamplingPanel)tabPane.getComponentAt(i);
					sp.resetButton();
				}
			}
        	
        });
        
        zoomLabel = new JLabel();
        zoomLabel.setSize(new Dimension(150, 150));
        zoomLabel.setPreferredSize(new Dimension(150, 150));
        zoomLabel.setOpaque(true);
        zoomLabel.setBackground(Color.BLACK);
        
        zoomLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (getIsSampling()) {
					if (zoomLabel.getIcon() != null) {
						takeSample(e.getX(), e.getY());
					} else {
						System.err.println("No image to sample");
					}
				}
			}
		
        	
        });
        
        zoomLabel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (zoomLabel.getIcon() != null) {			
					displayCircleOnIcon(e);
				}
			}
        	
        });
        
        zoomLabel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				int notches = arg0.getWheelRotation();
			    if (notches < 0) {
			          selectRadius++;
			          if (selectRadius > 20) {
			        	  selectRadius = 20;
			          }
 			          displayCircleOnIcon(arg0);
			     } else {
			          selectRadius--;
			          if (selectRadius < 1) {
			        	  selectRadius = 1;
			          }
			          displayCircleOnIcon(arg0);
			     }
			}
        
        });
        
        add(zoomLabel, "span, align center, wrap");
        add(tabPane, "span, pushx, growx, wrap");
        add(new JLabel("Robot Pixel Range"), "wrap");
        add(tabPane, "wrap");
        add(new JLabel("Robot Pixel Minimum"), "wrap");
        add(robotSizeSlider, "grow, wrap");
        add(robotMinSizeLabel, "split 2, pushx, growx");
        add(robotMaxSizeLabel, "align right, wrap");
        add(new JLabel("Green Pixel Minimum"), "wrap");
        add(greenSizeSlider, "grow, wrap");
        add(greenMinSizeLabel, "split 2, pushx, growx");
        add(greenMaxSizeLabel, "align right, wrap");
        add(new JLabel("Ball Pixel Minimum"), "wrap");
        add(ballSizeSlider, "grow,wrap");
        add(ballMinSizeLabel, "split 2, pushx, growx");
        add(ballMaxSizeLabel,"align right, wrap");
        add(new JLabel("Opponent Pixel Minimum"), "wrap");
        add(opponentSizeSlider, "grow,wrap");
        add(robotMinSizeLabel, "split 2, pushx, growx");
        add(robotMaxSizeLabel,"align right, wrap");
        
        ballSamplingPanel.addColourRangeListener(this);
        teamSamplingPanel.addColourRangeListener(this);
        greenSamplingPanel.addColourRangeListener(this);
        
        add(setRobotDimensionButton, "wrap");
        add(robotDimensionField, "wrap, w 50");

        add(autoRangeCheckBox, "split 4");
        add(contourCheckBox);
        add(newYellowVisionCheckBox);
        add(blackMaskBox);
        add(newNewYellowVisionCheckBox, "wrap");

        add(new JLabel("Robots not present: (eg. 1,4,5)"), "split 3");
        add(robotNotPresentField);
        add(robotNotPresentSaveButton);

        robotSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                robotMinSizeLabel.setText(robotSizeSlider.getLowValue() + "");
                robotMaxSizeLabel.setText(robotSizeSlider.getHighValue() + "");
            }

        });

        ballSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ballMinSizeLabel.setText(ballSizeSlider.getLowValue() + "");
                ballMaxSizeLabel.setText(ballSizeSlider.getHighValue() + "");
            }
        });

        greenSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                greenMinSizeLabel.setText(greenSizeSlider.getLowValue() + "");
                greenMaxSizeLabel.setText(greenSizeSlider.getHighValue() + "");
            }
        });

        setRobotDimensionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Please click centre of robot, then click center of a green quadrant.");
                isGettingRobotDimension = true;
            }
        });
        
        autoRangeCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isAutoRange = !isAutoRange;
			}    	
        });
        
        contourCheckBox.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent arg0) {
				isContour = !isContour;
			}

        });

        blackMaskBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                isGroundMask = !isGroundMask;
            }
        });

        robotNotPresentSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                robotNotPresentUpdated = true;
            }
        });
        
    }

    public boolean isGroundMask() { return isGroundMask; }

    public boolean isNewYellowVision() {
        return newYellowVisionCheckBox.isSelected();
    }

    public boolean isNewNewYellowVision() { return  newNewYellowVisionCheckBox.isSelected();}

    public boolean isContourActive() {
    	return isContour;
    }

    public int[] getRobotsNotSeen() {
        int[] robotNotSeen = new int[]{0,0,0,0,0}; //0 means it is seen, 1 means not seen
        try {
            String[] array = robotNotPresentField.getText().split(",");
            for (int i = 0; i < array.length; i++) {
                robotNotSeen[Integer.parseInt(array[i]) - 1] = 1;
            }
            return robotNotSeen;
        } catch (Exception e) {
            System.out.println("number format exception on the robots not seen");
            return new int[]{0,0,0,0,0};
        }
    }

    protected void displayCircleOnIcon(MouseEvent e) {
    	BufferedImage copy = new BufferedImage(originalImage.getWidth(),
				originalImage.getHeight(),
				originalImage.getType());
		
		Graphics g = copy.getGraphics();
	    g.drawImage(originalImage, 0, 0, null);
	    g.drawOval(e.getX()-selectRadius, e.getY()-selectRadius, 2*selectRadius, 2*selectRadius);
	    g.dispose();
		
		zoomLabel.setIcon(new ImageIcon(copy));
	}

	public void setRobotDimension(int xPos, int yPos) {
        if (clickNumber == 1) { //first click is middle of robot
            middleOfRobot = new Point(xPos, yPos);
            clickNumber = 2;
        } else if (clickNumber == 2) {
            clickNumber = 1;
            int distance = (int) Math.sqrt(squared(xPos - middleOfRobot.x) + squared(yPos - middleOfRobot.y));
            System.out.println("dimension: " + distance + "px");
            robotDimensionField.setText(distance + "");
            isGettingRobotDimension = false;
        }
    }

    public int getRobotDimension() {
        return Integer.parseInt(robotDimensionField.getText());
    }

    //getter
    public int getRobotSizeMinimum() {
        return robotSizeSlider.getLowValue();
    }

    public int getBallSizeMinimum() {
        return ballSizeSlider.getLowValue();
    }

    public int getGreenSizeMinimum() {
        return greenSizeSlider.getLowValue();
    }
    
    public int getRobotSizeMaximum() {
        return robotSizeSlider.getHighValue();
    }

    public int getBallSizeMaximum() {
        return ballSizeSlider.getHighValue();
    }

    public int getGreenSizeMaximum() {
        return greenSizeSlider.getHighValue();
    }

    public int getOpponentSizeMinimum() { return opponentSizeSlider.getLowValue(); }

    public int getOpponentSizeMaximum() { return opponentSizeSlider.getHighValue();}

    //setter
    public void setRobotSizeMinimum(int value) {
        robotSizeSlider.setLowValue(value);
    }

    public void setGreenSizeMinimum(int value) {
        greenSizeSlider.setLowValue(value);
    }

    public void setBallSizeMinimum(int value) {
        ballSizeSlider.setLowValue(value);
    }
    
    public void setRobotSizeMaximum(int value) {
        robotSizeSlider.setHighValue(value);
    }

    public void setGreenSizeMaximum(int value) {
        greenSizeSlider.setHighValue(value);
    }

    public void setBallSizeMaximum(int value) {
        ballSizeSlider.setHighValue(value);
    }

    public void setOpponentSizeMinimum(int value) { opponentSizeSlider.setLowValue(value);}

    public void setOpponentSizeMaximum(int value) { opponentSizeSlider.setHighValue(value);}

    public void takeSample(double xPos, double yPos) {
        for (SamplingPanel sp : samplingPanels) {
            if (sp.isSampling) {
            	/*
            	ImageIcon icon = (ImageIcon)zoomLabel.getIcon();
            	BufferedImage image = (BufferedImage)icon.getImage();
                sp.takeSample(image, xPos, yPos);
                */
            	
            	for (int x=(int) (xPos-selectRadius); x<xPos+selectRadius; x++) {
            		
            		for (int y=(int) (yPos-selectRadius); y<yPos+selectRadius; y++) {
            			if ( Math.pow((x-xPos),2) + Math.pow((y-yPos),2) <= Math.pow(selectRadius-1, 2)){
            				ImageIcon icon = (ImageIcon)zoomLabel.getIcon();
                        	BufferedImage image = (BufferedImage)icon.getImage();
                            sp.takeSample(image,  x, y);
            			}
                	}
            	}
            	if (isAutoRange) {
            		sp.setRange();
            	}
            }
        }
    }

    public boolean getIsSampling() {
        for (SamplingPanel sp : samplingPanels) {
            if (sp.isSampling) {
                return true;
            }
        }
        return false;
    }
    
    public void setZoomLabelIcon(BufferedImage image) {
    	BufferedImage scaled =  api.vision.Image.resize(image, zoomLabel.getWidth(), zoomLabel.getHeight());
    	originalImage = scaled;
    	zoomLabel.setIcon(new ImageIcon(scaled));
    }
    
    public boolean getIsGettingRobotDimension() {
        return isGettingRobotDimension;
    }


	@Override
	public void hRangeChanged(int max, int min, SamplingPanel panel) {
		byte temp = 0;
		if (panel.equals(this.teamSamplingPanel)) {
			temp = (1 << LookupTable.TEAM_BIT_POS);
		}
		else if (panel.equals(this.greenSamplingPanel)) {
			temp = (1 << LookupTable.GREEN_BIT_POS);
		}
		else if (panel.equals(this.ballSamplingPanel)) {
			temp = (1 << LookupTable.BALL_BIT_POS);
		}  
		
	}

	@Override
	public void sRangeChanged(int max, int min, SamplingPanel panel) {
		byte temp = 0;
		if (panel.equals(this.teamSamplingPanel)) {
			temp = (1 << LookupTable.TEAM_BIT_POS);
		}
		else if (panel.equals(this.greenSamplingPanel)) {
			temp = (1 << LookupTable.GREEN_BIT_POS);
		}
		else if (panel.equals(this.ballSamplingPanel)) {
			temp = (1 << LookupTable.BALL_BIT_POS);
		}
		
	}

	@Override
	public void vRangeChanged(int max, int min, SamplingPanel panel) {
		byte temp = 0;
		if (panel.equals(this.teamSamplingPanel)) {
			temp = (1 << LookupTable.TEAM_BIT_POS);
		}
		else if (panel.equals(this.greenSamplingPanel)) {
			temp = (1 << LookupTable.GREEN_BIT_POS);
		}
		else if (panel.equals(this.ballSamplingPanel)) {
			temp = (1 << LookupTable.BALL_BIT_POS);
		}
		
	}
    protected double squared (double x) {
        return x * x;
    }

	@Override
	public void imageUpdated(Mat image) {
	}
	
	public void setWcPanel(WebcamDisplayPanel panel) {
		wcPanel = panel;
	}


    public boolean isRobotNotPresentUpdated() {
        if (robotNotPresentUpdated) {
            robotNotPresentUpdated = false;
            return true;
        }
        return false;
    }
}

