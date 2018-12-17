package signy.ide.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertySetting {

	private static String language;
	private static String locale;
	private static String defaultPath;
	private static String useDefaultPath;
	private static String path;

	private static Path SETTINGS_PATH;
	private static Path pathProperties;

	private static Properties properties;

	static {

		properties = new Properties();
		initSettingsPath();
		initAppProperties();
		initProperties();

	}

	public static void initProperties() {

		defaultPath = PropertySetting.initVariable("defaultpath",
				System.getProperty("user.home") + File.separator + "signy-workspace" + File.separator);
		useDefaultPath = PropertySetting.initVariable("usedefaultpath", "false");
		path = PropertySetting.initVariable("path", "null");
		PropertySetting.writeProperty();

	}

	private static void initAppProperties() {
		pathProperties = SETTINGS_PATH.resolve(".settings");
		try {
			if (!Files.exists(pathProperties)) {
				Files.createFile(pathProperties);
			}
			properties.load(new FileInputStream(new File(String.valueOf(pathProperties.toFile()))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initSettingsPath() {
		SETTINGS_PATH = Paths.get(System.getProperty("user.home"), ".signy");
		try {
			if (!Files.exists(SETTINGS_PATH)) {
				Files.createDirectory(SETTINGS_PATH);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public static void writeProperty() {
		File file = pathProperties.toFile();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			properties.store(fos, "Update");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String initVariable(String key, String defaultValue) {
		String result = PropertySetting.getProperty(key);
		if (result == null) {
			PropertySetting.setProperty(key, defaultValue);
			result = defaultValue;
		}

		return result;
	}

}
