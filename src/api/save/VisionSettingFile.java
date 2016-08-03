package api.save;

import api.data.BoardProperties;
import api.data.Point;
import api.vision.LookupTable;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import ui.ColourPanel;
import ui.SamplingPanel;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.NoSuchElementException;

public class VisionSettingFile {

	private ColourPanel colourPanel;
	private BoardProperties boardProperties;

	public VisionSettingFile(ColourPanel colourPanel) {
		this.colourPanel = colourPanel;
	}

	/**
	 * <p>Saves the vision setting into a xml file</p>
	 */
	
	public void saveVisionSetting() {
		JFileChooser fileChooser;
		String path;
		//read in the last save directory
		if ((path = ConfigFile.getInstance().getLastSaveDirectory()) == null) {
			fileChooser = new JFileChooser();
		} else {
			fileChooser = new JFileChooser(path);
		}
		
		// Removes the accept all filter.
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		// Adds the save filter.
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text/xml", "xml"));
		
		fileChooser.showSaveDialog(null);

		if (fileChooser.getSelectedFile() == null) {
			return;
		}

		String extensionType = fileChooser.getFileFilter().getDescription();
		String fileName = fileChooser.getSelectedFile().getAbsolutePath();
		
		/*
		 * Check the output filename and adds the correct extension type. If the output filename already has the extension added, it
		 * doesn't append extension again.
		 */
		if (extensionType.contains("text/xml") && !fileName.contains("xml")) {
			fileName = fileName + "." + "xml";
		}

		save(fileName);

	}

