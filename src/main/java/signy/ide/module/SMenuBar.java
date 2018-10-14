package signy.ide.module;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import signy.ide.Main;
import signy.ide.module.SEditor;

public class SMenuBar {

	private Main mainApp;
	private SEditor editor;

	private Menu fileMenu, editMenu, selectionMenu, viewMenu, goMenu, helpMenu;

	public SMenuBar(Main main, SEditor sEditor) {

		this.mainApp = main;
		this.editor = sEditor;

		// 1. File Menu
		Menu fileMenu = new Menu("_File");

		MenuItem newMnItem = new MenuItem("_New");
		newMnItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
		newMnItem.setOnAction(e -> {
			this.editor.handleNewFile();
		});

		MenuItem newWindowMnItem = new MenuItem("New Window");
		newWindowMnItem.setAccelerator(
				new KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
		newWindowMnItem.setOnAction(e -> {
			System.out.println(e.getSource() + " didn't have any action yet");
		});

		MenuItem openFileMnItem = new MenuItem("Open File...");
		openFileMnItem.setOnAction(e -> {
			this.editor.handleOpenFiles();
		});

		MenuItem openProjectsMnItem = new MenuItem("Open Projects...");
		openProjectsMnItem.setOnAction(e -> {
			System.out.println(e.getSource() + " didn't have any action yet");
		});

		MenuItem openRecentMnItem = new MenuItem("Open Recent");
		openRecentMnItem.setOnAction(e -> {
			System.out.println(e.getSource() + " didn't have any action yet");
		});

		MenuItem saveMnItem = new MenuItem("_Save");
		saveMnItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
		saveMnItem.setOnAction(e -> {
			this.editor.handleSaveFile();
		});

		MenuItem saveAsMnItem = new MenuItem("Save _As...");
		saveAsMnItem.setAccelerator(
				new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
		saveAsMnItem.setOnAction(e -> {
			this.editor.handleSaveAs();
		});

		MenuItem saveAllMnItem = new MenuItem("Sav_e All");
		saveAllMnItem.setAccelerator(
				new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN, KeyCombination.SHORTCUT_DOWN));
		saveAllMnItem.setOnAction(e -> {
			this.editor.handleSaveAllFiles();
		});

		MenuItem closeMnItem = new MenuItem("_Close");
		closeMnItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
		closeMnItem.setOnAction(e -> {
			System.out.println(e.getSource() + " didn't have any action yet");
		});

		MenuItem closeAllMnItem = new MenuItem("C_lose All");
		closeAllMnItem.setOnAction(e -> {
			System.out.println(e.getSource() + " didn't have any action yet");
		});

		MenuItem restartMnItem = new MenuItem("Restart");
		restartMnItem.setOnAction(e -> {
			System.out.println(e.getSource() + " didn't have any action yet");
		});

		MenuItem exitMnItem = new MenuItem("E_xit");
		exitMnItem.setOnAction(e -> {
			mainApp.exit();
		});

		fileMenu.getItems().addAll(newMnItem, newWindowMnItem, new SeparatorMenuItem(), openFileMnItem,
				openProjectsMnItem, openRecentMnItem, new SeparatorMenuItem(), saveMnItem, saveAsMnItem, saveAllMnItem,
				new SeparatorMenuItem(), closeMnItem, closeAllMnItem, new SeparatorMenuItem(), restartMnItem,
				exitMnItem);

		// 2. Edit Menu
		Menu editMenu = new Menu("_Edit");

		// 3. Selection Menu
		Menu selectionMenu = new Menu("_Selection");

		// 4. View Menu
		Menu viewMenu = new Menu("_View");

		// 5. Go Menu
		Menu goMenu = new Menu("_Go");

		// 6. Help Menu
		Menu helpMenu = new Menu("_Help");

		this.fileMenu = fileMenu;
		this.editMenu = editMenu;
		this.selectionMenu = selectionMenu;
		this.viewMenu = viewMenu;
		this.goMenu = goMenu;
		this.helpMenu = helpMenu;

	}

	public MenuBar getMenuBar() {
		return new MenuBar(fileMenu, editMenu, selectionMenu, viewMenu, goMenu, helpMenu);
	}

}
