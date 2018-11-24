package signy.ide;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lib.org.eclipse.fx.ui.panes.SashPane;
import signy.ide.controls.panes.SEditorPane;
import signy.ide.controls.panes.STerminalPane;
import signy.ide.controls.panes.SViewPane;
import signy.ide.core.module.SConsole;
import signy.ide.core.module.SMenuBar;
import signy.ide.core.module.SOutput;

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

	private static SViewPane viewPane;
	private static SEditorPane editorPane;
	private static STerminalPane terminalPane;

	private static SConsole consolePane;
	private static SOutput outputPane;
	private static String rootDirectory = System.getProperty("user.dir");

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

		
		Label label = new Label("Signy IDE");
		headBar.getChildren().addAll(new ImageView(new Image("signy/ide/resources/icons/top-logo.png", 64, 32, false, true)));
		headBar.getChildren().addAll(label);

//		Label label2 = new Label("Tool Bar Boi");
//		toolBar.getChildren().add(label2);

		menuBar = new SMenuBar(this).getMenuBar();
		headBar.getChildren().add(menuBar);
		menuBar.getStylesheets().add(getClass().getResource("css/menu-bar.css").toExternalForm());

		consolePane = terminalPane.getConsolePane();
		outputPane = terminalPane.getOutputPane();

//		if (LoadingController.isLoadedOneTime() == false) {
//
////			outlineUpdate();
//
//			if (scanJdkInEnvPath() == true) {
////				LoadingController.setJdkPathFound(true);
//			} else {
//				
//
//			}
//
//			LoadingController.setLoadOneTime(true);
//
//		}

	}

	static void init() {
		editorPane.handleNewFile();
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
	
	public String getRootDirectory() {
		return rootDirectory;
	}

	public static boolean scanJdkInEnvPath() {
		try {
			LoadingController.setStateProcess(true);
			LoadingController.setJdkPath();
			while (LoadingController.isStateProcessRunning() == true) {
				try {
					Thread.sleep(150L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
				}
			}
			String s[] = LoadingController.getResults().split("\n");
			String temp = null;
			for (String ss : s) {
				if (ss.contains("jdk")) {
					temp = ss;
				}
			}
			String t[] = temp.split(" ");
			for (String ss : t) {
				if (ss.contains("jdk")) {
					LoadingController.setPath("C:/Program Files/Java/" + ss);
				}
			}
			
		} catch (NullPointerException ex) {
			return false;
		} finally {
		}
		return false;
	}

	static void endProcess() {
		consolePane.endProcess();
	}
	
	public static Process compile(String projectDirectory) {
		Process pro = null;
		try {
			pro = Runtime.getRuntime().exec(LoadingController.getPath() + "/bin/javac -d bin src/*.java", null, new File(projectDirectory));
			
		} catch (IOException e) {
			System.out.println("  Compile Failed! print stacktrace  ");
			for(StackTraceElement a: e.getStackTrace()) {
				consolePane.getConsoleArea().println("  [ERROR]  " + a.toString());				
			}
		}
		return pro;
	}
	
	public static Process run(String projectDirectory, String mainClass) {
		Process pro = null;
		try {
			pro = Runtime.getRuntime().exec(LoadingController.getPath() + "/bin/java " + mainClass, null, new File(projectDirectory+"/bin"));
			
		} catch (IOException e) {
			System.out.println("  Run Failed! print stacktrace  ");
			for(StackTraceElement a: e.getStackTrace()) {
				consolePane.getConsoleArea().println("  [ERROR]  " + a.toString());				
			}
		}
		return pro;
	}
}