	public void save(String fileName) {
		/*
		 * Check the output filename and adds the correct extension type. If the output filename already has the extension added, it
		 * doesn't append extension again.
		 */
		String folderPath = fileName.substring(0, fileName.lastIndexOf("\\"));;
		ConfigFile.getInstance().setLastSaveDirectory(folderPath);

		try {
			XMLConfiguration saveSetting = new XMLConfiguration();
			saveSetting.setFile(new File(fileName));
			saveSetting.save();

			saveSetting.addProperty("topRightX", boardProperties.topRight.x);
			saveSetting.addProperty("topRightY", boardProperties.topRight.y);
			saveSetting.addProperty("topLeftX", boardProperties.topLeft.x);
			saveSetting.addProperty("topLeftY", boardProperties.topLeft.y);
			saveSetting.addProperty("bottomRightX", boardProperties.bottomRight.x);
			saveSetting.addProperty("bottomRightY", boardProperties.bottomRight.y);
			saveSetting.addProperty("bottomLeftX", boardProperties.bottomLeft.x);
			saveSetting.addProperty("bottomLeftY", boardProperties.bottomLeft.y);

            saveSetting.addProperty("leftGoalTopLeftX", boardProperties.leftGoalTopLeft.x);
            saveSetting.addProperty("leftGoalTopLeftY", boardProperties.leftGoalTopLeft.y);
            saveSetting.addProperty("leftGoalTopRightX", boardProperties.leftGoalTopRight.x);
            saveSetting.addProperty("leftGoalTopRightY", boardProperties.leftGoalTopRight.y);
            saveSetting.addProperty("leftGoalBottomLeftX", boardProperties.leftGoalBottomLeft.x);
            saveSetting.addProperty("leftGoalBottomLeftY", boardProperties.leftGoalBottomLeft.y);
            saveSetting.addProperty("leftGoalBottomRightX", boardProperties.leftGoalBottomRight.x);
            saveSetting.addProperty("leftGoalBottomRightY", boardProperties.leftGoalBottomRight.y);

            saveSetting.addProperty("rightGoalTopLeftX", boardProperties.rightGoalTopLeft.x);
            saveSetting.addProperty("rightGoalTopLeftY", boardProperties.rightGoalTopLeft.y);
            saveSetting.addProperty("rightGoalTopRightX", boardProperties.rightGoalTopRight.x);
            saveSetting.addProperty("rightGoalTopRightY", boardProperties.rightGoalTopRight.y);
            saveSetting.addProperty("rightGoalBottomLeftX", boardProperties.rightGoalBottomLeft.x);
            saveSetting.addProperty("rightGoalBottomLeftY", boardProperties.rightGoalBottomLeft.y);
            saveSetting.addProperty("rightGoalBottomRightX", boardProperties.rightGoalBottomRight.x);
            saveSetting.addProperty("rightGoalBottomRightY", boardProperties.rightGoalBottomRight.y);

			SamplingPanel ballSP = colourPanel.ballSamplingPanel;

			saveSetting.addProperty("ballHUpper", ballSP.getUpperBoundForH());
			saveSetting.addProperty("ballHLower", ballSP.getLowerBoundForH());

			saveSetting.addProperty("ballSUpper", ballSP.getUpperBoundForS());
			saveSetting.addProperty("ballSLower", ballSP.getLowerBoundForS());

			saveSetting.addProperty("ballVUpper", ballSP.getUpperBoundForV());
			saveSetting.addProperty("ballVLower", ballSP.getLowerBoundForV());


			SamplingPanel teamSp = colourPanel.teamSamplingPanel;

			saveSetting.addProperty("teamHUpper", teamSp.getUpperBoundForH());
			saveSetting.addProperty("teamHLower", teamSp.getLowerBoundForH());

			saveSetting.addProperty("teamSUpper", teamSp.getUpperBoundForS());
			saveSetting.addProperty("teamSLower", teamSp.getLowerBoundForS());

			saveSetting.addProperty("teamVUpper", teamSp.getUpperBoundForV());
			saveSetting.addProperty("teamVLower", teamSp.getLowerBoundForV());

			SamplingPanel greenSp = colourPanel.greenSamplingPanel;

			saveSetting.addProperty("greenHUpper", greenSp.getUpperBoundForH());
			saveSetting.addProperty("greenHLower", greenSp.getLowerBoundForH());

			saveSetting.addProperty("greenSUpper", greenSp.getUpperBoundForS());
			saveSetting.addProperty("greenSLower", greenSp.getLowerBoundForS());

			saveSetting.addProperty("greenVUpper", greenSp.getUpperBoundForV());
			saveSetting.addProperty("greenVLower", greenSp.getLowerBoundForV());

			SamplingPanel groundSp = colourPanel.groundSamplingPanel;

			saveSetting.addProperty("groundHUpper", groundSp.getUpperBoundForH());
			saveSetting.addProperty("groundHLower", groundSp.getLowerBoundForH());

			saveSetting.addProperty("groundSUpper", groundSp.getUpperBoundForS());
			saveSetting.addProperty("groundSLower", groundSp.getLowerBoundForS());

			saveSetting.addProperty("groundVUpper", groundSp.getUpperBoundForV());
			saveSetting.addProperty("groundVLower", groundSp.getLowerBoundForV());

			SamplingPanel opponentSp = colourPanel.opponentSamplingPanel;

			saveSetting.addProperty("opponentHUpper", opponentSp.getUpperBoundForH());
			saveSetting.addProperty("opponentHLower", opponentSp.getLowerBoundForH());

			saveSetting.addProperty("opponentSUpper", opponentSp.getUpperBoundForS());
			saveSetting.addProperty("opponentSLower", opponentSp.getLowerBoundForS());

			saveSetting.addProperty("opponentVUpper", opponentSp.getUpperBoundForV());
			saveSetting.addProperty("opponentVLower", opponentSp.getLowerBoundForV());

			saveSetting.addProperty("robotMinSize", colourPanel.getRobotSizeMinimum());
			saveSetting.addProperty("greenMinSize", colourPanel.getGreenSizeMinimum());
			saveSetting.addProperty("ballMinSize", colourPanel.getBallSizeMinimum());

			saveSetting.addProperty("robotMaxSize", colourPanel.getRobotSizeMaximum());
			saveSetting.addProperty("greenMaxSize", colourPanel.getGreenSizeMaximum());
			saveSetting.addProperty("ballMaxSize", colourPanel.getBallSizeMaximum());

			saveSetting.save();

			//save last read file
			ConfigPreviousFile.getInstance().setPreviousVisionFile(fileName);

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>Opens the vision setting xml file and loads it into the program</p>
	 */
	
	public void openVisionSetting() {
		JFileChooser fileChooser;
		String path;
		//read in the last open directory
		if ((path = ConfigFile.getInstance().getLastOpenDirectory()) == null) {
			fileChooser = new JFileChooser();
		} else {
			fileChooser = new JFileChooser(path);
		}
		
		// Removes the accept all filter.
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		// Adds the open filter.
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text/xml", "xml"));
		
		fileChooser.showOpenDialog(null);

		if (fileChooser.getSelectedFile() == null) {
			return;
		}

		String fileName = fileChooser.getSelectedFile().getAbsolutePath();

		//creating the folder name and write into configuration
		String folderPath = fileName.substring(0, fileName.lastIndexOf("\\"));;
		ConfigFile.getInstance().setLastOpenDirectory(folderPath);
        open(fileName);
        //save last read file
        ConfigPreviousFile.getInstance().setPreviousVisionFile(fileName);
	}

    public void open(String fileName) {
        try {
            XMLConfiguration openSetting = new XMLConfiguration(fileName);

            boardProperties.topLeft = new Point(openSetting.getDouble("topLeftX"), openSetting.getDouble("topLeftY"));
            boardProperties.topRight = new Point(openSetting.getDouble("topRightX"), openSetting.getDouble("topRightY"));
            boardProperties.bottomLeft = new Point(openSetting.getDouble("bottomLeftX"), openSetting.getDouble("bottomLeftY"));
            boardProperties.bottomRight = new Point(openSetting.getDouble("bottomRightX"), openSetting.getDouble("bottomRightY"));

            try {
                boardProperties.leftGoalTopLeft = new Point(openSetting.getDouble("leftGoalTopLeftX"), openSetting.getDouble("leftGoalTopLeftY"));
                boardProperties.leftGoalTopRight = new Point(openSetting.getDouble("leftGoalTopRightX"), openSetting.getDouble("leftGoalTopRightY"));
                boardProperties.leftGoalBottomLeft = new Point(openSetting.getDouble("leftGoalBottomLeftX"), openSetting.getDouble("leftGoalBottomLeftY"));
                boardProperties.leftGoalBottomRight = new Point(openSetting.getDouble("leftGoalBottomRightX"), openSetting.getDouble("leftGoalBottomRightY"));
                boardProperties.rightGoalTopLeft = new Point(openSetting.getDouble("rightGoalTopLeftX"), openSetting.getDouble("rightGoalTopLeftY"));
                boardProperties.rightGoalTopRight = new Point(openSetting.getDouble("rightGoalTopRightX"), openSetting.getDouble("rightGoalTopRightY"));
                boardProperties.rightGoalBottomLeft = new Point(openSetting.getDouble("rightGoalBottomLeftX"), openSetting.getDouble("rightGoalBottomLeftY"));
                boardProperties.rightGoalBottomRight = new Point(openSetting.getDouble("rightGoalBottomRightX"), openSetting.getDouble("rightGoalBottomRightY"));
            } catch (NoSuchElementException ex) {
                System.out.println("outdated vision setting file, please update board area and save it again");
            }

            SamplingPanel ballSP = colourPanel.ballSamplingPanel;

            ballSP.setLowerBoundForH(openSetting.getInt("ballHLower"));
            ballSP.setLowerBoundForS(openSetting.getInt("ballSLower"));
            ballSP.setLowerBoundForV(openSetting.getInt("ballVLower"));

            ballSP.setUpperBoundForH(openSetting.getInt("ballHUpper"));
            ballSP.setUpperBoundForS(openSetting.getInt("ballSUpper"));
            ballSP.setUpperBoundForV(openSetting.getInt("ballVUpper"));

			for (int h=0; h<256; h++) {
				for (int s = 0; s<256; s++) {
					for(int v = 0; v<256; v++) {

						if (ballSP.getLowerBoundForH() <= h && h <= ballSP.getUpperBoundForH() && ballSP.getLowerBoundForS() <= s
								&& s <= ballSP.getUpperBoundForS() && ballSP.getLowerBoundForV() <= v && v <= ballSP.getUpperBoundForV()) {
							LookupTable.setData(LookupTable.BALL_COLOUR, h, s, v, true);
						} else {
							byte unmask = (byte) ~LookupTable.BALL_COLOUR;
							LookupTable.clearData(unmask,h,s,v);
						}
					}
				}
			}

            SamplingPanel teamSp = colourPanel.teamSamplingPanel;

            teamSp.setLowerBoundForH(openSetting.getInt("teamHLower"));
            teamSp.setLowerBoundForS(openSetting.getInt("teamSLower"));
            teamSp.setLowerBoundForV(openSetting.getInt("teamVLower"));

            teamSp.setUpperBoundForH(openSetting.getInt("teamHUpper"));
            teamSp.setUpperBoundForS(openSetting.getInt("teamSUpper"));
            teamSp.setUpperBoundForV(openSetting.getInt("teamVUpper"));

			for (int h=0; h<256; h++) {
				for (int s = 0; s<256; s++) {
					for(int v = 0; v<256; v++) {

						if (teamSp.getLowerBoundForH() <= h && h <= teamSp.getUpperBoundForH() && teamSp.getLowerBoundForS() <= s
								&& s <= teamSp.getUpperBoundForS() && teamSp.getLowerBoundForV() <= v && v <= teamSp.getUpperBoundForV()) {
							LookupTable.setData(LookupTable.TEAM_COLOUR, h, s, v, true);
						} else {
							byte unmask = (byte) ~LookupTable.TEAM_COLOUR;
							LookupTable.clearData(unmask,h,s,v);
						}
					}
				}
			}

            SamplingPanel greenSp = colourPanel.greenSamplingPanel;

            greenSp.setLowerBoundForH(openSetting.getInt("greenHLower"));
            greenSp.setLowerBoundForS(openSetting.getInt("greenSLower"));
            greenSp.setLowerBoundForV(openSetting.getInt("greenVLower"));

            greenSp.setUpperBoundForH(openSetting.getInt("greenHUpper"));
            greenSp.setUpperBoundForS(openSetting.getInt("greenSUpper"));
            greenSp.setUpperBoundForV(openSetting.getInt("greenVUpper"));

            SamplingPanel groundSp = colourPanel.groundSamplingPanel;

            groundSp.setLowerBoundForH(openSetting.getInt("groundHLower"));
            groundSp.setLowerBoundForS(openSetting.getInt("groundSLower"));
            groundSp.setLowerBoundForV(openSetting.getInt("groundVLower"));

            groundSp.setUpperBoundForH(openSetting.getInt("groundHUpper"));
            groundSp.setUpperBoundForS(openSetting.getInt("groundSUpper"));
            groundSp.setUpperBoundForV(openSetting.getInt("groundVUpper"));

			for (int h=0; h<256; h++) {
				for (int s = 0; s<256; s++) {
					for(int v = 0; v<256; v++) {

						if (groundSp.getLowerBoundForH() <= h && h <= groundSp.getUpperBoundForH() && groundSp.getLowerBoundForS() <= s
								&& s <= groundSp.getUpperBoundForS() && groundSp.getLowerBoundForV() <= v && v <= groundSp.getUpperBoundForV()) {
							LookupTable.setData(LookupTable.GROUND_COLOUR, h, s, v, true);
						} else {
							byte unmask = (byte) ~LookupTable.GROUND_COLOUR;
							LookupTable.clearData(unmask,h,s,v);
						}
					}
				}
			}

            SamplingPanel opponentSp = colourPanel.opponentSamplingPanel;

            opponentSp.setLowerBoundForH(openSetting.getInt("opponentHLower"));
            opponentSp.setLowerBoundForS(openSetting.getInt("opponentSLower"));
            opponentSp.setLowerBoundForV(openSetting.getInt("opponentVLower"));

            opponentSp.setUpperBoundForH(openSetting.getInt("opponentHUpper"));
            opponentSp.setUpperBoundForS(openSetting.getInt("opponentSUpper"));
            opponentSp.setUpperBoundForV(openSetting.getInt("opponentVUpper"));


            colourPanel.setRobotSizeMinimum(openSetting.getInt("robotMinSize",0));
            colourPanel.setGreenSizeMinimum(openSetting.getInt("greenMinSize",0));
            colourPanel.setBallSizeMinimum(openSetting.getInt("ballMinSize",0));

            colourPanel.setRobotSizeMaximum(openSetting.getInt("robotMaxSize",0));
            colourPanel.setGreenSizeMaximum(openSetting.getInt("greenMaxSize",0));
            colourPanel.setBallSizeMaximum(openSetting.getInt("ballMaxSize",0));

        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

}
