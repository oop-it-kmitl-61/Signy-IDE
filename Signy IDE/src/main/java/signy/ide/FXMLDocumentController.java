package signy.ide;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import signy.ide.module.SEditor;
import signy.ide.module.SMenuBar;
import signy.ide.module.STreeView;

public class FXMLDocumentController implements Initializable {

	@FXML
	private HBox headBar;
	@FXML
	private BorderPane editorPane, terminalPane, treeviewPane;
	@FXML
	private TreeView<?> treeview;

	private Main mainApp;
	private MenuBar menuBar;
	private static SEditor sEditor;
	private static Thread methodupdate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.mainApp = Main.getMainApp();

		sEditor = new SEditor(mainApp);
		editorPane.getStylesheets().add(getClass().getResource("css/editor.css").toExternalForm());
		editorPane.setCenter(sEditor.getTabPane());

		terminalPane.getStylesheets().add(getClass().getResource("css/terminal.css").toExternalForm());

		treeview = new STreeView().getTreeView();
		treeviewPane.getStylesheets().add(getClass().getResource("css/tree-view.css").toExternalForm());
		treeviewPane.setCenter(treeview);

		headBar.setAlignment(Pos.CENTER_LEFT);

		Label label = new Label("LogoBoi");
		headBar.getChildren().add(label);

		menuBar = new SMenuBar(mainApp, sEditor).getMenuBar();
		headBar.getChildren().add(menuBar);
		menuBar.getStylesheets().add(getClass().getResource("css/menu-bar.css").toExternalForm());

	}

	static void init() {
		sEditor.handleNewFile();
	}

	public static void methodupdateInterrupt() {
		methodupdate.interrupt();
	}

}
