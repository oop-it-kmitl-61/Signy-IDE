package signy.ide.core.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import javafx.scene.control.Tab;
import signy.ide.LoadingController;
import signy.ide.Utils;
import signy.ide.core.dom.JavaDocumentPartitioner;

public class SEditor {

	private Tab tab;
	private String tabTitle;
	private CodeArea textArea;

	private File file;
	private String fileName;
	private Path path;
	private boolean modified = false;
	private String fileExtension;

	private boolean listened = false;

	public SEditor() {

		this("Untitled", "", null, ".*");

	}

	public SEditor(File file) {

		this(file.getName(), fileToString(file.getPath(), StandardCharsets.UTF_8), file.toPath(),
				file.getName().substring(file.getName().lastIndexOf(".")));
		this.file = file;

	}

	SEditor(String title, String content, Path path, String fileExtension) {

		fileName = title;
		this.path = path;
		this.fileExtension = "*" + fileExtension;

		tab = new Tab();
		textArea = new CodeArea();
		
		textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));

		textArea.textProperty().addListener(((observable, oldValue, newValue) -> {
			setModified(true);

			if (this.fileExtension.equals("*.java")) {
				try {
					textArea.setStyleSpans(0, JavaDocumentPartitioner.getSyntaxHighlighting(newValue));
				} catch (StackOverflowError e) {
					System.err.println("StackOverflowError : Parentheses in text content didn't correctly.");
				}
				SOutline.createOutline(newValue);

//				if (!(tab.getId().contains("inter"))) {
//					Utils.findMainClass(textArea.getText());
//				}

				if (Utils.getClassName(textArea.getText()).length() <= 0 && !(tab.getId().contains("inter"))) {

				} else {

					Iterator<Entry<String, String>> it = LoadingController.getClassCode().entrySet().iterator();

					while (it.hasNext()) {
						Entry<String, String> pair = it.next();
						if (pair.getKey().toString().equals(tab.getId())) {
							tab.setText(Utils.getClassName(textArea.getText()));
							LoadingController.setClassCode(pair.getKey().toString(), textArea.getText());
						}
					}

				}

//				if (Utils.getInterName(textArea.getText()).length() <= 0 && !(tab.getId().contains("tab"))) {
//
//				} else {

					Iterator<Entry<String, String>> inter = LoadingController.getInter().entrySet().iterator();

					while (inter.hasNext()) {
						Entry<String, String> pair = inter.next();
						if (pair.getKey().toString().equals(tab.getId())) {
							tab.setText(Utils.getInterName(textArea.getText()));
							LoadingController.setInter(pair.getKey().toString(), textArea.getText());
						}
					}

//				}

			}
		}));

		textArea.appendText(content);
		modified = false;

		tab.setUserData(this);

		tabTitle = title;
		tab.setText(tabTitle);
		tab.setContent(new VirtualizedScrollPane<>(textArea));

	}

	public Tab getTab() {
		return this.tab;
	}

	public CodeArea getTextArea() {
		return this.textArea;
	}

	public File getFile() {
		return this.file;
	}

	public Path getPath() {
		return this.path;
	}

	void setPath(Path path) {
		this.path = path;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getText() {
		return textArea.getText();
	}

	public String getFileExtension() {
		return this.fileExtension;
	}

	public void setListened(boolean listened) {
		this.listened = listened;
	}

	public boolean isListened() {
		return listened;
	}

	public void setModified(boolean modified) {
		if (this.modified == false && modified == true && this.path != null) {
			this.tab.setText("> " + tabTitle);
		} else if (modified == false) {
			this.tab.setText(tabTitle);
		}
		this.modified = modified;
	}

	public boolean isModified() {
		return this.modified;
	}

	public void updateReference(File file) {
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
