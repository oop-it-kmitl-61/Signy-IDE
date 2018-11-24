package signy.ide.controls.panes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import signy.ide.FXMLDocumentController;
import signy.ide.Main;
import signy.ide.core.module.SEditor;
import signy.ide.core.module.SOutline;
import signy.ide.lang.symbols.SymbolsType;

public class SEditorPane {

	private FXMLDocumentController controller;
	private Main mainApp;
	private TabPane tabPane;
	private SEditor sEditor;
	private ReadOnlyObjectWrapper<SEditor> currentActiveTab = new ReadOnlyObjectWrapper<>();

	private File recentFile;

	private ExtensionFilter javaExtensionFilter = new ExtensionFilter("Java Files (*.java)", "*.java");
	private ExtensionFilter txtExtensionFilter = new ExtensionFilter("Plain Text (*.txt)", "*.txt");
	private ExtensionFilter noExtensionFilter = new ExtensionFilter("No Extension (*.)", "*.");
	private ExtensionFilter allExtensionFilter = new ExtensionFilter("All Files (*.*)", "*.*");

	@FXML
	private BorderPane treeviewPane;

	public SEditorPane(FXMLDocumentController controller) {

		this.controller = controller;
		this.mainApp = controller.getMainApp();

		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

		tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			if (newTab != null) {
				currentActiveTab.set((SEditor) newTab.getUserData());
				mainApp.setTitle(currentActiveTab.get().getPath());

				if (currentActiveTab.get().getFileExtension().equals("*.java")) {
					SOutline.createOutline(currentActiveTab.get().getText());
				} else {
					SOutline.createOutline(null);
				}
			} else {
				currentActiveTab.set(null);
				mainApp.setTitle(null);
			}
		});

	}

	private void requestFocus() {
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.selectLast();
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public SEditor getCurrentActiveTab() {
		return currentActiveTab.get();
	}

	private SEditor[] getAllEditorTabs() {
		List<Tab> allOpenTabs = tabPane.getTabs();
		SEditor[] editorTabs = new SEditor[allOpenTabs.size()];
		for (int i = 0, j = allOpenTabs.size(); i < j; i++) {
			editorTabs[i] = (SEditor) allOpenTabs.get(i).getUserData();
		}
		return editorTabs;
	}

	private File getRecentFile() {
		return this.recentFile;
	}

	private void setRecentFile(File file) {
		this.recentFile = file;
	}

	public void handleNewFile() {
		createNewEditorTab(null);
	}

	public void handleOpenFiles() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(javaExtensionFilter, txtExtensionFilter, allExtensionFilter);
		if (recentFile != null) {
			fileChooser.setInitialDirectory(recentFile.getParentFile());
		}
		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(mainApp.getScene().getWindow());

		if (selectedFiles == null) {
			return;
		}

		for (File file : selectedFiles) {
			createNewEditorTab(file);
		}
	}

	public void handleSaveFile() {
		saveFile(getCurrentActiveTab());
	}

	public void handleSaveAs() {
		saveFile(getCurrentActiveTab(), true);
	}

	public void handleSaveAllFiles() {
		if (getCurrentActiveTab() == null) {
			return;
		}
		SEditor[] allEditorTabs = getAllEditorTabs();
		saveFile(getCurrentActiveTab());
		for (SEditor editorTab : allEditorTabs) {
			if (!editorTab.equals(getCurrentActiveTab())) {
				saveFile(editorTab);
			}
		}
	}

	void handleCloseFile() {
		System.out.println("Handle Close File.");
		closeFile(getCurrentActiveTab());
	}

	public void createNewEditorTab(File file) {
		if (file == null) {
			sEditor = new SEditor();
		} else {

			sEditor = new SEditor(file);

		}

		sEditor.getTab().setOnCloseRequest(e -> {

		});

		tabPane.getTabs().add(sEditor.getTab());
		requestFocus();

		setRecentFile(sEditor.getFile());

	}

	private void saveFile(SEditor editorTab) {
		saveFile(editorTab, false);
	}

	private void saveFile(SEditor editorTab, boolean saveAs) {
		if (editorTab == null || (saveAs == false && editorTab.isModified() == false && editorTab.getPath() != null)) {
			return;
		}

		Path pathToSaveFile;
		if (editorTab.getFile() == null || saveAs) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll(javaExtensionFilter, txtExtensionFilter, noExtensionFilter,
					allExtensionFilter);
			String fileExtension = editorTab.getFileExtension();
			boolean isSetFirstExtension = false;
			if (fileExtension.equals(noExtensionFilter.getExtensions().get(0))) {
				fileChooser.setSelectedExtensionFilter(txtExtensionFilter);
				isSetFirstExtension = true;
			} else {
				for (ExtensionFilter extensionFilter : fileChooser.getExtensionFilters()) {
					for (String subExtension : extensionFilter.getExtensions()) {
						if (fileExtension.equals(subExtension)) {
							fileChooser.setSelectedExtensionFilter(extensionFilter);
							isSetFirstExtension = true;
							break;
						}
					}
				}
			}

			if (!isSetFirstExtension) {
				fileChooser.setSelectedExtensionFilter(allExtensionFilter);
			}

			fileChooser.setInitialFileName(editorTab.getFileName());
			if (editorTab.getFile() != null) {
				fileChooser.setInitialDirectory(editorTab.getFile().getParentFile());
			} else if (recentFile != null) {
				fileChooser.setInitialDirectory(recentFile.getParentFile());
			}
			File file = fileChooser.showSaveDialog(mainApp.getScene().getWindow());

			if (file == null) {
				return;
			}

			pathToSaveFile = file.toPath();
			editorTab.updateReference(file);
		} else {
			pathToSaveFile = editorTab.getPath();
			editorTab.updateReference(editorTab.getFile());
		}

		String contentToWrite = editorTab.getText().replace(SymbolsType.TAB.toString(), "\t");
		writeToFile(contentToWrite, pathToSaveFile);

		setRecentFile(editorTab.getFile());
		mainApp.setTitle(editorTab.getPath());

	}

	private void closeFile(SEditor editorTab) {
		if (editorTab == null) {
			return;
		}

	}

	private void writeToFile(String fileContent, Path path) {
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(fileContent, 0, fileContent.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String parseJavaFile(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

}
