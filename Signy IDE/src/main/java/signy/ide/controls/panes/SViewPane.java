package signy.ide.controls.panes;

import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import signy.ide.core.module.SExplorer;
import signy.ide.core.module.SOutline;
import signy.ide.core.module.SSearch;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class SViewPane {

	private TabPane tabPane;

	private SExplorer sExplorer;
	private SOutline sOutline;
	private SSearch sSearch;

	public SViewPane(SEditorPane sEditor) {

		this.tabPane = new TabPane();

		tabPane.setSide(Side.LEFT);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		sExplorer = new SExplorer();
		sExplorer.addListenerToEditor(sEditor);

		sOutline = new SOutline();
		sOutline.listenToEditor(sEditor);

		sSearch = new SSearch();

		tabPane.getTabs().add(sExplorer.getTab());
		tabPane.getTabs().add(sOutline.getTab());
		tabPane.getTabs().add(sSearch.getTab());

		tabPane.getSelectionModel().select(sSearch.getTab());

	}

	public TabPane getTabPane() {
		return this.tabPane;
	}

}
