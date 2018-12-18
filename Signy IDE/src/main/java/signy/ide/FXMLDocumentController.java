package signy.ide;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lib.org.eclipse.fx.ui.panes.SashPane;
import signy.ide.controls.nodes.SWelcomeView;
import signy.ide.controls.panes.SEditorPane;
import signy.ide.controls.panes.STerminalPane;
import signy.ide.controls.panes.SViewPane;
import signy.ide.core.module.SConsole;
import signy.ide.core.module.SMenuBar;
import signy.ide.core.module.SOutput;
import signy.ide.core.resources.SProject;
import signy.ide.lang.Lang;
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

	private static SWelcomeView sWelcomeView;

	private boolean check_Side_Panel = true;
	private boolean check_Bottom_Panel = true;

	private static SConsole consolePane;
	private static SOutput outputPane;
	private static String rootDirectory = System.getProperty("user.dir");

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		LoadingController.setController(this);

		Button btnBuild = new Button();
		btnBuild.setGraphic(new ImageView(new Image("icons/go.png", 12, 12, true, false)));
		btnBuild.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (LoadingController.getCurrentProject() != null) {
					consolePane.getCompiler().compile(LoadingController.getCurrentProject());
				}
			}

		});

		Region gap = new Region();
		gap.setPadding(new Insets(16, 0, 0, 0));
		profileBar.getChildren().addAll(gap, btnBuild);

		rootDirectory = LoadingController.getWorkspacePath().toString();
		this.mainApp = Main.getMainApp();

		editorPane = new SEditorPane(this);
		viewPane = new SViewPane(this);
		terminalPane = new STerminalPane(this);

		sWelcomeView = new SWelcomeView();

		viewAnchor.getStylesheets().add("css/tree-view.css");
		viewAnchor.getChildren().add(viewPane.getTabPane());
		AnchorPane.setTopAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setRightAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setBottomAnchor(viewPane.getTabPane(), 0.0);
		AnchorPane.setLeftAnchor(viewPane.getTabPane(), 0.0);

		editorAnchor.getStylesheets().add("css/editor.css");
		editorAnchor.getStylesheets().add("css/document.css");
		editorAnchor.getChildren().add(sWelcomeView.getContentPane());
		AnchorPane.setTopAnchor(sWelcomeView.getContentPane(), 0.0);
		AnchorPane.setRightAnchor(sWelcomeView.getContentPane(), 0.0);
		AnchorPane.setBottomAnchor(sWelcomeView.getContentPane(), 0.0);
		AnchorPane.setLeftAnchor(sWelcomeView.getContentPane(), 0.0);

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
		headBar.getChildren().addAll(new ImageView(new Image("icons/logo512-17.png", 64, 32, false, true)));
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
				SProject sProject = new SProject(projectEntry.getName(), projectEntry.toPath());
				LoadingController.getAllProjects().add(sProject);
				scanProject(sProject, projectEntry);
			}
		}

		Lang.autocompleteAnalyser.start();
		Lang.initAutocomplete(LoadingController.getStage());

	}

	public boolean getCheckSide() {
		return check_Side_Panel;
	}

	public void setCheckSide(boolean setPanel) {
		this.check_Side_Panel = setPanel;
	}

	public boolean getCheckBottom() {
		return check_Bottom_Panel;
	}

	public void setCheckBottom(boolean setPanel) {
		this.check_Bottom_Panel = setPanel;
	}

	private void scanProject(SProject sProject, File entry) {
		if (entry.listFiles() == null) {
			return;
		}
		for (final File fileEntry : entry.listFiles()) {
			if (fileEntry.isDirectory()) {
				scanProject(sProject, fileEntry);
			} else {
				sProject.getFilesSystem().add(fileEntry);
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

	public AnchorPane getEditorAnchor() {
		return editorAnchor;
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

	public SWelcomeView getWelcomeView() {
		return sWelcomeView;
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

	public static Process compile(SProject project) {
		FileUtil.createFolder(project.getPathBin());
		Process pro = null;
		try {
			String[] results = terminalPane.getConsolePane().getAllJavaFilePath().split(System.lineSeparator());
			String needed = LoadingController.getPath() + "/bin/javac -d bin ";
			for (String result : results) {
				needed += " " + result;
			}
			pro = Runtime.getRuntime().exec(needed, null, project);

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
