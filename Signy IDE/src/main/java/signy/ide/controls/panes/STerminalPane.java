package signy.ide.controls.panes;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lib.org.eclipse.fx.ui.panes.SashPane;
import signy.ide.core.module.SConsoleTab;
import signy.ide.core.module.SNetstat;

public class STerminalPane {

	private SashPane root;
	private AnchorPane terminalPane;
	private TabPane tabPane;
	private Button btnClose, btnMaximize;
	private HBox hbox;

	public STerminalPane(SashPane root) {

		this.root = root;
		terminalPane = new AnchorPane();
		tabPane = new TabPane();

		hbox = new HBox();
		hbox.setFillHeight(true);
		hbox.setPrefWidth(100);
		hbox.setPrefHeight(32);
		hbox.getStyleClass().add("hbox");

		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);

		btnClose = createTabButton("close.png", 10, 10);
		btnClose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				setRootWeight(1, 0);
			}

		});
		btnMaximize = createTabButton("maximize.png", 14, 8);
		btnMaximize.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				setRootWeight(0, 1);
			}

		});
		hbox.getChildren().addAll(region, btnMaximize, btnClose);

		terminalPane.getChildren().addAll(tabPane, hbox);
		AnchorPane.setTopAnchor(hbox, 0.0);
		AnchorPane.setRightAnchor(hbox, 0.0);
		AnchorPane.setTopAnchor(tabPane, 0.0);
		AnchorPane.setRightAnchor(tabPane, 0.0);
		AnchorPane.setLeftAnchor(tabPane, 0.0);
		AnchorPane.setBottomAnchor(tabPane, 0.0);

//		AnchorPane.setTopAnchor(tabPane, 0.0);
//		AnchorPane.setLeftAnchor(tabPane, 0.0);
//		AnchorPane.setBottomAnchor(tabPane, 0.0);
//		AnchorPane.setRightAnchor(tabPane, 0.0);
//		AnchorPane.setTopAnchor(addButton, 0.0);
//		AnchorPane.setRightAnchor(addButton, 0.0);

		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		Tab consoleTab = new SConsoleTab().getTab();
		consoleTab.setText("CONSOLE");
		tabPane.getTabs().add(consoleTab);

		Tab netStatTab = new SNetstat().getTab();
		netStatTab.setText("NETSTAT");
		tabPane.getTabs().add(netStatTab);

		tabPane.getTabs().add(new Tab("PROBLEMS"));
		tabPane.getTabs().add(new Tab("OUTPUT"));
		tabPane.getTabs().add(new Tab("PROGESS"));

	}

	public AnchorPane getTerminalPane() {
		return terminalPane;
	}

	private Button createTabButton(String iconName, int iconWidth, int iconHeight) {
		Button button = new Button();
		ImageView imageView = new ImageView(
				new Image("signy/ide/resources/icons/" + iconName, iconWidth, iconHeight, false, true));
		button.setGraphic(imageView);
		button.getStyleClass().add("tab-button");
		return button;
	}

	private void setRootWeight(int topPaneScale, int bottomPaneScale) {
		root.setWeights(new int[] { topPaneScale, bottomPaneScale });
	}

}
