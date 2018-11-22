package signy.ide.core;

import java.io.IOException;

import signy.ide.LoadingController;
import signy.ide.core.parserStream.JavacStream;

public class SJavaDevelopmentKit implements Runnable {

	private Thread outputStream, errorStream;

	@Override
	public void run() {

		try {

			Runtime runtime = Runtime.getRuntime();
			Process process;
			process = runtime.exec(LoadingController.getConsole());
			JavacStream errorstream = new JavacStream(process.getErrorStream(), "error");
			JavacStream outputstream = new JavacStream(process.getInputStream(), "Output");
			errorStream = new Thread(errorstream);
			outputStream = new Thread(outputstream);
			errorStream.start();
			outputStream.start();

			int exitval = process.waitFor();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
