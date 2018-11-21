package signy.ide.controls.buttons;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DropDownButton extends MenuButton {

	private TabPane root;

	private String pathDropdown = "signy/ide/resources/icons/dropdown.png";
	ImageView iconDropdown;

	private MenuItem itemConsole;
	private MenuItem itemProblems;
	private MenuItem itemOutput;
	private MenuItem itemProgress;

	public DropDownButton(TabPane tabPane, int iconWidth, int iconHeight) {

		this.root = tabPane;

		iconDropdown = new ImageView(new Image(pathDropdown, iconWidth, iconHeight, false, true));
		this.setGraphic(iconDropdown);

		itemConsole = new MenuItem("Console");
		itemProblems = new MenuItem("Problems");
		itemOutput = new MenuItem("Output");
		itemProgress = new MenuItem("Progress");

		getItems().addAll(itemConsole, itemProblems, itemOutput, itemProgress);

		getStyleClass().add("drop-down-button");

		initAction();

	}

	private void initAction() {
		for (Tab tab : root.getTabs()) {
			String key = tab.getText();
			switch (key) {
			case "CONSOLE":
				itemConsole.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						root.getSelectionModel().select(tab);
					}

				});
				break;
			case "PROBLEMS":
				itemProblems.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						root.getSelectionModel().select(tab);
					}

				});
				break;
			case "OUTPUT":
				itemOutput.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						root.getSelectionModel().select(tab);
					}

				});
				break;
			case "PROGRESS":
				itemProgress.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						root.getSelectionModel().select(tab);
					}

				});
				break;
			default:
				break;
			}
		}
	}

}
