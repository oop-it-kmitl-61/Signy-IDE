package signy.ide.controls.panes;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import signy.ide.core.module.SConsoleTab;
import signy.ide.core.module.SNetstat;

public class STerminalPane {

	private TabPane tabPane;

	public STerminalPane() {

		this.tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(new SConsoleTab().getTab());
		tabPane.getTabs().add(new SNetstat().getTab());
		tabPane.getTabs().add(new Tab("Problems"));
		tabPane.getTabs().add(new Tab("Output"));
		tabPane.getTabs().add(new Tab("Progress"));

	}

	public TabPane getTabPane() {
		return tabPane;
	}

}
