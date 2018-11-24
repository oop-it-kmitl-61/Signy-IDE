package signy.ide.core.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import signy.ide.FXMLDocumentController;
import signy.ide.LoadingController;
import signy.ide.controls.nodes.SConsoleArea;
import signy.ide.controls.nodes.SOutputArea;

public class SCompiler {
	
	private FXMLDocumentController controller;
	private String rootDir;
	private Thread thread;
	private SOutputArea outputArea;
	private SConsoleArea carea;

	public SCompiler(FXMLDocumentController controller){
		this.controller = controller;
		rootDir = controller.getRootDirectory();
		outputArea = controller.getTerminalPane().getOutputPane().getOutputArea();
		carea = controller.getTerminalPane().getConsolePane().getConsoleArea();
	}
	public boolean compile(){
		return compile(rootDir, LoadingController.getPath());
	}
	public boolean compile(String path){
		return compile(path, LoadingController.getPath());
	}
	public boolean compile(String path, String jdkPath){
		outputArea.clear();
		Process p = FXMLDocumentController.compile(path);
		controller.getTerminalPane().getTabPane().getSelectionModel().select(controller.getTerminalPane().getOutputPane().getTab());
		carea.println("Root directory : " + path);
		carea.println("Java directory : " + jdkPath);
		if(p != null){
			carea.println("compiling . . .");
			thread = new Thread(new JavacStreamController(p, outputArea));
			thread.start();
			return true;
		}
		carea.println("failed");
		return false;
	}
	
	public boolean run(){
		return run("example");
	}
	
	public boolean run(String mainClass){
		return run(mainClass, controller.getRootDirectory());
	}
	
	public boolean run(String mainClass, String path){
		return run(mainClass, path, LoadingController.getPath());
	}
	public boolean run(String mainClass, String path, String jdkPath){
		outputArea.clear();
		Process p = FXMLDocumentController.run(path, mainClass);
		
		carea.println("Root directory : " + path);
		carea.println("Java directory : " + jdkPath);
		if(p != null){
			carea.println("compiling . . .");
			thread = new Thread(new JavacStreamController(p, outputArea));
			thread.start();
			return true;
		}
		carea.println("failed");
		return false;
	}
	
	private class JavacStreamController implements Runnable {

		private boolean isStop = false;
		private boolean doStop = false;
		private Process p;
		private SOutputArea area;

		public synchronized boolean isStop() {
			return isStop;
		}

		public synchronized void doStop() {
			doStop = true;
			p.destroy();
		}

		private synchronized boolean keepRunning() {
			return doStop == false;
		}

		JavacStreamController(Process p, SOutputArea area) {
			this.p = p;
			this.area = area;
			
		}

		@Override
		public void run() {
			boolean isError = false;
			controller.getTerminalPane().getTabPane().getSelectionModel().select(controller.getTerminalPane().getOutputPane().getTab());
			while (keepRunning()) {
				System.out.println("At run : " + Thread.currentThread().getName() + " is running");
				try {
					String line = null;

					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = in.readLine()) != null && keepRunning()) {
						area.println("    " + line);
					}

					BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					while ((line = er.readLine()) != null && keepRunning()) {
						area.println(" [ERROR] " + line);
						isError = true;
					}
					p.waitFor();
				} catch (IOException | InterruptedException e) {
					area.println(" [ERROR] " + e.getMessage());
					isError = true;
				} finally {
					try {
						p.getInputStream().close();
						System.out.println("At run : InputStream closed");
					} catch (IOException e) {
						area.println(" [ERROR] " + e.getMessage());
						isError = true;
					}
					try {
						p.getErrorStream().close();
						System.out.println("At run : ErrorStream closed");
					} catch (IOException e) {
						area.println(" [ERROR] " + e.getMessage());
						isError = true;
					}
					doStop();
				}

			}
			carea.println((!isError)? "OK" :"Fail");
			System.out.println("At run : " + Thread.currentThread().getName() + " is stopped");
			isStop = true;
		}
	}
}
