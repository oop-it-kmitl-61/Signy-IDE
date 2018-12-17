package signy.ide.core.module.autocomplete;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.fxmisc.richtext.CodeArea;

public abstract class AutocompleteAnalyser extends Thread {

	protected static List<File> files;
	protected static String suffix;

	static {
		files = new ArrayList<>();
	}

	public void listFilesForFolder(File entry) {
		for (final File fileEntry : Objects.requireNonNull(entry.listFiles())) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else if (fileEntry.getName().endsWith("." + suffix)) {
				files.add(fileEntry);
			}
		}
	}

	public void compute(CodeArea area) {
		throw new UnsupportedOperationException();
	}

	public void callAnalysis(String text, boolean isNew) {
		throw new UnsupportedOperationException();
	}

	public List<File> getFilesCache() {
		return files;
	}

}
