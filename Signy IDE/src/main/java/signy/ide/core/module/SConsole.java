package signy.ide.core.module;

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.TAB;
import static org.fxmisc.wellbehaved.event.EventPattern.anyOf;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import signy.ide.FXMLDocumentController;
import signy.ide.controls.nodes.SConsoleArea;
import javafx.event.*;

public class SConsole {

	private FXMLDocumentController controller;
	final KeyCombination keyCombineCtrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

	private Tab tab;
	private SConsoleArea consoleArea;
	private TextField commandtf;
	private List<String> commandLog = new ArrayList<>();
	private int logPointer = 0;

	private Thread thread;
	private Process p;
	private ProcessBuilder pb;
	private ProcessBuilderCommand pbc = null;

	private String workingDir = System.getProperty("user.dir");
	private ArrayList<String> envPaths;

	public SConsole(FXMLDocumentController controller) {
		this(controller, "CONSOLE");
	}

	public SConsole(FXMLDocumentController controller, String title) {

		this.controller = controller;
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
		InputMap<Event> prevent = InputMap.consume(
				anyOf(
						keyPressed(KeyCode.UP),
						keyPressed(KeyCode.DOWN)
				));
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
				if (ke.getCode() == KeyCode.ENTER) {
					String text = commandtf.getText();
					commandLog.add(text);
					logPointer = commandLog.size();

//					if(pbc.isRun()) {
//						try {
//							thread.wait();
//							pbc.stop();
//							p.wait();
//							p.destroy();
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							System.out.println(e.getMessage());
//						}
//					}
					String[] input = text.split(" ");

					switch (input[0]) {
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
						pbc.stop();
						break;
					default:
						runCommand(commandtf.getText());
						break;
					}
					commandtf.clear();

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
				}
			}
		});

	}

	public void requestFocus() {
		commandtf.requestFocus();
	}

	public void runCommand(String Command) {

		try {
			if (pbc != null && pbc.isRun()) {
				pbc.stop();
			}
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
		private boolean state = false;

		public ProcessBuilderCommand(SConsoleArea consoleArea, String Command)
				throws InterruptedException, IOException {
			String cmd = EnvSearch(Command);

			if (cmd != null) {
				pb = new ProcessBuilder(cmd);
			} else {
				pb = new ProcessBuilder(EnvSearch("cmd"), "/c", Command);
			}
			consoleArea.println(workingDir + " > " + Command);

			thread = new Thread(new Runnable() {
				private String line;

				@Override
				public void run() {
//					if(terminate == true) {
//						terminate = false;
//						return;
//					}
					System.out.println(Thread.currentThread());
					try {
						line = null;
						p = pb.start();

						BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
						while ((line = in.readLine()) != null) {
							consoleArea.println("    " + line);
						}

						BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						while ((line = er.readLine()) != null) {
							consoleArea.println(" [ERROR] " + line);
						}
						p.waitFor();
//						p.getInputStream().close(); ปิดไป้ปด้วยสัส
//						p.getErrorStream().close(); ปิดไป้ปด้วยสัส
						state = true;
					} catch (IOException | InterruptedException e) {
						consoleArea.println(" [ERROR] " + e.getMessage());

						state = false;
					}

				}

			});
			thread.start();

		}

		public void stop() {
			try {
				p.destroy();
				this.state = false;
			} catch (Exception e) {
				this.state = true;
			}

		}

		public boolean isRun() {
			return this.state;
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

}
