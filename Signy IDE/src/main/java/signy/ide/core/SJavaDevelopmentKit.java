package signy.ide.core;

import java.io.IOException;

public class SJavaDevelopmentKit implements Runnable {

	private Thread outputStream, errorStream;

	@Override
	public void run() {

		try {

			Runtime runtime = Runtime.getRuntime();
			Process process;
			process = runtime.exec("cmd /C dir C:\\\"Program Files\"\\Java\\*");
			JavacStream errorstream = new JavacStream(process.getErrorStream(), "error");
			JavacStream outputstream = new JavacStream(process.getInputStream(), "output");
			errorStream = new Thread(errorstream);
			outputStream = new Thread(outputstream);
			errorStream.start();
			outputStream.start();

			process.waitFor();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
