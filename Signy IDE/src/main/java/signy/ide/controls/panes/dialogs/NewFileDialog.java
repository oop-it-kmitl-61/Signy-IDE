package signy.ide.controls.panes.dialogs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import signy.ide.FXMLDocumentController;
import signy.ide.LoadingController;
import signy.ide.core.module.SExplorer;
import signy.ide.core.resources.SFile;
import signy.ide.core.resources.SProject;
import signy.ide.utils.FileUtil;

public class NewFileDialog {

	private String iconLogo = "icons/logo.png";
	private Path temp;

	public NewFileDialog() {

		Dialog<String[]> dialog = new Dialog<>();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialog.resizableProperty().setValue(true);

		stage.getIcons().add(new Image(iconLogo));
		dialog.setTitle("New File");
		dialog.setHeaderText("File\nCreate a new file.");
		dialog.setGraphic(new ImageView(new Image(iconLogo, 50, 50, false, true)));

		Path needed = LoadingController.getWorkspacePath();
		File folder = needed.toFile();

		ButtonType finishButtonType = new ButtonType("Finish", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(finishButtonType, ButtonType.CANCEL);

		final Label labelPath = new Label("Enter or select the parent folder:");

		HBox hboxTextFieldPath = new HBox();
		hboxTextFieldPath.setPadding(new Insets(5, 0, 5, 0));

		TextField textFieldPath = new TextField();
		HBox.setHgrow(textFieldPath, Priority.ALWAYS);
		TreeItem<File> selectedItem = LoadingController.getController().getViewPane().getExplorerTab().getExplorerView()
				.getSelectionModel().getSelectedItem();
		String path = (selectedItem == null) ? LoadingController.getWorkspacePath().toString()
				: (selectedItem.getValue().isDirectory()) ? selectedItem.getValue().getAbsolutePath()
						: selectedItem.getValue().getParent();
		textFieldPath.setText(path);
		textFieldPath.setPromptText("Parent folder");

		hboxTextFieldPath.getChildren().add(textFieldPath);

		TreeView<File> treeView = FileUtil.createDirectoryHierarchy(folder);

		treeView.setPrefHeight(0);

		HBox hboxFileName = new HBox();
		hboxFileName.setPadding(new Insets(8, 0, 0, 0));
		final Label labelFileName = new Label("File name:");
		labelFileName.setPrefWidth(60);
		labelFileName.setPadding(new Insets(5, 0, 0, 0));
		TextField textFieldFilename = new TextField();
		textFieldFilename.setPromptText("File name");
		HBox.setHgrow(textFieldFilename, Priority.ALWAYS);

		hboxFileName.getChildren().addAll(labelFileName, textFieldFilename);

		Node finishButton = dialog.getDialogPane().lookupButton(finishButtonType);
		finishButton.getStyleClass().add("confirm-button");
		finishButton.setDisable(true);

		textFieldPath.textProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> finishButton
					.setDisable(newValue.trim().isEmpty() || textFieldFilename.getText().trim().isEmpty()));
		});

		textFieldFilename.textProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> finishButton
					.setDisable(newValue.trim().isEmpty() || textFieldPath.getText().trim().isEmpty()));
		});

		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				TreeItem<File> selectedItem = (TreeItem<File>) treeView.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					if (selectedItem.getValue().isDirectory()) {
						textFieldPath.setText(selectedItem.getValue().getAbsolutePath());
					} else {
						textFieldPath.setText(selectedItem.getValue().getParent());
					}
				}
			}

		});
		FileUtil.expandTree(treeView.getRoot(), Paths.get(path).toFile());

		GridPane gridPane = new GridPane();
		gridPane.setPrefSize(510, 440);
		gridPane.setPadding(new Insets(8, 10, 65, 10));

		gridPane.add(labelPath, 0, 0);
		gridPane.add(hboxTextFieldPath, 0, 1);
		GridPane.setHgrow(hboxTextFieldPath, Priority.ALWAYS);
		gridPane.add(treeView, 0, 2);
		GridPane.setHgrow(treeView, Priority.ALWAYS);
		GridPane.setVgrow(treeView, Priority.ALWAYS);
		gridPane.add(hboxFileName, 0, 3);
		GridPane.setHgrow(hboxFileName, Priority.ALWAYS);

		gridPane.getStyleClass().add("content-panel");
		dialog.getDialogPane().setContent(gridPane);
		dialog.getDialogPane().getStylesheets().add("css/dialog.css");

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == finishButtonType) {
				return new String[] { textFieldPath.getText(), textFieldFilename.getText() };
			}

			return null;
		});

		Optional<String[]> result = dialog.showAndWait();

		result.ifPresent(config -> {
			Path newFilePath = Paths.get(config[0] + File.separator + config[1]);
			FileUtil.createFile(newFilePath);
			SExplorer sExplorer = LoadingController.getController().getViewPane().getExplorerTab();
			sExplorer.refresh(needed.toString());
			TreeItem<File> item = FileUtil.traverse(sExplorer.getExplorerView().getRoot(), newFilePath.toFile());
			SProject project = null;
			if (item != null) {
				if (item.getValue() instanceof SFile) {
					project = ((SFile) item.getValue()).getRootProject();
				}
			}
			LoadingController.getController().getEditorPane().createNewEditorTab(project, newFilePath.toFile());
		});

	}

}
