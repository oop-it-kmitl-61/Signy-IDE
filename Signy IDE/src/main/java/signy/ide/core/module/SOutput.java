package signy.ide.core.module;

import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import javafx.scene.control.Tab;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import signy.ide.controls.nodes.SOutputArea;

public class SOutput {

	private Tab tab;

	private SOutputArea outputArea;

	public SOutput() {

		tab = new Tab("OUTPUT");
		outputArea = new SOutputArea();
		ScaledVirtualized<SOutputArea> scaleVirtualized = new ScaledVirtualized<>(outputArea);
		outputArea.addEventFilter(ScrollEvent.ANY, e -> {
			if (e.isControlDown()) {
				double scaleAmount = 0.9;
				Scale zoom = scaleVirtualized.getZoom();

				double scale = e.getDeltaY() < 0 ? zoom.getY() * scaleAmount : zoom.getY() / scaleAmount;
				zoom.setX(scale);
				zoom.setY(scale);
			}
		});

		VirtualizedScrollPane<ScaledVirtualized<SOutputArea>> virtualizedScrollPane = new VirtualizedScrollPane<>(
				scaleVirtualized);
		tab.setContent(new VirtualizedScrollPane<>(virtualizedScrollPane));

	}

	public Tab getTab() {
		return tab;
	}

	public String getText() {
		return outputArea.getText();
	}
	
	public SOutputArea getOutputArea() {
		return outputArea;
	}

	public void println(String text) {
		outputArea.appendText(text + "\n");
	}

	public void clearOutputArea() {
		outputArea.clear();
	}

}
