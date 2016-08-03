package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardDialog extends JDialog {
	//	private JPanel glassPanel;
	private BufferedImage boardImage;
	private JLabel picLabel;
	private JPanel panel;
	private BoardAreaGlassPanel glassPanel;

	public BoardDialog(JFrame jFrame) {
		super(jFrame, false);
		picLabel = new JLabel();
		glassPanel = new BoardAreaGlassPanel();
		this.setResizable(false);
		panel = new JPanel();
		panel.setLayout(null);
		panel.add(picLabel);
		panel.add(glassPanel);
		glassPanel.setVisible(true);
		panel.setComponentZOrder(glassPanel,0);
		this.add(panel);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	/**
	 * Set the new background image for the board area dialog, resize the dialog if the image changes
	 * @param image
	 */
	public void setBoardImage(BufferedImage image) {
		
		boardImage = image;

		picLabel.setIcon(new ImageIcon(image));

		if (picLabel.getWidth() != image.getWidth() || picLabel.getHeight() != image.getHeight()) {
			picLabel.setSize(image.getWidth(),image.getHeight());
			picLabel.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
			panel.setSize(image.getWidth(),image.getHeight());
			panel.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
	
			glassPanel.setSize(image.getWidth(),image.getHeight());
			glassPanel.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
			glassPanel.setOpaque(false);
	
			glassPanel.setBounds(0,0,image.getWidth(),image.getHeight());
			glassPanel.repaint();
			this.setSize(image.getWidth()+50,image.getHeight()+50);
			this.pack();
			this.validate();
			this.repaint();
		}
		
	}
}
