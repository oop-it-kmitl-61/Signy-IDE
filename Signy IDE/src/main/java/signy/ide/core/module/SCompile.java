package signy.ide.core.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import signy.ide.FXMLDocumentController;
import signy.ide.LoadingController;
import signy.ide.controls.nodes.SConsoleArea;

public class SCompile {
	
	private FXMLDocumentController controller;
	private String rootDir;
	private Thread thread;
	private SConsoleArea outputArea;

	public SCompile(FXMLDocumentController controller){
		this.controller = controller;
		rootDir = controller.getRootDirectory();
		outputArea = controller.getTerminalPane().getConsolePane().getConsoleArea();
		
	}
	public boolean compile(){
		return compile(rootDir, LoadingController.getPath());
	}
	public boolean compile(String path){
		return compile(path, LoadingController.getPath());
	}
	public boolean compile(String path, String jdkPath){
		Process p = FXMLDocumentController.compile(path);
		outputArea.println("Root directory : " + path);
		outputArea.println("Java directory : " + jdkPath);
		if(p != null){
			outputArea.println("compiling . . .");
			thread = new Thread(new JavacStreamController(p, outputArea));
			thread.start();
			outputArea.println("compile success");
			return true;
		}
		outputArea.println("compile failed");
		return false;
	}
	
	private class JavacStreamController implements Runnable {

		private boolean isStop = false;
		private boolean doStop = false;
		private Process p;
		private SConsoleArea area;

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

		JavacStreamController(Process p, SConsoleArea area) {
			this.p = p;
			this.area = area;
		}

		@Override
		public void run() {
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
					}
					p.waitFor();
				} catch (IOException | InterruptedException e) {
					area.println(" [ERROR] " + e.getMessage());

				} finally {
					try {
						p.getInputStream().close();
						System.out.println("At run : InputStream closed");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						p.getErrorStream().close();
						System.out.println("At run : ErrorStream closed");
					} catch (IOException e) {
						e.printStackTrace();
					}
					doStop();
				}

			}
			System.out.println("At run : " + Thread.currentThread().getName() + " is stopped");
			isStop = true;
		}
	}
}
