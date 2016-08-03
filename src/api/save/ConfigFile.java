package api.save;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;

public class ConfigFile {

	//http://commons.apache.org/proper/commons-configuration/index.html

	String path = "RobotSoccerConfiguration.config";

	private static ConfigFile instance = null;
	private String saveDirectoryKey;
	private String openDirectoryKey;

	private XMLConfiguration config;

	protected ConfigFile() {
		saveDirectoryKey="savedirectory";
		openDirectoryKey="opendirectory";
	}

	public static ConfigFile getInstance() {
		if (instance == null) {
			instance = new ConfigFile();
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
	public void createConfigFile() throws ConfigurationException {
		if (checkFileExist()) {
			config = new XMLConfiguration(path);
		} else {
			config = new XMLConfiguration();
			config.setFile(new File(path));
		}
		config.save();
	}

	public String getLastSaveDirectory() {
		return config.getString(saveDirectoryKey, null);
	}

	public String getLastOpenDirectory() {
		return config.getString(openDirectoryKey, null);
	}

	public void setLastSaveDirectory(String path) {
		if (config.getString(saveDirectoryKey, null) == null) {
			config.addProperty(saveDirectoryKey, path);
		} else {
			config.setProperty(saveDirectoryKey, path);
		}
		try {
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void setLastOpenDirectory(String path) {
		if (config.getString(openDirectoryKey, null) == null) {
			config.addProperty(openDirectoryKey, path);
		} else {
			config.setProperty(openDirectoryKey, path);
		}
		
		try {
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

}
