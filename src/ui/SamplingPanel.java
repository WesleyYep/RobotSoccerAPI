package ui;

import net.miginfocom.swing.MigLayout;
import api.vision.ColorSpace;
import api.vision.ColourRangeListener;
import api.vision.LookupTable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Users sample the webcam image and adds data to ColourSlider</p>
 * <p>{@link ui.ColourSlider}</p>
 * Created by Wesley on 3/02/2015.
 */
public class SamplingPanel extends JPanel implements ActionListener {
    private ColourSlider HSlider, SSlider, VSlider;
    private JButton sampleButton, detectButton, setValueButton, clearAllButton;
    private JLabel lowHLabel, highHLabel, lowSLabel, highSLabel, lowVLabel, highVLabel, hueLabel, saturationLabel, valueLabel;
    private JPanel optionPanePanel;
    private JTextField lowValueTextField, highValueTextField;
    private JComboBox<String> HSVCombo;
    private List<ColourRangeListener> colourRangeListeners;
    private int hMax = -1, hMin = -1, sMax = -1, sMin = -1, vMax = -1, vMin = -1;
    private byte mask = 0b00000000;
    public boolean isSampling = false;
    private static final String[] DETECTSTRING = {"Detect", "Stop"};
    
    public SamplingPanel() {
        this.setLayout(new MigLayout());
        colourRangeListeners = new ArrayList<ColourRangeListener>();
        lowHLabel = new JLabel();
        highHLabel = new JLabel();
        lowSLabel = new JLabel();
        highSLabel = new JLabel();
        lowVLabel = new JLabel();
        highVLabel = new JLabel();
        hueLabel = new JLabel("Hue");
        saturationLabel = new JLabel("Saturation");
        valueLabel = new JLabel("Value");
        
        int minorTickSpacing = 5, majorTickSpacing = 25;
        
        HSlider = new ColourSlider(0, 255);
        HSlider.setMinorTickSpacing(minorTickSpacing);
        HSlider.setMajorTickSpacing(majorTickSpacing);
        HSlider.setLabelTable(HSlider.createStandardLabels(majorTickSpacing));
        HSlider.setPaintTicks(true);
        HSlider.setPaintLabels(true);
        
        SSlider = new ColourSlider(0, 255);
        SSlider.setMinorTickSpacing(minorTickSpacing);
        SSlider.setMajorTickSpacing(majorTickSpacing);
        SSlider.setLabelTable(SSlider.createStandardLabels(majorTickSpacing));
        SSlider.setPaintTicks(true);
        SSlider.setPaintLabels(true);
        
        VSlider = new ColourSlider(0, 255);
        VSlider.setMinorTickSpacing(minorTickSpacing);
        VSlider.setMajorTickSpacing(majorTickSpacing);
        VSlider.setLabelTable(VSlider.createStandardLabels(majorTickSpacing));
        VSlider.setPaintTicks(true);
        VSlider.setPaintLabels(true);
        
        // Add change listener. Update labels.
        HSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowHLabel.setText(HSlider.getLowValue() + "");
				highHLabel.setText(HSlider.getHighValue() + "");
				
				for (ColourRangeListener c : colourRangeListeners) {
					//c.hRangeChanged(HSlider.getHighValue(), HSlider.getLowValue(),SamplingPanel.this);
				}
			}
        	
        });
        
        SSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowSLabel.setText(SSlider.getLowValue() + "");
				highSLabel.setText(SSlider.getHighValue() + "");
				
				for (ColourRangeListener c : colourRangeListeners) {
					//c.sRangeChanged(SSlider.getHighValue(), SSlider.getLowValue(),SamplingPanel.this);
				}
			}
        	
        });
        
        VSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowVLabel.setText(VSlider.getLowValue() + "");
				highVLabel.setText(VSlider.getHighValue() + "");
				
				for (ColourRangeListener c : colourRangeListeners) {
					//c.vRangeChanged(VSlider.getHighValue(), VSlider.getLowValue(),SamplingPanel.this);
				}
			}
        	
        });
        
        sampleButton = new JButton("Start Sample");
        detectButton = new JButton(DETECTSTRING[0]);
        setValueButton = new JButton("Set Value");
        clearAllButton = new JButton("Clear All");
        
        optionPanePanel = new JPanel(new MigLayout());
        
        // Create textfield for optionPanePanel and set the documentfilter.
        lowValueTextField = new JTextField();
        ((AbstractDocument)lowValueTextField.getDocument()).setDocumentFilter(new YUVFilter());
        highValueTextField = new JTextField();
        ((AbstractDocument)highValueTextField.getDocument()).setDocumentFilter(new YUVFilter());
        
		HSVCombo = new JComboBox<String>();
		
		HSVCombo.addItem("H");
		HSVCombo.addItem("S");
		HSVCombo.addItem("V");
        
		optionPanePanel.add(HSVCombo, "span, push, grow, wrap");
		optionPanePanel.add(new JLabel("Choose values 0-255"), "span, push, grow, wrap");
		optionPanePanel.add(new JLabel("Set low value"), "split 2, span");
        optionPanePanel.add(lowValueTextField, "push, grow, wrap");
        optionPanePanel.add(new JLabel("Set high value"), "split 2, span");
        optionPanePanel.add(highValueTextField, "push, grow");
        
        add(hueLabel, "span, wrap");
        add(lowHLabel, "width 30:30:30, split 3");
        add(HSlider, "width 400:400:400");
        add(highHLabel, "width 30:30:30, wrap 15");
        add(saturationLabel, "span, wrap");
        add(lowSLabel, "width 30:30:30, split 3");
        add(SSlider, "width 400:400:400");
        add(highSLabel, "width 30:30:30, wrap 15");
        add(valueLabel, "span, wrap");
        add(lowVLabel, "width 30:30:30, split 3");
        add(VSlider, "width 400:400:400");
        add(highVLabel, "width 30:30:30, wrap 15");
        add(clearAllButton, "span, split 4, align right");
        add(sampleButton);
        add(detectButton, "width 75:75:75");
        add(setValueButton, "wrap");

        sampleButton.addActionListener(this);
        detectButton.addActionListener(this);
        setValueButton.addActionListener(this);
        clearAllButton.addActionListener(this);
    }

    public void takeSample(BufferedImage image, double xPos, double yPos) {
        Color color = new Color(image.getRGB((int)xPos, (int)yPos));
        float[] hsv = ColorSpace.RGBToHSV(color.getRed(), color.getGreen(), color.getBlue());
       // System.out.println("H: " + hsv[0] + "S: " + hsv[1] + "V: " + hsv[2]);
        HSlider.addToData((int)hsv[0]);
        SSlider.addToData((int)hsv[1]);
        VSlider.addToData((int)hsv[2]);
        
        if (hMax == -1 || hsv[0] > hMax) {
        	hMax = (int) hsv[0];
        }
        
        if (hMin == -1 || hsv[0] < hMin) {
        	hMin = (int) hsv[0];
        }
        
        if (sMax == -1 || hsv[1] > sMax) {
        	sMax = (int) hsv[1];
        }
        
        if (sMin == -1 || hsv[1] < sMin) {
        	sMin = (int) hsv[1];
        }
        
        if (vMax == -1 || hsv[2] > vMax) {
        	vMax = (int) hsv[2];
        }
        
        if (vMin == -1 || hsv[2] < vMin) {
        	vMin = (int) hsv[2];
        }
    }
    
    public void setRange() {
//    	System.out.println("");
//    	System.out.println(yMin + " " + yMax);
//    	System.out.println(uMin + " " + uMax);
//    	System.out.println(vMin + " " + vMax);
    	
    	HSlider.setLowValue(hMin);
    	HSlider.setHighValue(hMax);
    	
    	SSlider.setLowValue(sMin);
    	SSlider.setHighValue(sMax);
    	
    	VSlider.setLowValue(vMin);
    	VSlider.setHighValue(vMax);
    	 HSlider.repaint();
         SSlider.repaint();
         VSlider.repaint(); 
    }
    
    public void addColourRangeListener(ColourRangeListener c) {
    	colourRangeListeners.add(c);
    }

    public int getLowerBoundForH() {
        return HSlider.getLowValue();
    }

    public int getUpperBoundForH() {
        return HSlider.getHighValue();
    }

    public int getLowerBoundForS() {
        return SSlider.getLowValue();
    }

    public int getUpperBoundForS() {
        return SSlider.getHighValue();
    }

    public int getLowerBoundForV() {
        return VSlider.getLowValue();
    }

    public int getUpperBoundForV() {
        return VSlider.getHighValue();
    }
    
    public void setUpperBoundForH(int i) {
    	HSlider.setHighValue(i);
    }
    
    public void setLowerBoundForH(int i) {
    	HSlider.setLowValue(i);
    }
    
    public void setUpperBoundForS(int i) {
    	SSlider.setHighValue(i);
    }
    
    public void setLowerBoundForS(int i) {
    	SSlider.setLowValue(i);
    }
    
    public void setUpperBoundForV(int i) {
    	VSlider.setHighValue(i);
    }
    
    public void setLowerBoundForV(int i) {
    	VSlider.setLowValue(i);
    }
    
    public byte getMask() {
    	return mask;
    }
    
    public void setMask(byte mask) {
    	this.mask = mask;
    }

    /**
     * <p>Changes the detect button to default value and stops sampling</p>
     */
    
    public void resetButton() {
    	detectButton.setText(DETECTSTRING[0]);
    	sampleButton.setText("Start Sample");
    	isSampling = false;
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sampleButton) {
            if (!isSampling) {
                sampleButton.setText("Stop Sample");
                isSampling = true;                                
            } else {
                sampleButton.setText("Start Sample");
                isSampling = false;

                for (int h=0; h<256; h++) {
                	for (int s = 0; s<256; s++) {
                		for(int v = 0; v<256; v++) {

                			if (HSlider.getLowValue() <= h && h <= HSlider.getHighValue() && SSlider.getLowValue() <= s
                                    && s <= SSlider.getHighValue() && VSlider.getLowValue() <= v && v <= VSlider.getHighValue()) {
                				    LookupTable.setData(mask, h, s, v, true);
                			} else {
                                byte unmask = (byte) ~mask;
                                LookupTable.clearData(unmask,h,s,v);
                            }
                		}
                	}
                }

            }
		} else if (e.getSource() == detectButton) {
            if (detectButton.getText().equals(DETECTSTRING[0])) {
                detectButton.setText(DETECTSTRING[1]);
            } else {
                detectButton.setText(DETECTSTRING[0]);
            }
		} else if (e.getSource() == setValueButton) {
			int selection = JOptionPane.showConfirmDialog(
					null,
					optionPanePanel,
					"Set value for slider",
					JOptionPane.OK_CANCEL_OPTION
					);
			
			if (selection == JOptionPane.OK_OPTION) {
				try {
					String HSVSelection = (String)HSVCombo.getSelectedItem();
					int lowValue = Integer.parseInt(lowValueTextField.getText());
					int highValue = Integer.parseInt(highValueTextField.getText());
					
					if (lowValue > highValue) {
						JOptionPane.showMessageDialog(null, "Cannot have low value greater than high value");
					} else {
						
						switch(HSVSelection) {
						case "H":
							
							HSlider.setLowValue(lowValue);
							HSlider.setHighValue(highValue);
							break;
						case "S":
							
							SSlider.setLowValue(lowValue);
							SSlider.setHighValue(highValue);
							break;
						case "V":
							
							VSlider.setLowValue(lowValue);
							VSlider.setHighValue(highValue);
							break;
						}
						
					}
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "You did not insert a number.");
				}
			}
			
		} else if (e.getSource() == clearAllButton) {
			HSlider.clearData();
			SSlider.clearData();
			VSlider.clearData();
			
			hMax = -1;
			hMin = -1;
			
			sMax = -1;
			sMin = -1;
			
			vMax = -1;
			vMin = -1;
		}
	}
	
	
	public void deactiviate() {
		if (isSampling) {
			sampleButton.setText("Start Sample");
            isSampling = false;
		}
		
		if (!detectButton.getText().equals(DETECTSTRING[0])) {
			detectButton.setText(DETECTSTRING[0]);
        }
	}
	
	/**
	 * <p>Filters the text inserted into textfield for YUV values.</p>
	 * @author Chang Kon, Wesley, John
	 *
	 */
	
	private class YUVFilter extends DocumentFilter {

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			try {
				// Try to change text to int. Throw exception is not possible.
				Integer.parseInt(text);
				
				// If there are 3 digits, check the value before adding to textfield.
				if (offset == 2) {
					int value = Integer.parseInt(fb.getDocument().getText(0, offset) + text);
					
					if (value > 255) {
						return;
					}
				}
				
				// As the values are only from 0-255, anything greater than 3 digits will be above 255 and is invalid.
				if (offset > 2) {
					return;
				}
				
			} catch (NumberFormatException e) {
				return;
			}
			
			super.replace(fb, offset, length, text, attrs);
		}
		
	}
	
}
