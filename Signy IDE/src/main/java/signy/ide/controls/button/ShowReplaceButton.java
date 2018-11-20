package signy.ide.controls.button;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ShowReplaceButton extends Button {

	private BorderPane root, child;

	private String pathExtend = "signy/ide/resources/icons/replace_extend.png";
	private String pathCollapse = "signy/ide/resources/icons/replace_collapse.png";

	ImageView iconExtend, iconCollapse;

	private boolean stage;

	public ShowReplaceButton(BorderPane root, BorderPane child) {

		this.root = root;
		this.child = child;

		getStyleClass().add("show-replace-button");

		iconExtend = new ImageView(new Image(pathExtend, 6, 8, false, true));
		iconCollapse = new ImageView(new Image(pathCollapse, 6, 6, false, true));
		setGraphic(iconExtend);
		stage = false;

		setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				toggleStage();
			}

		});

	}

	public boolean isStage() {
		return stage;
	}

	private void toggleStage() {
		if (stage == false) {
			root.setBottom(child);
			setGraphic(iconCollapse);
			stage = true;
		} else {
			root.setBottom(null);
			setGraphic(iconExtend);
			stage = false;
		}
	}

}
