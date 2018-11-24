package signy.ide.controls.nodes;

import org.fxmisc.richtext.StyleClassedTextArea;

import javafx.application.Platform;

public class SOutputArea extends StyleClassedTextArea {

	public SOutputArea() {
		println();
		setEditable(false);
	}

	public static void runLater(final Runnable runnable) {
		if (runnable == null) {
			return;
		}
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable);
		}
	}

	public void println(String text) {
		print(text + System.lineSeparator());
	}

	public void println() {
		print(System.lineSeparator());
	}

	public void print(String text) {
		runLater(() -> {
			if (getLength() >= Short.MAX_VALUE) {
				clear();
			}
			appendText(text);
			requestFollowCaret();
		});
	}

	public void clear() {
		runLater(() -> {
			super.clear();
			requestFollowCaret();
		});
	}

}
