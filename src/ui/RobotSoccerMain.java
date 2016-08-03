package ui;

import api.data.*;
import api.listeners.WebcamDisplayPanelListener;
import api.robots.Robots;
import com.alee.extended.layout.WrapFlowLayout;
import com.alee.laf.WebLookAndFeel;
import api.communication.SerialPortCommunicator;
import jssc.SerialPortList;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Mat;
import api.save.VisionSettingFile;
import api.workers.VisionWorker;
import api.robots.Ball;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import api.data.Point;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class RobotSoccerMain extends JFrame implements WebcamDisplayPanelListener {

	private JButton connectionButton;
    private Field field;
	private RobotInfoPanel[] robotInfoPanels;
	private SerialPortCommunicator serialCom;
	final private Robots bots;
	private VisionPanel visionPanel;
	private VisionSettingFile visionSetting;
	private VisionWorker visionWorker;
	private final static String[] CONNECTION = {"Connect", "Disconnect"};


	public RobotSoccerMain() throws MalformedURLException {
		// Auto wrap after 12 columns.
		// https://www.youtube.com/watch?v=U6xJfP7-HCc
		// Layout constraint, column constraint
		super("BLAZE Robot Soccer");
		setLayout(new BorderLayout());
		// Set default close operation.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel(new BorderLayout());
		// Toolbar
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setLayout(new MigLayout("ins 0, top"));

		//create serial port communicator;
		serialCom = new SerialPortCommunicator();
		bots = new Robots(5);
		Ball ball = new Ball(new Point(0, 0));
		field = new Field(bots, ball, this);

		// get the port names.
		String[] portNames = SerialPortList.getPortNames();
		final JComboBox<String> portNamesComboBox = new JComboBox<String>(portNames);
		final JToggleButton testRotateButton = new JToggleButton("Rotate");
		final JToggleButton testBackwardButton = new JToggleButton("Backward");
		final JToggleButton testForwardButton = new JToggleButton("Forward");
		final List<JToggleButton> toggleButtonList = new ArrayList<JToggleButton>();
		toggleButtonList.add(testForwardButton);
		toggleButtonList.add(testRotateButton);
		toggleButtonList.add(testBackwardButton);

		//open the port and selecting COM3 port if available;
		for (int i =0; i<portNames.length; i++) {
			if (portNames[i].equals("COM3")) {
				portNamesComboBox.setSelectedIndex(i);
			}

			if (portNames[i].equals("COM4")) {
				portNamesComboBox.setSelectedIndex(i);
			}
		}

		serialCom.openPort((String) portNamesComboBox.getSelectedItem());

		portNamesComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serialCom.closePort();
				serialCom.openPort((String) portNamesComboBox.getSelectedItem());
			}

		});

		testForwardButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

			}
		});

		testBackwardButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

			}
		});

		testRotateButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

			}
		});

		//creating panel holding robot informations
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout());
		robotInfoPanels = new RobotInfoPanel[5];

		for (int i = 0; i<5; i++) {
			robotInfoPanels[i] = new RobotInfoPanel(bots, i);
			infoPanel.add(robotInfoPanels[i]);
		}

		DrawAreaGlassPanel glassPanel = new DrawAreaGlassPanel(field);
		glassPanel.setVisible(false);
		field.add(glassPanel);
		field.addComponentListener(glassPanel);

		// Create webcam component panel.
		JPanel webcamComponentPanel = new JPanel(new MigLayout());
		JLabel webcamLabel = new JLabel("Webcam");
		webcamLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		// Create the components.
		connectionButton = new JButton(CONNECTION[0]);

		// Add components into panel.
		webcamComponentPanel.add(webcamLabel, "span, wrap");
		webcamComponentPanel.add(connectionButton, "span, align right");
		webcamComponentPanel.setOpaque(false);

        WebcamDisplayPanel webcamDisplayPanel = new WebcamDisplayPanel();
		webcamDisplayPanel.setPreferredSize(new Dimension(640, 480));
		webcamDisplayPanel.setSize(new Dimension(640, 480));
		ColourPanel colourPanel = new ColourPanel();
		visionPanel = new VisionPanel();

		final JPanel optionContainer = new JPanel(new BorderLayout());
		JPanel robotViewPanel = new JPanel(new BorderLayout());
		robotViewPanel.add(infoPanel, BorderLayout.NORTH);
		WrapFlowLayout flow = new WrapFlowLayout(false, 10, 10);
		flow.setHalign(SwingConstants.CENTER);
		JPanel robotScreenPanel = new JPanel(flow);
        robotScreenPanel.add(field);
        robotScreenPanel.add(webcamDisplayPanel);
		robotViewPanel.add(robotScreenPanel, BorderLayout.CENTER);

        JScrollPane robotViewScrollPane = new JScrollPane(robotViewPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		final String[] options = {
			"Colour",
			"Vision"
		};
		final JList<String> optionList = new JList<String>(options);
		optionList.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;
			boolean gestureStarted = false;
			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (!gestureStarted) {
					if (isSelectedIndex(index0)) {
						super.removeSelectionInterval(index0, index1);
					} else {
						super.setSelectionInterval(index0, index1);
					}
				}
				gestureStarted = true;
			}
			@Override
			public void setValueIsAdjusting(boolean isAdjusting) {
				if (isAdjusting == false) {
					gestureStarted = false;
				}
			}
		});

		optionList.setCellRenderer(new OptionRenderer());
		//optionList.setBackground(new Color(121, 121, 121));

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionContainer, robotViewScrollPane) {
			@Override
			public void setDividerLocation(int location) {
				if (optionList.getSelectedValue() == null) {
					super.setDividerLocation(location);
					super.setDividerSize(0);
					return;
				}
				super.setDividerSize(6);
				super.setDividerLocation(location);
			}
		};
		splitPane.setResizeWeight(0.0);
        splitPane.setDividerLocation(0);

		final JPanel optionCards = new JPanel(new CardLayout());
        optionCards.add(colourPanel, options[0]);
        optionCards.add(visionPanel, options[1]);

		optionList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (optionList.getSelectedValue() == null) {
						if (optionCards.getParent() != null) {
							optionContainer.remove(optionCards);
							optionContainer.revalidate();
							optionContainer.repaint();
							splitPane.setDividerLocation(0);
						}
						return;
					} else if (optionList.getSelectedValue().equals(options[0])) {
						CardLayout c = (CardLayout)optionCards.getLayout();
						c.show(optionCards, options[0]);
					} else if (optionList.getSelectedValue().equals(options[1])) {
						CardLayout c = (CardLayout)optionCards.getLayout();
						c.show(optionCards, options[1]);
					}

