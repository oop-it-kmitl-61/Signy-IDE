package signy.ide.core.module;

import java.io.*;
import java.util.ArrayList;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.event.*;

public class SConsoleTab {
	private Tab tab;
	private TextArea textArea;
	private TextField commandtf;
	private String content = "";
	private Process p;
	private ProcessBuilder builder;
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
		commandtf.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER) {
					runCommand(commandtf.getText());
				}

			}
		});
		this.tab.setText(title);
		this.textArea.setText(content);

	}

	public void runCommand(String Command) {
		try {
			String line = null;
			builder = new ProcessBuilder("cmd.exe", "/c", Command);
			builder.redirectErrorStream(true);
			p = builder.start();
			content += "\n > " + Command +" \n ";

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = in.readLine()) != null) {
				content += line + "\n";
			}

			BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = er.readLine()) != null) {
				content += line + "\n";
			}

			p.waitFor();
			p.getInputStream().close();
			p.getErrorStream().close();
		} catch (IOException | InterruptedException e) {
			content += e.getMessage();
		} finally {
			
			p.destroy();
			commandtf.setText("");
			textArea.setText(content);
		}
	}

	public Tab getTab() {
		return this.tab;
	}

}
