package signy.ide.core;

import java.io.File;
import java.io.IOException;

import signy.ide.LoadingController;
import signy.ide.core.parserStream.JavacStream;

public class SRun implements Runnable {

	private Thread outputStream, errorStream;

	@Override
	public void run() {

		try {
			LoadingController.setResults(" ");
			Runtime runtime = Runtime.getRuntime();
			System.out.println(LoadingController.getPath() + "/bin/java " + "Demo");
			Process process = runtime.exec(LoadingController.getPath() + "/bin/java " + "Demo", null, new File("C:\\C"));
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
