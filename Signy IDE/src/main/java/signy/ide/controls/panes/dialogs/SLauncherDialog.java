package signy.ide.controls.panes.dialogs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import signy.ide.settings.PropertySetting;
import signy.ide.utils.FileUtil;

public class SLauncherDialog {

	public SLauncherDialog() {

		Dialog<Path> dialog = new Dialog<>();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

		stage.getIcons().add(new Image("icons/logo512-16.png"));
		dialog.setTitle("Signy Launcher");
		dialog.setHeaderText(
				"Select a directory as workspace\nSigny IDE uses the workspace directory to store its preferences and development artifacts.");

		ButtonType loginButtonType = new ButtonType("Launch", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		HBox hbox = new HBox();
//		hbox.setPrefHeight(10);

		ObservableList<Path> choices = FXCollections.observableArrayList();
		Path needed = Files.exists(Paths.get(PropertySetting.getProperty("path")))
				? Paths.get(PropertySetting.getProperty("path"))
				: Paths.get(PropertySetting.getProperty("defaultpath"));

		File folder = needed.toFile();
		choices.add(folder.toPath());

		final Label labelWorkspace = new Label("Workspace:");
		labelWorkspace.setPrefWidth(70);
		ComboBox<Path> comboBox = new ComboBox<>(choices);
		comboBox.setPrefWidth(425);
		final Region gap = new Region();
		gap.setPrefWidth(8);
		Button btnBrowse = new Button("Browse...");
		btnBrowse.setPrefWidth(90);
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setInitialDirectory(folder);
				directoryChooser.setTitle("Select Workspace Directory");

				File selectedFile = directoryChooser.showDialog(stage);
				if (selectedFile != null) {
					choices.add(selectedFile.toPath());
					comboBox.getSelectionModel().selectLast();
				}

			}

		});

		hbox.getChildren().addAll(labelWorkspace, comboBox, gap, btnBrowse);

		CheckBox checkBox = new CheckBox("Use this as the default and do not ask again");

		GridPane gridPane = new GridPane();
		gridPane.setVgap(20);
		gridPane.setPadding(new Insets(24, 10, 65, 10));

		gridPane.add(hbox, 0, 0);
		gridPane.add(checkBox, 0, 1);

//		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
//		loginButton.setDisable(true);
//
//		comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//			loginButton.setDisable(newValue == null);
//		});

		comboBox.getSelectionModel().selectFirst();

		dialog.getDialogPane().setContent(gridPane);

		Platform.runLater(() -> comboBox.requestFocus());

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return comboBox.getValue();
			} else if (dialogButton.equals(btnBrowse)) {

			}
			System.exit(0);
			return null;
		});

		Optional<Path> result = dialog.showAndWait();

		result.ifPresent(workspacePath -> {
			if (!workspacePath.toFile().exists()) {
				FileUtil.createFolder(workspacePath);
			}

			if (checkBox.isSelected()) {
				PropertySetting.setProperty("usedefaultpath", "true");
			}
			PropertySetting.setProperty("path", workspacePath.toString());
		});

	}

}
