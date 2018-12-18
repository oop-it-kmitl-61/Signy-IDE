package signy.ide.core.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import signy.ide.FXMLDocumentController;
import signy.ide.LoadingController;
import signy.ide.controls.nodes.SConsoleArea;
import signy.ide.controls.nodes.SOutputArea;
import signy.ide.core.resources.SProject;

public class SCompiler {

	private FXMLDocumentController controller;
	private String rootDir;
	private Thread thread;

	public SCompiler(FXMLDocumentController controller) {
		this.controller = controller;
		rootDir = controller.getRootDirectory();
	}

	public boolean compile() {
		return compile(LoadingController.getCurrentProject(), LoadingController.getPath());
	}

	public boolean compile(SProject project) {
		return compile(project, LoadingController.getPath());
	}

	public boolean compile(SProject project, String jdkPath) {
		LoadingController.getController().getTerminalPane().getOutputPane().getOutputArea().clear();
		SConsoleArea carea = LoadingController.getController().getTerminalPane().getConsolePane().getConsoleArea();
		Process p = FXMLDocumentController.compile(project);
		controller.getTerminalPane().getTabPane().getSelectionModel()
				.select(controller.getTerminalPane().getOutputPane().getTab());
		carea.println("Root directory : " + project.getPath().toString());
		carea.println("Java directory : " + jdkPath);
		if (p != null) {
			carea.println("compiling . . .");
			thread = new Thread(new JavacStreamController(p,
					LoadingController.getController().getTerminalPane().getOutputPane().getOutputArea()));
			thread.start();
			return true;
		}
		carea.println("failed");
		return false;
	}

	public boolean run(SProject project) {
		return run(project, controller.getRootDirectory());
	}

	public boolean run(SProject project, String path) {
		return run(project, path, LoadingController.getPath());
	}

	public boolean run(SProject project, String path, String jdkPath) {
		LoadingController.getController().getTerminalPane().getOutputPane().getOutputArea().clear();
		Process p = FXMLDocumentController.run(path, project);

		SConsoleArea carea = LoadingController.getController().getTerminalPane().getConsolePane().getConsoleArea();
		carea.println("Root directory : " + path);
		carea.println("Java directory : " + jdkPath);
		if (p != null) {
			carea.println("compiling . . .");
			thread = new Thread(new JavacStreamController(p,
					LoadingController.getController().getTerminalPane().getOutputPane().getOutputArea()));
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

			SConsoleArea carea = LoadingController.getController().getTerminalPane().getConsolePane().getConsoleArea();
			boolean isError = false;
			controller.getTerminalPane().getTabPane().getSelectionModel()
					.select(controller.getTerminalPane().getOutputPane().getTab());
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

			carea.println((!isError) ? "OK" : "Fail");
			System.out.println("At run : " + Thread.currentThread().getName() + " is stopped");
			isStop = true;
		}
	}
}
