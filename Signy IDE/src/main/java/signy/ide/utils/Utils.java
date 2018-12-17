package signy.ide.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Utils {

	public Utils() {

	}

	public static String fileToString(String path, Charset encoding) {
		String content = "";
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			content = new String(encoded, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	public static String listToString(List<?> list) {
		String result = "";
		for (int i = 0; i < list.size(); ++i) {
			result += list.get(i) + " ";
		}
		return result.trim();
	}

}
