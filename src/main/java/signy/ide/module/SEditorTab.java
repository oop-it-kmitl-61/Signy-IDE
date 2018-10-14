package signy.ide.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

public class SEditorTab {

	private Tab tab;
	private String tabTitle;
	private TextArea textArea;

	private File file;
	private String fileName;
	private Path path;
	private boolean modified = false;
	private String fileExtension;

	SEditorTab() {

		this("Untitled", "", null, ".*");

	}

	SEditorTab(String title, String content, Path path, String fileExtension) {

		this.fileName = title;
		this.path = path;
		this.fileExtension = "*" + fileExtension;

		this.tab = new Tab();
		this.textArea = new TextArea();

		this.textArea.setText(content);

		this.textArea.textProperty().addListener(((observable, oldValue, newValue) -> {
			setModified(true);
		}));

		this.tab.setUserData(this);

		this.tabTitle = title;
		this.tab.setText(tabTitle);
		this.tab.setContent(textArea);

	}

	SEditorTab(File file) {

		this(file.getName(), fileToString(file.getPath(), StandardCharsets.UTF_8), file.toPath(),
				file.getName().substring(file.getName().lastIndexOf(".")));
		this.file = file;

	}

	Tab getTab() {
		return this.tab;
	}

	public TextArea getTextArea() {
		return this.textArea;
	}

	File getFile() {
		return this.file;
	}

	Path getPath() {
		return this.path;
	}

	void setPath(Path path) {
		this.path = path;
	}

	String getFileName() {
		return this.fileName;
	}

	String getContent() {
		return textArea.getText();
	}

	String getFileExtension() {
		return this.fileExtension;
	}

	boolean isModified() {
		return this.modified;
	}

	void setModified(boolean modified) {
		if (this.modified == false && modified == true && this.path != null) {
			this.tab.setText("> " + tabTitle);
		}
		else if (modified == false) {
			this.tab.setText(tabTitle);
		}
		this.modified = modified;
	}

	void updateReference(File file) {
		this.file = file;
		this.fileName = file.getName();
		this.path = file.toPath();
		this.fileExtension = "*" + file.getName().substring(file.getName().lastIndexOf("."));
		this.tabTitle = file.getName();
		setModified(false);
	}

	private static String fileToString(String path, Charset encoding) {
		String content = "";
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			content = new String(encoded, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

}
