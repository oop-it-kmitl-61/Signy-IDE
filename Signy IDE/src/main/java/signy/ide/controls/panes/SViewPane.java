package signy.ide.controls.panes;

import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import signy.ide.FXMLDocumentController;
import signy.ide.core.module.SExplorer;
import signy.ide.core.module.SOutline;
import signy.ide.core.module.SSearch;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class SViewPane {

	private TabPane tabPane;

	private SExplorer explorer;
	private SOutline outline;
	private SSearch search;

	public SViewPane(FXMLDocumentController controller) {

		this.tabPane = new TabPane();

		tabPane.setSide(Side.LEFT);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		explorer = new SExplorer(controller);
		outline = new SOutline(controller);
		search = new SSearch(controller);

		tabPane.getTabs().add(explorer.getTab());
		tabPane.getTabs().add(outline.getTab());
		tabPane.getTabs().add(search.getTab());

	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public SExplorer getExplorerTab() {
		return explorer;
	}

	public SOutline getOutlineTab() {
		return outline;
	}

	public SSearch getSearchTab() {
		return search;
	}

}
