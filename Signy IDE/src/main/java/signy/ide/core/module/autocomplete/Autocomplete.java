package signy.ide.core.module.autocomplete;

import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static org.fxmisc.wellbehaved.event.EventPattern.anyOf;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Autocomplete extends Popup {

	protected static ListView mainArea;
	protected static CodeArea codeAreaFocused;
	protected static VirtualizedScrollPane scrollPane;

	protected static Stage stage;

	protected static boolean begin;
	protected static boolean wasSameSymbol;
	protected static boolean hideTemporarily = true;

	protected static AtomicInteger maxLength;

	protected static String userCl;
	protected ObservableList<String> suggestData;

	protected Autocomplete(Stage stage) {

		Autocomplete.stage = stage;

		begin = true;
		wasSameSymbol = false;

		maxLength = new AtomicInteger();

		suggestData = FXCollections.observableArrayList();

		mainArea = new ListView(suggestData);
		mainArea.setPrefWidth(630);
		mainArea.setPrefHeight(200);
		mainArea.getStyleClass().add("autocomplete");

		// TODO highlight paragraph on mouse moved
		// mainArea.setOnMouseEntered(event -> mainArea.moveTo(, 0));
		mainArea.setOnMouseClicked(event -> doOption());
		mainArea.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.PERIOD)
				doOption();
		});
//		mainArea.addEventFilter(ScrollEvent.ANY, e -> {
//			ScrollBar verticalBar = (ScrollBar) scrollPane.lookup(".scroll-bar:vertical");
//
//			Runnable action = () -> {
//				if (e.getDeltaY() < 0)
//					verticalBar.increment();
//				else
//					verticalBar.decrement();
//			};
//			IntStream.range(0, 4).forEach((i) -> action.run());
//		});

		InputMap<Event> prevent = InputMap.consume(anyOf(keyPressed(LEFT), keyPressed(RIGHT)));
		Nodes.addInputMap(mainArea, prevent);

		getContent().add(mainArea);
	}

	protected void setOptions(List<AutocompleteItem> options) {
		throw new UnsupportedOperationException();
	}

	protected void doOption() {
		throw new UnsupportedOperationException();
	}

	public void callSnippet(List<PlainTextChange> changeList, CodeArea codeArea) {
		throw new UnsupportedOperationException();
	}

	public void hideSnippet() {
		EditWord.clear();
		AutocompleteDatabase.cache.clear();
		hideTemporarily();
	}

	public void hideTemporarily() {
		if (!hideTemporarily) {
			hide();
			hideTemporarily = true;
		}
	}

	public void checkCaretPosition() {
		int position = codeAreaFocused.getCaretPosition();
		boolean isOutRange = EditWord.isOutRange(position);
		if (isOutRange && !(position > 0 && ".".equals(codeAreaFocused.getText(position - 1, position))
				&& EditWord.classN != null)) {
			EditWord.classN = null;
			hideSnippet();
		}
	}
}