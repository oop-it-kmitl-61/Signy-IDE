package signy.ide.controls.items;

import java.io.File;

import javafx.scene.control.MenuItem;

public class SMenuItem extends MenuItem {

	private File file;

	public SMenuItem(File file) {
		super(file.getName());

		this.file = file;
	}

	public File getFile() {
		return file;
	}

}
