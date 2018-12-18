package signy.ide.controls.panes.dialogs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import signy.ide.LoadingController;
import signy.ide.core.resources.SProject;

public class NewJavaProjectDialog {

	private String iconLogo = "icons/logo.png";
	private Path temp;

	public NewJavaProjectDialog() {

		Dialog<String[]> dialog = new Dialog<>();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialog.resizableProperty().setValue(true);

		stage.getIcons().add(new Image(iconLogo));
		dialog.setTitle("New Java Project");
		dialog.setHeaderText("Create a Java Project\nEnter a project name.");
		dialog.setGraphic(new ImageView(new Image(iconLogo, 50, 50, false, true)));

		Path needed = LoadingController.getWorkspacePath();
		File folder = needed.toFile();

		ButtonType finishButtonType = new ButtonType("Finish", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(finishButtonType, ButtonType.CANCEL);

		HBox hboxProjectName = new HBox();
		hboxProjectName.setPrefHeight(36);

		final Label labelName = new Label("Project name:");
		labelName.setPrefWidth(80);
		labelName.setPadding(new Insets(5, 0, 0, 0));
		TextField textFieldName = new TextField();
		HBox.setHgrow(textFieldName, Priority.ALWAYS);
		textFieldName.setPromptText("Project name");

		hboxProjectName.getChildren().addAll(labelName, textFieldName);

		CheckBox checkBox = new CheckBox("Use default location");
		checkBox.setSelected(true);

		HBox hboxLocation = new HBox();
		hboxLocation.setPadding(new Insets(5, 0, 0, 0));

		final Label labelLocation = new Label("Location:");
		labelLocation.setPadding(new Insets(5, 5, 0, 0));
		TextField textFieldLocation = new TextField();
		HBox.setHgrow(textFieldLocation, Priority.ALWAYS);
		textFieldLocation.setText(folder.getAbsolutePath());
		final Region gap1 = new Region();
		gap1.setPrefWidth(8);
		Button btnBrowse = new Button("Browse...");
		btnBrowse.setPrefWidth(90);
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setInitialDirectory(folder);
				directoryChooser.setTitle("Project Folder Selection");

				File selectedFile = directoryChooser.showDialog(stage);
				if (selectedFile != null) {
					textFieldLocation.setText(selectedFile.getAbsolutePath());
				}

			}

		});

		hboxLocation.getChildren().addAll(labelLocation, textFieldLocation, gap1, btnBrowse);

		Node finishButton = dialog.getDialogPane().lookupButton(finishButtonType);
		finishButton.getStyleClass().add("confirm-button");
		finishButton.setDisable(true);

		textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
			newValue = newValue.trim();
			finishButton.setDisable(newValue == null || newValue.equals(""));
		});

		textFieldLocation.textProperty().addListener((observable, oldValue, newValue) -> {
			if (checkBox.isSelected() == false) {
				temp = Paths.get(newValue);
			}
		});

		checkBox.selectedProperty().addListener((observale, oldValue, newValue) -> {
			labelLocation.setDisable(newValue);
			textFieldLocation.setDisable(newValue);
			btnBrowse.setDisable(newValue);
			textFieldLocation.setText(newValue ? folder.getAbsolutePath() : temp == null ? "" : temp.toString());
		});

		GridPane gridPane = new GridPane();
		gridPane.minWidth(525);
		gridPane.minHeight(215);
		gridPane.setPrefWidth(525);
		gridPane.setPadding(new Insets(16, 20, 65, 25));

		gridPane.add(hboxProjectName, 0, 0);
		GridPane.setHgrow(hboxProjectName, Priority.ALWAYS);
		gridPane.add(checkBox, 0, 1);
		gridPane.add(hboxLocation, 0, 2);

		gridPane.getStyleClass().add("content-panel");
		dialog.getDialogPane().setContent(gridPane);
		dialog.getDialogPane().getStylesheets().add("css/dialog.css");

		Platform.runLater(() -> {
			textFieldName.requestFocus();
			labelLocation.setDisable(true);
			textFieldLocation.setDisable(true);
			btnBrowse.setDisable(true);
		});

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == finishButtonType) {
				return new String[] { textFieldName.getText(), textFieldLocation.getText() };
			}

			return null;
		});

		Optional<String[]> result = dialog.showAndWait();

		result.ifPresent(config -> {
			if (config != null) {
				String projectName = config[0];
				String path = config[1];
				SProject sProject = new SProject(projectName, Paths.get(path + File.separator + projectName));
				LoadingController.getAllProjects().add(sProject);
				sProject.initProject();
				LoadingController.getController().getViewPane().getExplorerTab().refresh(path);
			}
		});

	}

}
