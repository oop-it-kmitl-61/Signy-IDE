package signy.ide.core.module;

import java.io.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.event.*;

public class SConsole {
	private Tab tab;
	private TextArea textArea;
	private TextField commandtf;
	private ProcessBuilderCommand pbc;

	public SConsole() {
		this("Console");
	}

	public SConsole(String title) {
		this.tab = new Tab();
		this.textArea = new TextArea();
		this.commandtf = new TextField();
		this.textArea.setEditable(false);
		BorderPane p0 = new BorderPane();
		p0.setCenter(textArea);
		p0.setBottom(commandtf);
		this.tab.setContent(p0);
		this.tab.setText("Console");
		commandtf.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER) {

					if (pbc != null && pbc.isRunning()) {
						pbc.stop();
					}

					switch (commandtf.getText()) {
					case "getEnv":
						getEnvPath();
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
			pbc = new ProcessBuilderCommand(textArea, Command);
		} catch (InterruptedException | IOException e) {
			textArea.appendText(" [ERROR] " + e.getMessage() + "\n");
		}

	}

	public Tab getTab() {
		return this.tab;
	}

	public class ProcessBuilderCommand {
		private Process p;
		private ProcessBuilder pb;
		private Thread thread;
		private boolean running = false;

		public ProcessBuilderCommand(TextArea ta, String Command) throws InterruptedException, IOException {
			String cmd = "C:\\Windows\\System32\\cmd.exe";
			pb = new ProcessBuilder(cmd, "/c", Command);
			ta.appendText(System.getProperty("user.dir") + " > " + cmd + " /c " + Command + "\n");

			thread = new Thread(new Runnable() {
				private String line;

				@Override
				public void run() {
					try {
						line = null;
						p = pb.start();

						BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
						while ((line = in.readLine()) != null) {
							ta.appendText("    " + line + "\n");
						}

						BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						while ((line = er.readLine()) != null) {
							ta.appendText(" [ERROR] " + line + "\n");
						}

						p.getInputStream().close();
						p.getErrorStream().close();

						p.waitFor();
						running = true;
					} catch (IOException | InterruptedException e) {
						ta.appendText(" [ERROR] " + e.getMessage() + "\n");

						running = false;
						new SConsole();
					}

				}

			});
			thread.start();

		}

		public void stop() {
			if (running || p.isAlive()) {
				this.running = false;
				p.destroy();
			}
		}

		public boolean isRunning() {
			return this.running;
		}
	}

	public void getEnvPath() {
		String[] envPaths = System.getenv("path").split(";");
		for (String envPath : envPaths) {
			this.textArea.appendText(envPath + "\n");
		}
	}

}
