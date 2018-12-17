package signy.ide.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang.exception.ExceptionUtils;

public class FileUtil extends ExceptionUtils {

	public static void createFile(Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFolder(Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFolders(Path... paths) {
		for (Path path : paths) {
			createFolder(path);
		}
	}

}
