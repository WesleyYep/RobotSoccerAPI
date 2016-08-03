package ui;

import api.listeners.RobotListener;
import api.robots.Robots;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import api.robots.Robot;

public class RobotInfoPanel extends JPanel implements RobotListener {
	private Robots robots;
	private JLabel title;
	private JLabel xCoordinate;
	private JLabel yCoordinate;
	private JLabel orientation;
	private int index;

	public RobotInfoPanel(Robots robots, int i) {
		this.robots = robots;
		xCoordinate = new JLabel("x = 0");
		yCoordinate = new JLabel("y = 0");
		orientation = new JLabel("theta = 0");
		title = new JLabel("Robot " + (i+1));
		this.index = i;

		this.setLayout(new MigLayout(
				"wrap 1, ins 0", // layout
				"[min:100:max]", // column
				"" //row
		));
		this.add(title, "span");
		this.add(xCoordinate, "span");
		this.add(yCoordinate, "span");
		this.add(orientation, "span");

		// Create border. Initially black.
		Border border = BorderFactory.createLineBorder(Color.black);
		Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		this.setBorder(BorderFactory.createCompoundBorder(border, padding));

	}

	@Override
	public void positionChanged() {
		double x = robots.getPosition(index).x;
		double y = robots.getPosition(index).y;
		double o = robots.getTheta(index);

		xCoordinate.setText("x= " + (int)x);
		yCoordinate.setText("y= " + (int)y);
		// Show only up to two decimal places.
		orientation.setText("theta= " + String.format("%.2f", o));

		this.repaint();
	}

}
