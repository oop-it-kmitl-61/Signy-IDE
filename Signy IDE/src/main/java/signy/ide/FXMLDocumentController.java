package signy.ide;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lib.org.eclipse.fx.ui.panes.SashPane;
import signy.ide.controls.panes.SEditorPane;
import signy.ide.controls.panes.STerminalPane;
import signy.ide.controls.panes.SViewPane;
import signy.ide.core.module.SConsole;
import signy.ide.core.module.SMenuBar;
import signy.ide.core.module.SOutput;
import signy.ide.core.resources.Project;
import signy.ide.lang.Lang;
import signy.ide.settings.PropertySetting;
import signy.ide.utils.FileUtil;

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

		rootDirectory = LoadingController.getWorkspacePath().toString();
		this.mainApp = Main.getMainApp();

		editorPane = new SEditorPane(this);
		viewPane = new SViewPane(this);
		terminalPane = new STerminalPane(this);

		viewAnchor.getStylesheets().add("css/tree-view.css");
		viewAnchor.getChildren().add(viewPane.getTabPane());
		AnchorPane.setTopAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setRightAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setBottomAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setLeftAnchor(viewPane.getTabPane(), 0.0);

		editorAnchor.getStylesheets().add("css/editor.css");
		editorAnchor.getStylesheets().add("css/document.css");
		editorAnchor.getChildren().add(editorPane.getTabPane());
		AnchorPane.setTopAnchor(editorPane.getTabPane(), 0.0);
		AnchorPane.setRightAnchor(editorPane.getTabPane(), 0.0);
		AnchorPane.setBottomAnchor(editorPane.getTabPane(), 0.0);
		AnchorPane.setLeftAnchor(editorPane.getTabPane(), 0.0);

		terminalAnchor.getStylesheets().add("css/terminal.css");
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
		headBar.getChildren()
				.addAll(new ImageView(new Image("icons/top-logo.png", 64, 32, false, true)));
		headBar.getChildren().addAll(label);

		menuBar = new SMenuBar(this).getMenuBar();
		headBar.getChildren().add(menuBar);
		menuBar.getStylesheets().add("css/menu-bar.css");

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

		for (final File projectEntry : LoadingController.getWorkspacePath().toFile().listFiles()) {
			if (Files.exists(Paths.get(projectEntry.getAbsolutePath(), ".project"))) {
				Project project = new Project(projectEntry.getName(), projectEntry.toPath());
				LoadingController.getAllProjects().add(project);
				scanProject(project, projectEntry);
			}
		}

		Lang.autocompleteAnalyser.start();
		Lang.initAutocomplete(LoadingController.getStage());

	}

	private void scanProject(Project project, File entry) {
		if (entry.listFiles() == null) {
			return;
		}
		for (final File fileEntry : entry.listFiles()) {
			if (fileEntry.isDirectory()) {
				scanProject(project, fileEntry);
			} else {
				project.getFilesSystem().add(fileEntry);
			}
		}
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
			pro = Runtime.getRuntime().exec(LoadingController.getPath() + "/bin/javac -d bin src/*.java", null,
					new File(projectDirectory));

		} catch (IOException e) {
			System.out.println("  Compile Failed! print stacktrace  ");
			for (StackTraceElement a : e.getStackTrace()) {
				consolePane.getConsoleArea().println("  [ERROR]  " + a.toString());
			}
		}
		return pro;
	}

	public static Process run(String projectDirectory, String mainClass) {
		Process pro = null;
		try {
			pro = Runtime.getRuntime().exec(LoadingController.getPath() + "/bin/java " + mainClass, null,
					new File(projectDirectory + "/bin"));

		} catch (IOException e) {
			System.out.println("  Run Failed! print stacktrace  ");
			for (StackTraceElement a : e.getStackTrace()) {
				consolePane.getConsoleArea().println("  [ERROR]  " + a.toString());
			}
		}
		return pro;
	}
}
