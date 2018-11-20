package signy.ide.controls.panes;

import javafx.scene.control.MenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lib.org.eclipse.fx.ui.panes.SashPane;
import signy.ide.controls.button.CloseButton;
import signy.ide.controls.button.DropDownButton;
import signy.ide.controls.button.ResizeButton;
import signy.ide.core.module.SConsole;

public class STerminalPane {

	private SashPane root;
	private AnchorPane terminalPane;
	private TabPane tabPane;
	private HBox hbox;
	private CloseButton btnClose;
	private ResizeButton btnResize;
	private MenuButton btnDropDown;

	public STerminalPane(SashPane root) {

		this.root = root;
		terminalPane = new AnchorPane();
		tabPane = new TabPane();

		Tab consoleTab = new SConsole().getTab();
		consoleTab.setText("CONSOLE");
		tabPane.getTabs().add(consoleTab);

		tabPane.getTabs().add(new Tab("PROBLEMS"));
		tabPane.getTabs().add(new Tab("OUTPUT"));
		tabPane.getTabs().add(new Tab("PROGRESS"));

		hbox = new HBox();
		hbox.setFillHeight(true);
		hbox.setPrefWidth(100);
		hbox.setPrefHeight(32);
		hbox.getStyleClass().add("hbox");

		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);

		btnResize = new ResizeButton(this.root, 14, 8);
		btnClose = new CloseButton(this.root, btnResize, 10, 10);

		btnDropDown = new DropDownButton(tabPane, 13, 3);

		hbox.getChildren().addAll(region, btnDropDown, btnResize, btnClose);

		terminalPane.getChildren().addAll(tabPane, hbox);
		AnchorPane.setTopAnchor(hbox, 0.0);
		AnchorPane.setRightAnchor(hbox, 0.0);
		AnchorPane.setTopAnchor(tabPane, 0.0);
		AnchorPane.setRightAnchor(tabPane, 0.0);
		AnchorPane.setLeftAnchor(tabPane, 0.0);
		AnchorPane.setBottomAnchor(tabPane, 0.0);

		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

	}

	public AnchorPane getTerminalPane() {
		return terminalPane;
	}

}
