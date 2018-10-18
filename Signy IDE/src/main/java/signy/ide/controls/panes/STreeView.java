package signy.ide.controls.panes;

import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import signy.ide.core.module.SExplorer;
import signy.ide.core.module.SOutlineTab;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class STreeView {

	private TabPane tabPane;

	private SExplorer sExplorer;
	private SOutlineTab sOutlineTab;

	public STreeView(SEditorPane sEditor) {

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
