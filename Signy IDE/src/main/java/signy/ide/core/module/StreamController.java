package signy.ide.core.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import signy.ide.LoadingController;
import signy.ide.controls.nodes.SConsoleArea;

class StreamController implements Runnable {

	private boolean isStop = false;
	private boolean doStop = false;
	private Process p;
	private ProcessBuilder pb;
	private SConsoleArea area;

	public synchronized boolean isStop() {
		return isStop;
	}

	public synchronized void doStop() {
		doStop = true;
		p.destroy();
	}

	private synchronized boolean keepRunning() {
		return doStop == false;
	}

	StreamController(Process p, ProcessBuilder pb, SConsoleArea area) {
		this.p = p;
		this.pb = pb;
		this.area = area;
	}

	@Override
	public void run() {
		while (keepRunning()) {

			System.out.println("At run : " + Thread.currentThread().getName() + " is running");
			try {
				String line = null;
				p = pb.start();

				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = in.readLine()) != null && keepRunning()) {
					area.println("    " + line);
					LoadingController.appendResults(line + System.lineSeparator());
				}

				BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = er.readLine()) != null && keepRunning()) {
					area.println(" [ERROR] " + line);
				}
				p.waitFor();
			} catch (IOException | InterruptedException e) {
				area.println(" [ERROR] " + e.getMessage());

			} finally {
				try {
					p.getInputStream().close();
					System.out.println("At run : InputStream closed");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					p.getErrorStream().close();
					System.out.println("At run : ErrorStream closed");
				} catch (IOException e) {
					e.printStackTrace();
				}
				doStop();
			}

		}
		System.out.println("At run : " + Thread.currentThread().getName() + " is stopped");
		isStop = true;
	}

}
