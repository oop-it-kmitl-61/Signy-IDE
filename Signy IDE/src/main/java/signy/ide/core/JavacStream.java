package signy.ide.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.concurrent.Task;
import signy.ide.LoadingController;

public class JavacStream extends Task<Object> {

	private InputStream in;
	private String type;

	public JavacStream(InputStream input, String type) {
		this.in = input;
		this.type = type;
	}

	@Override
	protected Object call() throws Exception {
		try {
			synchronized (this) {
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					LoadingController.setResults(LoadingController.getResults() + line + "\n");
				}
				LoadingController.setStateProcess(false);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}