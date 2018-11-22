package signy.ide.core.module;

import java.io.*;
import java.util.ArrayList;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import signy.ide.FXMLDocumentController;
import signy.ide.LoadingController;
import javafx.application.Platform;
import javafx.event.*;

public class SConsole {

	private FXMLDocumentController controller;

	private Tab tab;
	private TextArea textArea;
	private TextField commandtf;

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

		this.tab = new Tab();
		this.textArea = new TextArea();
		this.commandtf = new TextField();
		this.textArea.setEditable(false);
		BorderPane p0 = new BorderPane();
		p0.setCenter(textArea);
		p0.setBottom(commandtf);
		envPaths = getEnvPath();
		this.tab.setContent(p0);
		this.tab.setText(title);
		commandtf.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER) {

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
					String[] input = commandtf.getText().split(" ");

					switch (input[0]) {
					case "getEnv":
						for (String envPath : envPaths) {
							textArea.appendText(envPath + "\n");
						}
						break;
					case "clr":
					case "clear":
						textArea.clear();
						break;
					case "EnvSearch":
						if (input.length > 1) {
							textArea.appendText(EnvSearch(input[1]) != null ? " Found " + EnvSearch(input[1]) + "\n"
									: " Not Found ");
						} else {
							textArea.appendText(" [ERROR] Invalid argument : EnvSearch [argument] \n");
						}
						break;
					case "JavaSearch":
						textArea.appendText(EnvSearch("java") + "\n");
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
					commandtf.setText("");

				}
			}
		});

	}

	public void runCommand(String Command) {

		try {
			if (pbc != null && pbc.isRun()) {
				pbc.stop();
			}
			pbc = new ProcessBuilderCommand(textArea, Command);
		} catch (InterruptedException | IOException e) {
			textArea.appendText(" [ERROR] " + e.getMessage() + "\n");
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

		public ProcessBuilderCommand(TextArea ta, String Command) throws InterruptedException, IOException {
			String cmd = EnvSearch(Command);

			if (cmd != null) {
				pb = new ProcessBuilder(cmd);
			} else {
				pb = new ProcessBuilder(EnvSearch("cmd"), "/c", Command);
			}
			ta.appendText(workingDir + " > " + Command + "\n");

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
							appendText(ta, "    " + line + "\n");
						}

						BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						while ((line = er.readLine()) != null) {
							appendText(ta, " [ERROR] " + line + "\n");
						}
						p.waitFor();
//						p.getInputStream().close(); ปิดไป้ปด้วยสัส
//						p.getErrorStream().close(); ปิดไป้ปด้วยสัส
						state = true;
					} catch (IOException | InterruptedException e) {
						appendText(ta, " [ERROR] " + e.getMessage() + "\n");


						state = false;
					}

				}

			});
			thread.start();

		}

		void appendText(TextArea ta, String text) {
			Platform.runLater(() -> ta.appendText(text));
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
