package signy.ide.controls.panes;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import signy.ide.core.module.SExplorer;
import signy.ide.core.module.SOutlineTab;
import javafx.scene.control.TabPane.TabClosingPolicy;
import lib.org.eclipse.fx.ui.panes.SashPane;

public class SViewPane {

	private SashPane root;

	private TabPane tabPane;

	private SExplorer sExplorer;
	private SOutlineTab sOutlineTab;

	public SViewPane(SashPane root, SEditorPane sEditor) {

		this.root = root;
		this.tabPane = new TabPane();

		tabPane.setSide(Side.LEFT);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		sExplorer = new SExplorer("C:\\C");
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

	private void setRootWeight(int leftPaneScale, int rightPaneScale) {
		root.setWeights(new int[] { leftPaneScale, rightPaneScale });
	}

}
