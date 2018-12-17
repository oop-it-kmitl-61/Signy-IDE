package signy.ide.core.module;

import static org.fxmisc.wellbehaved.event.EventPattern.anyOf;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import signy.ide.FXMLDocumentController;
import signy.ide.LoadingController;
import signy.ide.controls.nodes.SConsoleArea;
import signy.ide.core.resources.Project;
import signy.ide.lang.Lang;

public class SConsole {

	private FXMLDocumentController controller;
	final KeyCombination keyCombineCtrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

	private Tab tab;
	private SConsoleArea consoleArea;
	private TextField commandtf;
	private SCompiler compiler;
	private List<String> commandLog = new ArrayList<>();
	private int logPointer = 0;

	private Thread thread;
	private StreamController streamController;
	private Process p;
	private ProcessBuilder pb = new ProcessBuilder();
	private ProcessBuilderCommand pbc = null;

	private String workingDir;
	private String mainClass = "example";
	private ArrayList<String> envPaths;

	public SConsole(FXMLDocumentController controller) {
		this(controller, "CONSOLE");
	}

	public SConsole(FXMLDocumentController controller, String title) {

		this.controller = controller;
		this.workingDir = controller.getRootDirectory();
		this.tab = new Tab();
		this.consoleArea = new SConsoleArea();

		ScaledVirtualized<SConsoleArea> scaleVirtualized = new ScaledVirtualized<>(consoleArea);
		consoleArea.addEventFilter(ScrollEvent.ANY, e -> {
			if (e.isControlDown()) {
				double scaleAmount = 0.9;
				Scale zoom = scaleVirtualized.getZoom();

				double scale = e.getDeltaY() < 0 ? zoom.getY() * scaleAmount : zoom.getY() / scaleAmount;
				zoom.setX(scale);
				zoom.setY(scale);
			}
		});

		this.commandtf = new TextField();
//		this.commandtf = new TextField() {
//			@Override
//			public void copy() {
//				endProcess();
//				consoleArea.println();
//			}
//		};
		InputMap<Event> prevent = InputMap
				.consume(anyOf(keyPressed(KeyCode.UP), keyPressed(KeyCode.DOWN), keyPressed(KeyCode.ESCAPE)));
		Nodes.addInputMap(commandtf, prevent);

		BorderPane p0 = new BorderPane();
		VirtualizedScrollPane<ScaledVirtualized<SConsoleArea>> virtualizedScrollPane = new VirtualizedScrollPane<>(
				scaleVirtualized);
		p0.setCenter(virtualizedScrollPane);
		p0.setBottom(commandtf);
		envPaths = getEnvPath();
		this.tab.setContent(p0);
		this.tab.setText(title);

		commandtf.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (compiler == null) {
					compiler = new SCompiler(controller);
				}
				if (keyCombineCtrlC.match(ke)) {
					endProcess();
					consoleArea.println("\n    Terminated\n");
				}
				if (ke.getCode() == KeyCode.ENTER) {
					String text = commandtf.getText();
					commandLog.add(text);
					logPointer = commandLog.size();
					commandtf.clear();

					String[] input = text.split(" ");

					switch (input[0]) {
					case "cd":
						try {
							File f = new File(workingDir + "/" + text.substring(3));
							if (f.exists()) {
								workingDir = f.getCanonicalPath();
								consoleArea.println(workingDir);
							} else {
								consoleArea.println("\n  [ERROR]  File or directory does not exists.");
							}

						} catch (Exception e) {
							if (e.toString().contains("String index out of range")) {
								consoleArea.println("Please enter following this command cd [path]");
							} else {
								consoleArea.println(e.getMessage());
							}
						}

						break;
					case "getEnv":
						for (String envPath : envPaths) {
							consoleArea.println(envPath);
						}
						break;
					case "clr":
					case "clear":
						consoleArea.clear();
						break;
					case "EnvSearch":
						if (input.length > 1) {
							consoleArea.println(
									EnvSearch(input[1]) != null ? " Found " + EnvSearch(input[1]) : " Not Found ");
						} else {
							consoleArea.println(" [ERROR] Invalid argument : EnvSearch [argument]");
						}
						break;
					case "JavaSearch":
						consoleArea.println(EnvSearch("java"));
						break;
					case "killProcess":
					case "KillProcess":
					case "EndProc":
						endProcess();
						break;
					case "jdk":
						FXMLDocumentController.scanJdkInEnvPath();
						consoleArea.println(LoadingController.getPath());
						break;
					case "compile":
						System.out.println(LoadingController.getCurrentProject());
						compiler.compile(workingDir + "/" + LoadingController.getCurrentProject().getTitle());
						break;
					case "run":
						if (input[1] != null && input[1].length() > 0) {
							compiler.run(input[1], workingDir + "/" + LoadingController.getCurrentProject().getTitle());
						}
						break;
					default:
						runCommand(text);
						break;
					}

				} else if (ke.getCode() == KeyCode.UP) {
					if (logPointer == 0) {
						return;
					}
					SConsoleArea.runLater(() -> {
						commandtf.setText(commandLog.get(--logPointer));
						commandtf.positionCaret(commandtf.getLength());
					});
				} else if (ke.getCode() == KeyCode.DOWN) {
					if (logPointer >= commandLog.size() - 1) {
						return;
					}
					SConsoleArea.runLater(() -> {
						commandtf.setText(commandLog.get(++logPointer));
						commandtf.positionCaret(commandtf.getLength());
					});
				} else if (ke.getCode() == KeyCode.ESCAPE) {
					commandtf.setText("");
				}
			}
		});

		init();
	}

	private void init() {
		try {
			pbc = new ProcessBuilderCommand(consoleArea, "cmd");

		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	public void requestFocus() {
		commandtf.requestFocus();
	}

	public void runCommand(String Command) {

		try {

			if (thread != null) {
				System.out.println("At Main : " + thread.getName() + " will execute " + "'" + Command + "'");
			}
			if (streamController != null) {
				endProcess();
			}
			if (thread != null) {
				System.out.println("At Main : Before do next command, RunnableThread is alive? " + thread.isAlive());
			}
			if (p != null) {
				System.out.println("At Main : Before do next command, Process is alive? " + p.isAlive());
			}

			consoleArea.println(workingDir + " > " + Command);
			pbc = new ProcessBuilderCommand(consoleArea, Command);
		} catch (InterruptedException | IOException e) {
			consoleArea.println(" [ERROR] " + e.getMessage());
		}

	}

	public Tab getTab() {
		return this.tab;
	}

	public String EnvSearch(String command) {
		envPaths = getEnvPath();
		File file;
		for (String envPath : envPaths) {
			file = new File(envPath + "\\" + command + ".exe");
			if (file.exists()) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}

	private class ProcessBuilderCommand {

		public ProcessBuilderCommand(SConsoleArea consoleArea, String Command)
				throws InterruptedException, IOException {
			String cmd = EnvSearch(Command);

			if (cmd != null) {
				pb = new ProcessBuilder(cmd);
			} else {
				pb = new ProcessBuilder(EnvSearch("cmd"), "/c", Command);
			}
			/*-----  DON'T DELETE THIS LINE  -----*/
			pb.redirectErrorStream(true);

			pb.directory(new File(workingDir));
			streamController = new StreamController(p, pb, consoleArea);
			thread = new Thread(streamController);
			thread.start();

		}

	}

	public ArrayList<String> getEnvPath() {
		ArrayList<String> envList = new ArrayList<String>();
		String[] envPaths = System.getenv("path").split(";");
		for (String envPath : envPaths) {
			envList.add(envPath);
		}
		return envList;
	}

	@SuppressWarnings("deprecation")
	public void endProcess() {
		boolean isInterrupted = false;
		try {
			streamController.doStop();
			while (!streamController.isStop()) {
				try {
					System.out.println("Sleep \"Main\" for 100L untill " + thread.getName() + " stop. Is it stopped? : "
							+ streamController.isStop());
					isInterrupted = true;
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			}
			System.out.println(thread.getName() + " Is stop? : " + streamController.isStop());

		} catch (NullPointerException e) {

		} finally {
			try {
				p.destroy();
			} catch (NullPointerException e) {

			}
			try {
				thread.stop();
			} catch (NullPointerException | NoSuchMethodError e) {

			}
		}
	}

	public SConsoleArea getConsoleArea() {
		return this.consoleArea;
	}

}
