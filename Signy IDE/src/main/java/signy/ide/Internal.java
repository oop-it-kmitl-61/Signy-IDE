package signy.ide;

import java.util.Properties;

import signy.ide.lang.Lang;
import signy.ide.settings.PropertySetting;

public class Internal {

	private static Properties localeProperties;
	private static Properties localeLangProperties;

	static {

		localeProperties = new Properties();
		localeLangProperties = new Properties();

		init();

	}

	public static void init() {

		PropertySetting.initProperties();

		Lang.init();

	}

	public static String getProperty(String key, boolean locale) {
		Properties properties = locale ? localeProperties : localeLangProperties;

		String result = properties.getProperty(key);

		if (result == null) {
			System.exit(1);
		}

		return result;
	}

}
