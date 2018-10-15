package signy.ide.controls.panes;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class STerminalPane {

	private TabPane tabPane;

	public STerminalPane() {

		this.tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(new Tab("Problems"));
		tabPane.getTabs().add(new Tab("Output"));
		tabPane.getTabs().add(new Tab("Console"));
		tabPane.getTabs().add(new Tab("Progress"));

	}

	public TabPane getTabPane() {
		return tabPane;
	}

}
