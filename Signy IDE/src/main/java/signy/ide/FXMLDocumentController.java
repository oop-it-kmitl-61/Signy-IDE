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
	private AnchorPane viewAnchor, editorAnchor, terminalAnchor;

	private Main mainApp;
	private MenuBar menuBar;
	private SViewPane viewPane;
	private static SEditorPane editorPane;
	private STerminalPane terminalPane;
	private static Thread methodupdate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.mainApp = Main.getMainApp();

		editorPane = new SEditorPane(this);
		viewPane = new SViewPane(this);
		terminalPane = new STerminalPane(this);

		viewAnchor.getStylesheets().add(getClass().getResource("css/tree-view.css").toExternalForm());
		viewAnchor.getChildren().add(viewPane.getTabPane());
		AnchorPane.setTopAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setRightAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setBottomAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setLeftAnchor(viewPane.getTabPane(), 0.0);

		editorAnchor.getStylesheets().add(getClass().getResource("css/editor.css").toExternalForm());
		editorAnchor.getStylesheets().add(getClass().getResource("css/document.css").toExternalForm());
		editorAnchor.getChildren().add(editorPane.getTabPane());
		AnchorPane.setTopAnchor(editorPane.getTabPane(), 0.0);
		AnchorPane.setRightAnchor(editorPane.getTabPane(), 0.0);
		AnchorPane.setBottomAnchor(editorPane.getTabPane(), 0.0);
		AnchorPane.setLeftAnchor(editorPane.getTabPane(), 0.0);

		terminalAnchor.getStylesheets().add(getClass().getResource("css/terminal.css").toExternalForm());
		terminalAnchor.getChildren().add(terminalPane.getTerminalPane());
		AnchorPane.setTopAnchor(terminalPane.getTerminalPane(), 0.0);
		AnchorPane.setRightAnchor(terminalPane.getTerminalPane(), 0.0);
		AnchorPane.setBottomAnchor(terminalPane.getTerminalPane(), 0.0);
		AnchorPane.setLeftAnchor(terminalPane.getTerminalPane(), 0.0);

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

		menuBar = new SMenuBar(this).getMenuBar();
		headBar.getChildren().add(menuBar);
		menuBar.getStylesheets().add(getClass().getResource("css/menu-bar.css").toExternalForm());

	}

	static void init() {
		editorPane.handleNewFile();
	}

	public static void methodupdateInterrupt() {
		methodupdate.interrupt();
	}

	public Main getMainApp() {
		return mainApp;
	}

	public SashPane getWorkspacePane() {
		return workspacePane;
	}

	public SashPane getSubWorkspacePane() {
		return subWorkspacePane;
	}

	public SViewPane getViewPane() {
		return viewPane;
	}

	public SEditorPane getEditorPane() {
		return editorPane;
	}

	public STerminalPane getTerminalPane() {
		return terminalPane;
	}

}