//					if (optionList.getSelectedValue().equals(options[1])) {
//						webcamController.getWebcamDisplayPanel().setZoomCursor();
//					} else {
//						webcamController.getWebcamDisplayPanel().setDefaultCursor();
//					}

					optionContainer.add(optionCards, BorderLayout.CENTER);
					optionContainer.revalidate();
					optionContainer.repaint();
					splitPane.setDividerLocation(500);
				}

			}
		});

		contentPane.add(optionList, BorderLayout.WEST);
		contentPane.add(splitPane, BorderLayout.CENTER);

		// set toolbar
		toolbar.add(webcamComponentPanel);
		add(toolbar, BorderLayout.PAGE_START);
		add(contentPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(1290, 900));

    }

	@Override
	public void imageUpdated(Mat image) {
	}
	
	/**
	 * Create the GUI and show it. As with all GUI code, this must run
	 * on the event-dispatching thread.
	 * @throws MalformedURLException 
	 */
	private static void createAndShowGUI() throws MalformedURLException {
		//Create and set up the content pane.
		JFrame frame = new RobotSoccerMain();
		frame.setMinimumSize(new Dimension(1290, 1000));

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		String path = System.getProperty("user.dir");
	//	System.load( path + "\\native\\opencv_java2411.dll" );
		System.load( path + "\\native\\opencv_java310.dll" );

		SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(WebLookAndFeel.class.getCanonicalName());
                createAndShowGUI();
            } catch (MalformedURLException | ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
	}
}