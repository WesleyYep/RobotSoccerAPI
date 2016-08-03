package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DrawAreaGlassPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener{

	private Point startPoint;
	private Point endPoint;
	private boolean isMouseDrag;
	private int mouseClicked;
	private Field field;

	public DrawAreaGlassPanel(Field panel) {
		this.setOpaque(false);
		this.setPreferredSize(panel.getPreferredSize());
		this.setBounds(0,0,panel.getPreferredSize().width, getPreferredSize().height);
		field = panel;
		isMouseDrag = false;
		addMouseListener(this);
		addMouseMotionListener(this);
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (isMouseDrag) {
			// drawRect does not take negative values hence values need to be calculated so it doesn't fill the rectangle.
			// Rectangle co-ordinates.
			int x, y, w, h;

			x = Math.min(startPoint.x, endPoint.x);
			y = Math.min(startPoint.y, endPoint.y);

			w = Math.abs(endPoint.x - startPoint.x);
			h = Math.abs(endPoint.y - startPoint.y);

			g.setColor(Color.RED);
			g.drawRect(x, y, w, h);
		}

	}


	@Override
	public void mouseDragged(MouseEvent e) {
		isMouseDrag = true;
		endPoint = e.getPoint();
		if (mouseClicked == MouseEvent.BUTTON1) {
			field.repaint();
		}
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));		
	}


	@Override
	public void mouseClicked(MouseEvent e) {

	}


	@Override
	public void mouseEntered(MouseEvent e) {

	}


	@Override
	public void mouseExited(MouseEvent e) {

	}


	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
		mouseClicked = e.getButton();
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		endPoint = e.getPoint();
		isMouseDrag = false;

		if (e.getButton() == MouseEvent.BUTTON1) {
			Rectangle area = new Rectangle(startPoint);
			area.add(endPoint);		
		}
		this.setVisible(false);
	}


	@Override
	public void componentHidden(ComponentEvent arg0) {}


	@Override
	public void componentMoved(ComponentEvent arg0) {}


	@Override
	public void componentResized(ComponentEvent arg0) {
		this.setSize(arg0.getComponent().getWidth(), arg0.getComponent().getHeight());
	}


	@Override
	public void componentShown(ComponentEvent arg0) {}
}
