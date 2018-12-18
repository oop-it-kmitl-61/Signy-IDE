package signy.ide.controls.panes.dialogs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import signy.ide.LoadingController;
import signy.ide.core.resources.SFile;
import signy.ide.core.resources.SProject;
import signy.ide.utils.FileUtil;

public class NewJavaPackageDialog {

	private String iconLogo = "icons/logo512-16.png";
	private String iconContent = "icons/package128.png";
	private Path temp;

	public NewJavaPackageDialog() {

		Dialog<String> dialog = new Dialog<>();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialog.resizableProperty().setValue(true);

		stage.getIcons().add(new Image(iconLogo));
		dialog.setTitle("New Java Package");
		dialog.setHeaderText("Java Package\nCreate a new Java package.");
		dialog.setGraphic(new ImageView(new Image(iconContent, 50, 50, false, true)));

		Path needed = LoadingController.getWorkspacePath();
		File folder = needed.toFile();

		TreeItem<File> selectedItem = LoadingController.getController().getViewPane().getExplorerTab().getExplorerView()
				.getSelectionModel().getSelectedItem();
		String path = (selectedItem == null) ? needed.toString() : selectedItem.getValue().getAbsolutePath();

		ButtonType finishButtonType = new ButtonType("Finish", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(finishButtonType, ButtonType.CANCEL);

		final Label labelSourceFolder = new Label("Source folder:");
		labelSourceFolder.setPadding(new Insets(0, 32, 0, 0));
		TextField textFieldSourceFolder = new TextField();
		textFieldSourceFolder
				.setText(
						(selectedItem != null)
								? (selectedItem.getValue() instanceof SProject)
										? ((SProject) selectedItem.getValue()).getPathSource().toString()
												.substring(needed.toString().length() + 1)
										: (selectedItem.getValue() instanceof SFile)
												? ((SFile) selectedItem.getValue()).getRootProject().getPathSource()
														.toString().substring(needed.toString().length() + 1)
												: ""
								: "");
		Button btnSourceBrowse = new Button("Browse...");
		btnSourceBrowse.setPrefWidth(90);

		final Label labelName = new Label("Name:");
		TextField textFieldName = new TextField();
		if (selectedItem != null) {
			if (selectedItem.getValue() instanceof SFile) {
				if (selectedItem.getValue().getPath().toString().length() > ((SFile) selectedItem.getValue())
						.getRootProject().getPathSource().toString().length()) {
					textFieldName.setText(selectedItem.getValue().getPath().toString().substring(
							((SFile) selectedItem.getValue()).getRootProject().getPathSource().toString().length() + 1)
							.replaceAll(File.separator + File.separator, "."));
				}
			}
		}

		GridPane gridPane = new GridPane();
		gridPane.setHgap(5);
		gridPane.setVgap(8);
		gridPane.setPrefSize(535, 460);
		gridPane.setMinSize(535, 460);
		gridPane.setPadding(new Insets(8, 10, 65, 10));

		gridPane.add(labelSourceFolder, 0, 0);
		gridPane.add(textFieldSourceFolder, 1, 0);
		GridPane.setHgrow(textFieldSourceFolder, Priority.ALWAYS);
		gridPane.add(btnSourceBrowse, 2, 0);
		gridPane.add(labelName, 0, 1);
		gridPane.add(textFieldName, 1, 1);
		GridPane.setHgrow(textFieldName, Priority.ALWAYS);

		gridPane.getStyleClass().add("content-panel");
		dialog.getDialogPane().setContent(gridPane);
		dialog.getDialogPane().getStylesheets().add("css/dialog.css");

		Node finishButton = dialog.getDialogPane().lookupButton(finishButtonType);
		finishButton.getStyleClass().add("confirm-button");
		finishButton.setDisable(true);

		Platform.runLater(() -> textFieldName.requestFocus());

		textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
			Pattern pattern = Pattern.compile("[^a-z0-9.]", Pattern.CASE_INSENSITIVE);
			Matcher match = pattern.matcher(newValue);
			finishButton.setDisable(match.find() || newValue.isEmpty());
		});

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == finishButtonType) {
				return textFieldName.getText().replaceAll("\\.", File.separator + File.separator);
			}

			return null;
		});

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(configs -> {
			SProject project = null;
			if (selectedItem.getValue() instanceof SProject) {
				project = (SProject) selectedItem.getValue();
			} else if (selectedItem.getValue() instanceof SFile) {
				project = ((SFile) selectedItem.getValue()).getRootProject();
			}

			if (project != null) {
				Path newPath = Paths.get(project.getPathSource().toString());
				for (String config : configs.split(File.separator + File.separator)) {
					newPath = Paths.get(newPath.toString() + File.separator + config);
					FileUtil.createFolder(newPath);
				}
				LoadingController.getController().getViewPane().getExplorerTab().refresh(needed.toString());
			}
		});

	}

}
