package signy.ide;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lib.org.eclipse.fx.ui.panes.SashPane;
import signy.ide.controls.panes.SEditorPane;
import signy.ide.controls.panes.STerminalPane;
import signy.ide.controls.panes.SViewPane;
import signy.ide.core.module.SMenuBar;

public class FXMLDocumentController implements Initializable {

	@FXML
	private HBox headBar, toolBar, statusBar;
	@FXML
	private VBox profileBar;
	@FXML
	private BorderPane test;
	@FXML
	private SashPane workspacePane, subWorkspacePane;
	@FXML
	private AnchorPane treeviewPane, editorPane, terminalPane;

	private Main mainApp;
	private MenuBar menuBar;
	private SViewPane treeview;
	private static SEditorPane sEditorPane;
	private static STerminalPane sTerminalPane;
	private static Thread methodupdate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.mainApp = Main.getMainApp();

		sEditorPane = new SEditorPane(mainApp);
		treeview = new SViewPane(sEditorPane);
		sTerminalPane = new STerminalPane(subWorkspacePane);

		treeviewPane.getStylesheets().add(getClass().getResource("css/tree-view.css").toExternalForm());
		treeviewPane.getChildren().add(treeview.getTabPane());
		AnchorPane.setTopAnchor(treeview.getTabPane(), 0.0);
		AnchorPane.setRightAnchor(treeview.getTabPane(), 0.0);
		AnchorPane.setBottomAnchor(treeview.getTabPane(), 0.0);
		AnchorPane.setLeftAnchor(treeview.getTabPane(), 0.0);

		editorPane.getStylesheets().add(getClass().getResource("css/editor.css").toExternalForm());
		editorPane.getStylesheets().add(getClass().getResource("css/document.css").toExternalForm());
		editorPane.getChildren().add(sEditorPane.getTabPane());
		AnchorPane.setTopAnchor(sEditorPane.getTabPane(), 0.0);
		AnchorPane.setRightAnchor(sEditorPane.getTabPane(), 0.0);
		AnchorPane.setBottomAnchor(sEditorPane.getTabPane(), 0.0);
		AnchorPane.setLeftAnchor(sEditorPane.getTabPane(), 0.0);

		terminalPane.getStylesheets().add(getClass().getResource("css/terminal.css").toExternalForm());
		terminalPane.getChildren().add(sTerminalPane.getTerminalPane());
		AnchorPane.setTopAnchor(sTerminalPane.getTerminalPane(), 0.0);
		AnchorPane.setRightAnchor(sTerminalPane.getTerminalPane(), 0.0);
		AnchorPane.setBottomAnchor(sTerminalPane.getTerminalPane(), 0.0);
		AnchorPane.setLeftAnchor(sTerminalPane.getTerminalPane(), 0.0);

		workspacePane.setSashWidth(2);
		workspacePane.setWeights(new int[] { 20, 80 });

		subWorkspacePane.setHorizontal(false);
		subWorkspacePane.setSashWidth(1);
		subWorkspacePane.setWeights(new int[] { 65, 35 });

		headBar.setAlignment(Pos.CENTER_LEFT);

		Label label = new Label("LogoBoi");
		headBar.getChildren().add(label);

//		Label label2 = new Label("Tool Bar Boi");
//		toolBar.getChildren().add(label2);

		menuBar = new SMenuBar(mainApp, sEditorPane).getMenuBar();
		headBar.getChildren().add(menuBar);
		menuBar.getStylesheets().add(getClass().getResource("css/menu-bar.css").toExternalForm());

	}

	static void init() {
		sEditorPane.handleNewFile();
	}

	public static void methodupdateInterrupt() {
		methodupdate.interrupt();
	}

}
