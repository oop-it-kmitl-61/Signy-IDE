package signy.ide.core.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class SNetstat {
	private Tab tab;
	private TextArea textArea;
	private String content = "";
	private Button update = new Button("Update");
	private Process p;
	private ProcessBuilder builder;
	final String cmd = "netstat -ano";

	public SNetstat() {
		this.tab = new Tab();
		this.textArea = new TextArea();
		this.textArea.setEditable(false);
		BorderPane p0 = new BorderPane();
		p0.setCenter(textArea);
		p0.setTop(update);
		this.tab.setContent(p0);
		this.tab.setText("NetStat");
		runCommand();
		this.textArea.setText(content);
		update.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (event.getTarget().toString().contains("Update")) {
					runCommand();
				}
				
			}
		});

	}

	public void runCommand() {
		content = "";
		try {
			String line = null;
			builder = new ProcessBuilder("cmd.exe", "/c", "netstat -ano");
			builder.redirectErrorStream(true);
			p = builder.start();
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
			textArea.setText(content);
		}
	}
	
	public Tab getTab() {
		return this.tab;
	}

}
