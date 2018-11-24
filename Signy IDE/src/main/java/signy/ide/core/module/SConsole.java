package signy.ide.core.module;

import static org.fxmisc.wellbehaved.event.EventPattern.anyOf;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import signy.ide.FXMLDocumentController;
import signy.ide.controls.nodes.SConsoleArea;

public class SConsole {

	private FXMLDocumentController controller;
	final KeyCombination keyCombineCtrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

	private Tab tab;
	private SConsoleArea consoleArea;
	private TextField commandtf;
	private List<String> commandLog = new ArrayList<>();
	private int logPointer = 0;

	private Thread thread;
	private RunnableImpl runnableImpl;
	private Process p;
	private ProcessBuilder pb = new ProcessBuilder();
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

		this.commandtf = new TextField() {
			@Override
			public void copy() {
				endProcess();
				consoleArea.println();
			}
		};
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
				if (ke.getCode() == KeyCode.ENTER) {
					String text = commandtf.getText();
					commandLog.add(text);
					logPointer = commandLog.size();
					commandtf.clear();

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
						endProcess();
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
			if (runnableImpl != null) {
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
		private boolean state = false;

		public ProcessBuilderCommand(SConsoleArea consoleArea, String Command)
				throws InterruptedException, IOException {
			String cmd = EnvSearch(Command);

			if (cmd != null) {
				pb = new ProcessBuilder(cmd);
			} else {
				pb = new ProcessBuilder(EnvSearch("cmd"), "/c", Command);
			}
			pb.redirectErrorStream(true);

			runnableImpl = new RunnableImpl(p, pb, consoleArea);
			thread = new Thread(runnableImpl);
//			thread = new Thread(new Runnable() {
//				private String line;
//
//				@Override
//				public void run() {
////					if(terminate == true) {
////						terminate = false;
////						return;
////					}
//					System.out.println(Thread.currentThread());
//					try {
//						line = null;
//						p = pb.start();
//
//						BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//						while ((line = in.readLine()) != null) {
//							System.out.println(line);
//							consoleArea.println("    " + line);
//						}
//
//						BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//						while ((line = er.readLine()) != null) {
//							System.out.println(" [ERROR] " + line);
//							consoleArea.println(" [ERROR] " + line);
//						}
//						System.out.println("Fin");
//						p.waitFor();
//						state = true;
//					} catch (IOException | InterruptedException e) {
//						consoleArea.println(" [ERROR] " + e.getMessage());
//
//						state = false;
//					} finally {
//						try {
//							p.getInputStream().close();
//							System.out.println("In closed");
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						try {
//							p.getErrorStream().close();
//							System.out.println("Err closed");
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//
//				}
//
//			});
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

	public void netStat() {
		try {
			String[] command = { "CMD", "/C", "netstat -ano |findstr :993 |findstr ESTABLISHED" };
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File("C:/Windows/System32/"));
			pb.redirectErrorStream(true);
			System.out.println(Thread.currentThread());
			Process p = pb.start();
			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = in.readLine()) != null) {
				consoleArea.println("    " + line);
			}

			BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = er.readLine()) != null) {
				consoleArea.println(" [ERROR] " + line);
			}
			System.out.println(Thread.currentThread());
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void endProcess() {
		boolean isInterrupted = false;
		try {
			runnableImpl.doStop();
			while (!runnableImpl.isStop()) {
				try {
					System.out.println("Sleep Main for 100L untill " + thread.getName() + " stop. Is it stopped? : "
							+ runnableImpl.isStop());
					isInterrupted = true;
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(thread.getName() + " Is stop? : " + runnableImpl.isStop());
		} catch (NullPointerException e) {

		} finally {
			try {
				p.destroy();
			} catch (NullPointerException e) {

			}
			try {
				thread.stop();
				thread.destroy();
			} catch (NullPointerException | NoSuchMethodError e) {

			}
		}
		if (isInterrupted == true) {
//			consoleArea.println("^C");
		}
	}

}
