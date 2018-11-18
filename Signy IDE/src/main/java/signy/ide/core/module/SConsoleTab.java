package signy.ide.core.module;

import java.io.*;

import javax.swing.JTextArea;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.event.*;

public class SConsoleTab {
	private Tab tab;
	private TextArea textArea;
	private TextField commandtf;
	private ProcessBuilderCommand pbc;
	
	public SConsoleTab() {
		this("Console");
	}

	public SConsoleTab(String title) {
		this.tab = new Tab();
		this.textArea = new TextArea();
		this.commandtf = new TextField();
		this.textArea.setEditable(false);
		BorderPane p0 = new BorderPane();
		p0.setCenter(textArea);
		p0.setBottom(commandtf);
		this.tab.setContent(p0);
		this.tab.setText(title);
		commandtf.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER) {
					
					if(pbc != null && pbc.isRunning()) {
						pbc.stop();
					}
					runCommand(commandtf.getText());
					tab.setText(commandtf.getText());
					commandtf.setText("");
				}
			}
		});
		

	}

	public void runCommand(String Command) {
		try {
			pbc = new ProcessBuilderCommand(textArea, Command);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
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
			pb = new ProcessBuilder(Command);
			
			ta.appendText(System.getProperty("user.dir") + " > " + Command + "\n");
			this.running = true;
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						String line = null;
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
					} catch (IOException | InterruptedException e) {
						System.out.println(e.getMessage());
					}
					
				}
				
			});
			thread.start();
			
		}
		public void stop() {
			p.destroy();
			this.running = false;
		}
		
		public boolean isRunning() {
			return this.running;
		}
	}

}
