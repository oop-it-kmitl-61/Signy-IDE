package signy.ide.controls.button;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lib.org.eclipse.fx.ui.panes.SashPane;

public class CloseButton extends Button {

	private SashPane root;
	private ResizeButton buttonResize;

	private String pathClose = "signy/ide/resources/icons/close.png";

	private ImageView iconClose;

	public CloseButton(SashPane root, ResizeButton buttonResize, int iconWidth, int iconHeight) {

		this.root = root;
		this.buttonResize = buttonResize;

		iconClose = new ImageView(new Image(pathClose, iconWidth, iconHeight, false, true));
		setGraphic(iconClose);
		getStyleClass().add("tab-button");

		setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				closeRootPane();
			}

		});
	}

	private void closeRootPane() {
		buttonResize.setStage(false);
		for (Node sash : root.getSashes()) {
			root.setWeights(new int[] { 1, 0 });
			sash.setMouseTransparent(false);
		}
	}

}
