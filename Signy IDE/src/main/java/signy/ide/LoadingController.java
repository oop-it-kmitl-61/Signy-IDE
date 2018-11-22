package signy.ide;

import java.util.HashMap;

import signy.ide.core.SJavaDevelopmentKit;

public final class LoadingController {

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

}
