package ui;

import com.jidesoft.swing.RangeSlider;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 3/02/2015.
 */
public class ColourSlider extends RangeSlider {
	private List<Integer> data = new ArrayList<Integer>();

	public ColourSlider() {
		super();
	}

	public ColourSlider(int orientation) {
		super(orientation);
	}

	public ColourSlider(int min, int max) {
		super(min, max);
	}

	public ColourSlider(int min, int max, int low, int high) {
		super(min, max, low, high);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Mark data points.
		g2.setPaint(Color.red);
		for(int i = 0; i < data.size(); i++) {
			double x = data.get(i);
			// x minus the radius of the circle.
			g2.fill(new Ellipse2D.Double(x - 2, 0, 4, 4));
		}
	}

	/**
	 * <p>Adds the HSV value to the list</p> 
	 * @param HSV value. 0-255
	 */
	
	public void addToData(int value) {
	//	double difference = getMaximum() - getMinimum();
		value = (int)(value/255.0 * (getWidth()-20) + 10);
        //System.out.println(value);
        data.add(value);
		repaint();
	}

	/**
	 * <p>Removes all colour sampling data</p>
	 */
	
	public void clearData() {
		data.clear();
		repaint();
	}
	
}
