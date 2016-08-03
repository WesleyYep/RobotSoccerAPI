package api.save;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;

public class ConfigPreviousFile {

	//http://commons.apache.org/proper/commons-configuration/index.html

	String path = "PreviousFile.config";

	private static ConfigPreviousFile instance = null;
	private String previousVisionKey;
	private String previousStratKey;

	private XMLConfiguration config;

	protected ConfigPreviousFile() {
        previousVisionKey="previousVision";
        previousStratKey="previousStrat";
	}

	public static ConfigPreviousFile getInstance() {
		if (instance == null) {
			instance = new ConfigPreviousFile();
		}
		return instance;
	}

	public boolean checkFileExist() {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * create a new configuration file for robot soccer program
	 * @return 0 - successfully created configuration file
	 * ,1 - encountered an error while creating the file
	 * ,2 - file already created
	 */
	public void createConfigFile() {
		try {
			if (checkFileExist()) {
				config = new XMLConfiguration(path);
			} else {
				config = new XMLConfiguration();
				config.setFile(new File(path));
			}
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String getPreviousVisionFile() {
		return config.getString(previousVisionKey, null);
	}

	public String getPreviousStratFile() {
		return config.getString(previousStratKey, null);
	}

	public void setPreviousVisionFile(String path) {
		if (config.getString(previousVisionKey, null) == null) {
			config.addProperty(previousVisionKey, path);
		} else {
			config.setProperty(previousVisionKey, path);
		}
		try {
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void setPreviousStratFile(String path) {
		if (config.getString(previousStratKey, null) == null) {
			config.addProperty(previousStratKey, path);
		} else {
			config.setProperty(previousStratKey, path);
		}
		
		try {
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

}
