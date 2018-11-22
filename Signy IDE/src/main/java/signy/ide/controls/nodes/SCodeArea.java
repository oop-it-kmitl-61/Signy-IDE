package signy.ide.controls.nodes;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import javafx.event.Event;
import javafx.scene.control.IndexRange;
import signy.ide.core.dom.JavaDocumentPartitioner;
import signy.ide.core.module.SOutline;
import signy.ide.lang.symbols.SymbolsType;

import static javafx.scene.input.KeyCode.*;
import static org.fxmisc.wellbehaved.event.EventPattern.anyOf;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SCodeArea extends CodeArea {

	private String name;
	private String fileExtension;

	public SCodeArea(String name, String fileExtension, String content) {

		this.name = name;
		this.fileExtension = fileExtension;

		InputMap<Event> prevent = InputMap.consume(
				anyOf(
						keyPressed(TAB), 
						keyPressed(ENTER),
						keyPressed(BACK_SPACE)
//						keyPressed(LEFT), 
//						keyPressed(RIGHT)
				));

		Nodes.addInputMap(this, prevent);

		setOnKeyPressed(event -> {
			int position = getCaretPosition();

			if (event.getCode() == TAB) {
				position = deleteSelection(position);
				insertText(position, SymbolsType.TAB.toString());
			} else if (event.getCode() == ENTER) {
				position = deleteSelection(position);
				insertText(position, "\n" + getTabLength(position));
			} else if (event.getCode() == BACK_SPACE) {
				String paragraph = getParagraph(getCurrentParagraph()).getText();
				if (deleteSelection(-1) == 0) {
					if (position >= 4 && SymbolsType.TAB.toString().equals(getText(position - 4, position))) {
						int whitespaces = 0;
						for (int i = getCaretColumn() - 1; i >= 0; --i) {
							if (paragraph.charAt(i) == ' ') {
								++whitespaces;
							} else {
								break;
							}
						}

						if (whitespaces % 4 == 0) {
							deleteText(position - 4, position);
						} else {
							deletePreviousChar();
						}
					} else {
						deletePreviousChar();
					}
				}
			} else if (event.getCode() == LEFT) {
//					this.moveTo(position - 1);
//					requestFollowCaret();
				System.out.println("LEFT");
			} else if (event.getCode() == RIGHT) {
//					this.moveTo(position + 1);
//					requestFollowCaret();
				System.out.println("RIGHT");
			}
		});

		textProperty().addListener(((observable, oldValue, newValue) -> {
			if (fileExtension.equals("*.java")) {
				try {
					setStyleSpans(0, JavaDocumentPartitioner.getSyntaxHighlighting(newValue));
				} catch (StackOverflowError e) {
					System.err.println("StackOverflowError : Parentheses in text content didn't correctly.");
				}
				SOutline.createOutline(newValue);
			}
		}));

		setParagraphGraphicFactory(LineNumberFactory.get(this));
		replaceText(0, 0, content.replace("\t", SymbolsType.TAB.toString()));

	}

	private int deleteSelection(int position) {
		IndexRange range = getSelection();
		if (range.getLength() != 0) {
			deleteText(range.getStart(), range.getEnd());
		}

		return position == -1 ? range.getLength() : (position == range.getEnd() ? range.getStart() : position);
	}

	private String getTabLength(int position) {
		char open = '{';
		char close = '}';
		Stack<Character> brackets = new Stack<>();
		String textToAnalyze = getText(0, position);
		for (int i = 0; i < position; ++i) {
			char charToAnalyze = textToAnalyze.charAt(i);
			if (charToAnalyze == open) {
				brackets.push(open);
			} else if (charToAnalyze == close) {
				brackets.pop();
			}
		}
		return IntStream.range(0, brackets.size()).mapToObj(i -> SymbolsType.TAB.toString()).collect(Collectors.joining());
	}

}
