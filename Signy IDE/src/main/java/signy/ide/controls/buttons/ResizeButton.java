package signy.ide.controls.buttons;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lib.org.eclipse.fx.ui.panes.SashPane;

public class ResizeButton extends Button {

	private SashPane root;
	private int[] oldWeight;

	private String pathMaximize = "icons/maximize.png";
	private String pathMinimize = "icons/minimize.png";

	private ImageView iconMaximize, iconMinimize;

	private boolean stage;

	public ResizeButton(SashPane root, int iconWidth, int iconHeight) {

		this.root = root;
		this.oldWeight = root.getWeights();

		iconMaximize = new ImageView(new Image(pathMaximize, iconWidth, iconHeight, false, true));
		iconMinimize = new ImageView(new Image(pathMinimize, iconWidth, iconHeight, false, true));
		setGraphic(iconMaximize);
		stage = false;
		getStyleClass().add("tab-button");

		setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				toggleStage();
			}

		});

	}

	public void setStage(boolean stage) {
		this.stage = stage;
		if (stage == true) {
			for (Node sash : root.getSashes()) {
				sash.setMouseTransparent(true);
			}
			setGraphic(iconMinimize);
		} else {
			for (Node sash : root.getSashes()) {
				sash.setMouseTransparent(false);
			}
			setGraphic(iconMaximize);
		}
	}

	public boolean isStage() {
		return stage;
	}

	private void toggleStage() {
		if (stage == false) {
			oldWeight = root.getWeights();
			root.setWeights(new int[] { 0, 1 });
			setStage(true);
		} else {
			root.setWeights(oldWeight);
			setStage(false);
		}
	}
	
}
