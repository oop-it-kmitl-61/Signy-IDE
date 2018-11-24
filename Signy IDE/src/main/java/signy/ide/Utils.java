package signy.ide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

public class Utils {

	public Utils() {

	}

	public static String getInterName(String text) {
		try {
			if (text.contains("interface")) {
				String temp[] = text.split(" ");
				for (int i = 0; i < temp.length; i++) {
					if (temp[i].equals("interface")) {
						return fixInterName(temp[++i].trim());
					}
				}
			}
		} catch (Exception ex) {

		}
		return "";
	}

	public static void findMainClass(String text) {

		try {
			if (text.contains("main(String")) {

				String[] temp = text.split(" ");
				for (int i = 0; i < temp.length; i++) {
					if (temp[i].equals("class")) {
						LoadingController.setMainClass(temp[++i]);

					}

				}

			}
		} catch (Exception ex) {

		}

	}

	private static String fixInterName(String s) {

		if (s.substring(s.length() - 1, s.length()).equals("{")) {
			return s.substring(0, s.length() - 1);
		}
		String t = "";
		char[] ch = s.toCharArray();

		for (int i = 0; i < ch.length; i++) {
			if (ch[i] != '<') {
				t += ch[i];
			} else {
				return t;
			}
		}

		return s;
	}

	public static String getClassName(String text) {

		try {
			if (text.contains("class")) {
				String temp[] = text.split(" ");
				for (int i = 0; i < temp.length; i++) {
					if (temp[i].equals("class")) {

						return fixClassName(temp[++i].trim());
					}
				}
			}

		} catch (Exception ex) {

		}
		return "";

	}

	private static String fixClassName(String s) {

		if (s.substring(s.length() - 1, s.length()).equals("{")) {
			return s.substring(0, s.length() - 1);
		}
		String t = "";
		char[] ch = s.toCharArray();

		for (int i = 0; i < ch.length; i++) {
			if (ch[i] != '<') {
				t += ch[i];
			} else {
				return t;
			}
		}

		return s;

	}

	public static void saveToFile() {

		try {

			Iterator<Entry<String, String>> it = LoadingController.getClassCode().entrySet().iterator();

			while (it.hasNext()) {

				Entry<String, String> pair = it.next();

				File file = new File(Utils.getClassName(pair.getValue().toString()) + ".java");
				FileOutputStream fos = new FileOutputStream(file);

				if (!file.exists()) {
					file.createNewFile();
				}

				byte[] content = pair.getValue().toString().getBytes();

				fos.write(content);

				fos.flush();
				fos.close();

			}

			Iterator<Entry<String, String>> inter = LoadingController.getInter().entrySet().iterator();

			while (inter.hasNext()) {

				Entry<String, String> pair = inter.next();

				File file = new File(Utils.getInterName(pair.getValue().toString()) + ".java");
				FileOutputStream fos = new FileOutputStream(file);

				if (!file.exists()) {
					file.createNewFile();
				}

				byte[] content = pair.getValue().toString().getBytes();

				fos.write(content);

				fos.flush();
				fos.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
