package signy.ide.core.module;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

import javafx.scene.control.Tab;

public class SOutput {

	private Tab tab;

	private StyleClassedTextArea outputArea;

	public SOutput() {

		tab = new Tab("OUTPUT");
		outputArea = new StyleClassedTextArea();

		tab.setContent(new VirtualizedScrollPane<>(outputArea));

	}

	public Tab getTab() {
		return tab;
	}
	public String getText() {
		return outputArea.getText();
	}

	public void println(String text) {
		outputArea.appendText(text + "\n");
	}

	public void clearOutputArea() {
		outputArea.clear();
	}

}
