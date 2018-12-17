package signy.ide;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import signy.ide.core.SJavaDevelopmentKit;
import signy.ide.core.resources.Project;
import signy.ide.settings.PropertySetting;

public final class LoadingController {

	private static Path workspacePath;

	private static ArrayList<Project> projects = new ArrayList<>();

	private static Project currentProject;

	private static Stage stage;

	private static String consolePath;

	private static String jdkPath;

	private static boolean jdkPathFound = false;

	private static boolean stateProcessRun = false;

	private static String results = "";

	private static String mainClass = "";

	private static boolean oneTimeLoad = false;

	private static String className;

	private static String intername;

	private static HashMap<String, String> classCode = new HashMap<>();

	private static HashMap<String, String> inter = new HashMap<>();

	private static String args = "";

	private static Thread backgroundThread;

	public LoadingController() {

	}

	public static void setWorkspacePath(Path path) {
		workspacePath = path;
	}

	public static Path getWorkspacePath() {
		return workspacePath;
	}

	public static ArrayList<Project> getAllProjects() {
		return projects;
	}

	public synchronized static void setCurrentProject(Project project) {
		currentProject = project;
	}

	public synchronized static Project getCurrentProject() {
		return currentProject;
	}

	public synchronized static Stage getStage() {
		return stage;
	}

	public synchronized static void setStage(Stage stage) {
		LoadingController.stage = stage;
	}

	public synchronized static void setJdkPath() {
		backgroundThread = new Thread(new SJavaDevelopmentKit());
		backgroundThread.start();
	}

	public static void setInter(String key, String value) {
		inter.put(key, value);
	}

	public static HashMap<String, String> getInter() {
		return inter;
	}

	public static String getArgs() {
		return args;
	}

	public synchronized static String getClassName() {
		return className;
	}

	public synchronized static void setClassName(String className) {
		LoadingController.className = className;
	}

	public synchronized static boolean isLoadedOneTime() {
		return oneTimeLoad;
	}

	public synchronized static void setLoadOneTime(boolean oneTimeLoad) {
		LoadingController.oneTimeLoad = oneTimeLoad;
	}

	public synchronized static String getMainClass() {
		return mainClass;
	}

	public synchronized static void setMainClass(String s) {
		mainClass = s;
	}

	public synchronized static boolean isStateProcessRunning() {
		return stateProcessRun;
	}

	public synchronized static void setStateProcess(boolean run) {
		stateProcessRun = run;
	}

	public synchronized static HashMap<String, String> getClassCode() {
		return classCode;
	}

	public synchronized static void setClassCode(String key, String value) {
		classCode.put(key, value);
	}

	public synchronized static String getResults() {
		return results;
	}

	public synchronized static void setResults(String results) {
		LoadingController.results = results;
	}

	public synchronized static void appendResults(String results) {
		LoadingController.results += results;
	}

	public static String getConsole() {
		return null;
	}

	public static String getPath() {
		if (jdkPath == null) {
			FXMLDocumentController.scanJdkInEnvPath();
		}
		return jdkPath;
	}

	public static void setPath(String string) {
		jdkPath = string;

	}

}
