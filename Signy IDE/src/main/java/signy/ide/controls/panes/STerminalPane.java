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
import signy.ide.FXMLDocumentController;
import signy.ide.controls.buttons.CloseButton;
import signy.ide.controls.buttons.DropDownButton;
import signy.ide.controls.buttons.ResizeButton;
import signy.ide.core.module.SConsole;
import signy.ide.core.module.SOutput;

public class STerminalPane {

	private SashPane root;
	private AnchorPane terminalPane;
	private TabPane tabPane;
	private HBox hbox;
	private CloseButton btnClose;
	private ResizeButton btnResize;
	private MenuButton btnDropDown;

	SConsole consolePane;
	SOutput outputPane;

	public STerminalPane(FXMLDocumentController controller) {

		this.root = controller.getSubWorkspacePane();
		terminalPane = new AnchorPane();
		tabPane = new TabPane();

		consolePane = new SConsole(controller);
		tabPane.getTabs().add(consolePane.getTab());

		tabPane.getTabs().add(new Tab("PROBLEMS"));

		outputPane = new SOutput();
		tabPane.getTabs().add(outputPane.getTab());
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

	public TabPane getTabPane() {
		return tabPane;
	}

	public AnchorPane getTerminalPane() {
		return terminalPane;
	}

	public SConsole getConsolePane() {
		return consolePane;
	}

	public SOutput getOutputPane() {
		return outputPane;
	}

}
