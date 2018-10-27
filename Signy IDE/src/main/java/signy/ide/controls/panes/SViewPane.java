package signy.ide.controls.panes;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import signy.ide.core.module.SExplorer;
import signy.ide.core.module.SOutlineTab;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class SViewPane {

	private TabPane tabPane;

	private SExplorer sExplorer;
	private SOutlineTab sOutlineTab;

	public SViewPane(SEditorPane sEditor) {

		this.tabPane = new TabPane();
		tabPane.setSide(Side.LEFT);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		sExplorer = new SExplorer();
		sExplorer.addListenerToEditor(sEditor);
		sOutlineTab = new SOutlineTab();
		sOutlineTab.listenToEditor(sEditor);

		tabPane.getTabs().add(sExplorer.getTab());
		tabPane.getTabs().add(sOutlineTab.getTab());
		tabPane.getTabs().add(new Tab("Search"));

	}

	public TabPane getTabPane() {
		return this.tabPane;
	}

}
