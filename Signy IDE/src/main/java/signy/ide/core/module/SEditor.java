package signy.ide.core.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import javafx.scene.control.Tab;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import signy.ide.controls.nodes.SCodeArea;

public class SEditor {

	private Tab tab;
	private String tabTitle;
	private SCodeArea textArea;

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
		textArea = new SCodeArea(title, this.fileExtension, content);
		ScaledVirtualized<SCodeArea> scaleVirtualized = new ScaledVirtualized<>(textArea);

		textArea.addEventFilter(ScrollEvent.ANY, e -> {
			if (e.isControlDown()) {
				double scaleAmount = 0.9;
				Scale zoom = scaleVirtualized.getZoom();

				double scale = e.getDeltaY() < 0 ? zoom.getY() * scaleAmount : zoom.getY() / scaleAmount;
				zoom.setX(scale);
				zoom.setY(scale);
			}
		});

		textArea.textProperty().addListener(((observable, oldValue, newValue) -> {
			setModified(true);
		}));

		modified = false;

		tab.setUserData(this);

		tabTitle = title;
		tab.setText(tabTitle);
		VirtualizedScrollPane<ScaledVirtualized<SCodeArea>> virtualizedScrollPane = new VirtualizedScrollPane<>(scaleVirtualized);
		tab.setContent(virtualizedScrollPane);

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
