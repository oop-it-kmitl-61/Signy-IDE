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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import signy.ide.LoadingController;
import signy.ide.core.module.SExplorer;
import signy.ide.core.resources.SFile;
import signy.ide.core.resources.SProject;
import signy.ide.lang.symbols.ModifiersType;
import signy.ide.utils.FileUtil;
import signy.ide.utils.Utils;

public class NewClassDialog {

	private String iconLogo = "icons/logo512-16.png";
	private String iconContent = "icons/class128.png";
	private Path temp;

	public NewClassDialog() {

		Dialog<String> dialog = new Dialog<>();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialog.resizableProperty().setValue(true);

		stage.getIcons().add(new Image(iconLogo));
		dialog.setTitle("New Java Class");
		dialog.setHeaderText("Java Class\nCreate a new Java class.");
		dialog.setGraphic(new ImageView(new Image(iconContent, 50, 50, false, false)));

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

		final Label labelPackage = new Label("Package:");
		TextField textFieldPackage = new TextField();
		if (selectedItem != null) {
			if (selectedItem.getValue() instanceof SFile) {
				if (selectedItem.getValue().getPath().toString().length() > ((SFile) selectedItem.getValue())
						.getRootProject().getPathSource().toString().length()) {
					textFieldPackage.setText(selectedItem.getValue().getPath().toString().substring(
							((SFile) selectedItem.getValue()).getRootProject().getPathSource().toString().length() + 1)
							.replaceAll(File.separator + File.separator, "."));
				}
			}
		}
		Button btnPackageBrowse = new Button("Browse...");
		btnPackageBrowse.setPrefWidth(90);

		final Label labelName = new Label("Name:");
		TextField textFieldName = new TextField();

		final Label labelModifiers = new Label("Modifiers:");
		final ToggleGroup group = new ToggleGroup();
		RadioButton rbPublic = new RadioButton("public");
		rbPublic.setToggleGroup(group);
		rbPublic.setSelected(true);
		RadioButton rbPackage = new RadioButton("package");
		rbPackage.setToggleGroup(group);
		rbPackage.setDisable(true);
		RadioButton rbPrivate = new RadioButton("private");
		rbPrivate.setToggleGroup(group);
		rbPrivate.setDisable(true);
		RadioButton rbProtected = new RadioButton("protected");
		rbProtected.setToggleGroup(group);
		rbProtected.setDisable(true);

		GridPane radioGroup = new GridPane();
		radioGroup.setHgap(16);
		radioGroup.add(rbPublic, 0, 0);
		radioGroup.add(rbPackage, 1, 0);
		radioGroup.add(rbPrivate, 2, 0);
		radioGroup.add(rbProtected, 3, 0);

		final Label labelQuestion1 = new Label("Which method stubs would you like to create?");
		CheckBox cbMedthod1 = new CheckBox("public static void main(String[] args)");

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
		gridPane.add(labelPackage, 0, 1);
		gridPane.add(textFieldPackage, 1, 1);
		GridPane.setHgrow(textFieldPackage, Priority.ALWAYS);
		gridPane.add(btnPackageBrowse, 2, 1);

		gridPane.add(labelName, 0, 2);
		gridPane.add(textFieldName, 1, 2);
		GridPane.setHgrow(textFieldName, Priority.ALWAYS);
		gridPane.add(labelModifiers, 0, 3);
		gridPane.add(radioGroup, 1, 3);
		gridPane.add(labelQuestion1, 0, 4, 2, 1);
		gridPane.add(cbMedthod1, 1, 5);

		gridPane.getStyleClass().add("content-panel");
		dialog.getDialogPane().setContent(gridPane);
		dialog.getDialogPane().getStylesheets().add("css/dialog.css");

		Node finishButton = dialog.getDialogPane().lookupButton(finishButtonType);
		finishButton.getStyleClass().add("confirm-button");
		finishButton.setDisable(true);

		Platform.runLater(() -> textFieldName.requestFocus());

		textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
			Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
			Matcher match = pattern.matcher(newValue);
			finishButton.setDisable(match.find() || newValue.isEmpty());
		});

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == finishButtonType) {
				return textFieldName.getText();
			}

			return null;
		});

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(config -> {
			Path newPath = Paths.get(path + File.separator + config + ".java");
			FileUtil.createFile(newPath);
			String content = "";
			if (group.getSelectedToggle() != null) {
				switch (((RadioButton) group.getSelectedToggle()).getText()) {
				case "public":
					if (!textFieldPackage.getText().trim().isEmpty()) {
						content += ModifiersType.KEYWORDS.get()[1] + " " + textFieldPackage.getText().trim() + " ;"
								+ System.lineSeparator() + System.lineSeparator();
					}
					content += ModifiersType.KEYWORDS.get()[0] + " " + ModifiersType.TYPES.get()[0] + " " + config
							+ " {" + System.lineSeparator() + System.lineSeparator();
					if (cbMedthod1.isSelected()) {
						content += "\tpublic static void main(String[] args) {" + System.lineSeparator()
								+ "\t\t// TODO Auto-generated method stub" + System.lineSeparator()
								+ System.lineSeparator() + "\t}";
					}
					content += System.lineSeparator() + System.lineSeparator() + "}" + System.lineSeparator();

					Utils.writeToFile(content, newPath);
					SExplorer sExplorer = LoadingController.getController().getViewPane().getExplorerTab();
					sExplorer.refresh(needed.toString());
					TreeItem<File> item = FileUtil.traverse(sExplorer.getExplorerView().getRoot(), newPath.toFile());
					SProject project = null;
					if (item != null) {
						if (item.getValue() instanceof SFile) {
							project = ((SFile) item.getValue()).getRootProject();
						}
					}
					LoadingController.setCurrentProject(project);
					LoadingController.getController().getEditorPane().createNewEditorTab(project, newPath.toFile());
					break;
				case "package":
					break;
				case "private":
					break;
				case "protected":
					break;
				default:
					break;
				}
			}
		});

	}

}
