package signy.ide;

import java.util.HashMap;

import signy.ide.core.SBuild;
import signy.ide.core.SJavaDevelopmentKit;
import signy.ide.core.SRun;

public final class LoadingController {

	private static String console = "cmd /C dir C:\\\"Program Files\"\\Java\\*";

	private static String path = "";

	private static boolean jdkPathFound = false;

	private static boolean stateProcessRun = false;

	private static String results = "";

	private static Thread buildAndRunThread;

	private static String mainClass = "";

	private static boolean oneTimeLoad = false;

	private static String className;

	private static String intername;

	private static HashMap<String, String> classCode = new HashMap<>();

	private static HashMap<String, String> inter = new HashMap<>();

	private static String args = "";

	public LoadingController() {

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

	public synchronized static void setBuild() {
		buildAndRunThread = new Thread(new SBuild());
		buildAndRunThread.start();
	}

	public synchronized static void setRun() {
		buildAndRunThread = new Thread(new SRun());
		buildAndRunThread.start();
	}

	public synchronized static void setJdkPath() {
		buildAndRunThread = new Thread(new SJavaDevelopmentKit());
		buildAndRunThread.start();
	}

	public synchronized static void setJdkPathFound(boolean found) {
		jdkPathFound = found;
	}

	public synchronized static String getConsole() {
		return console;
	}

	public synchronized static String getPath() {
		return path;
	}

	public synchronized static void setPath(String path) {
		LoadingController.path = path;
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

}
