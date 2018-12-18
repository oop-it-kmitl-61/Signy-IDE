package signy.ide.controls.nodes;

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.DIVIDE;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.SLASH;
import static javafx.scene.input.KeyCode.TAB;
import static org.fxmisc.wellbehaved.event.EventPattern.anyOf;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

import java.util.Collection;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import javafx.event.Event;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import signy.ide.core.dom.JavaDocumentPartitioner;
import signy.ide.core.module.SOutline;
import signy.ide.core.module.autocomplete.AutocompleteDatabase;
import signy.ide.lang.Lang;
import signy.ide.lang.symbols.SymbolsType;

public class SCodeArea extends CodeArea {

	private String name;
	private String fileExtension;

	final private KeyCombination keyCombineCtrlShiftF = new KeyCodeCombination(KeyCode.F, KeyCombination.SHIFT_DOWN,
			KeyCombination.CONTROL_DOWN);

	public SCodeArea(String name, String fileExtension, String content) {

		this.name = name;
		this.fileExtension = fileExtension;

		setParagraphGraphicFactory(LineNumberFactory.get(this));

		focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue)
				Lang.autocomplete.hideSnippet();
		});

		caretPositionProperty().addListener(listener -> Lang.autocompleteAnalyser.compute(this));

//		multiPlainChanges().subscribe(changes -> Lang.autocomplete.callSnippet(changes, this));

		if (fileExtension.equals("*.java") || fileExtension.equals("*.class")) {
//			multiPlainChanges()
//					.subscribe(changes -> setStyleSpans(0, JavaDocumentPartitioner.getSyntaxHighlighting(changes)));
			InputMap<Event> prevent = InputMap.consume(anyOf(keyPressed(TAB), keyPressed(ENTER), keyPressed(BACK_SPACE),
					keyPressed(DELETE), keyPressed(SLASH), keyPressed(DIVIDE)
//							keyPressed(LEFT), 
//							keyPressed(RIGHT)
			));

			Nodes.addInputMap(this, prevent);

//			caretPositionProperty().addListener(obs -> Lang.autocomplete.checkCaretPosition());

			setOnKeyPressed(event -> {
				int position = getCaretPosition();
				int currentParagraph = getCurrentParagraph();
				int positionColumn = getCaretColumn();

				if (event.getCode() == TAB) {
					position = deleteSelection(position);
					String tab = (positionColumn % 4 == 0) ? SymbolsType.TAB.toString()
							: SymbolsType.TAB.toString().substring(positionColumn % 4);
					insertText(currentParagraph, positionColumn, tab);
				} else if (event.getCode() == KeyCode.SEMICOLON) {
					deleteSelection(-1);

					new Thread(() -> {
						try {
							Thread.sleep(50L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Lang.autocompleteAnalyser.callAnalysis(getText(), false);
					}).start();
				} else if (event.getCode() == ENTER) {
					String paragraph = getParagraph(getCurrentParagraph()).getText();
					position = deleteSelection(position);
					insertText(position, "\n" + getTabLength(position));

					Lang.autocompleteAnalyser.callAnalysis(getText(), false);
				} else if (event.getCode() == BACK_SPACE) {
					AutocompleteDatabase.cache.clear();
					makeComment(currentParagraph, positionColumn);
				} else if (event.getCode() == DELETE) {

				} else if (event.getCode() == SLASH || event.getCode() == DIVIDE) {
					String paragraph = getParagraph(getCurrentParagraph()).getText();
					position = deleteSelection(position);
					Collection<String> styleCollect = getStyleAtPosition(currentParagraph, positionColumn);
					if (positionColumn != paragraph.length()) {
						if (styleCollect.isEmpty()) {
							if (paragraph.charAt(positionColumn) == ' ') {
								deleteNextChar();
							}
						}
					}

				} else if (keyCombineCtrlShiftF.match(event)) {
					formatTextFlow();
					setStyleSpans(0, JavaDocumentPartitioner.getSyntaxHighlighting(getText()));
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
				if (fileExtension.equals("*.java") || fileExtension.equals("*.class")) {
					try {
						setStyleSpans(0, JavaDocumentPartitioner.getSyntaxHighlighting(newValue));
					} catch (StackOverflowError e) {
						System.err.println("StackOverflowError : Parentheses in text content didn't correctly.");
					}
					SOutline.createOutline(newValue);
				}
			}));

			replaceText(0, 0, content.replace("\t", SymbolsType.TAB.toString()));
			Lang.autocompleteAnalyser.callAnalysis(getText(), false);

		} else {
			replaceText(0, 0, content);
		}

	}

	private int deleteSelection(int position) {
		IndexRange range = getSelection();
		if (range.getLength() != 0) {
			deleteText(range.getStart() + 1, range.getEnd());
		}

		return position == -1 ? range.getLength() : (position == range.getEnd() ? range.getStart() : position);
	}

	private void makeComment(int paragraphIndex, int positionColumn) {
		String paragraph = getParagraph(getCurrentParagraph()).getText();
		if (deleteSelection(-1) == 0) {
			if (positionColumn != 0 && paragraph.charAt(positionColumn - 1) == '/') {
				replaceText(paragraphIndex, positionColumn - 1, paragraphIndex, positionColumn, " ");
				moveTo(paragraphIndex, positionColumn - 1);
			} else {
				int whitespaces = 0;
				int range = (positionColumn % 4 == 0) ? (positionColumn == 0) ? positionColumn : positionColumn - 4
						: positionColumn - (positionColumn % 4);
				for (int i = positionColumn - 1; i >= range; --i) {
					if (paragraph.charAt(i) == ' ') {
						++whitespaces;
					} else {
						break;
					}
				}

				if (whitespaces > 0) {
					deleteText(paragraphIndex, positionColumn - whitespaces, paragraphIndex, positionColumn);
				} else {
					deletePreviousChar();
				}
			}
		} else {
			deletePreviousChar();
		}
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
		return IntStream.range(0, brackets.size()).mapToObj(i -> SymbolsType.TAB.toString())
				.collect(Collectors.joining());
	}

	public void clearFormatTextFlow() {
		boolean isLast = false;
		for (int i = 0; !isLast; ++i) {
			try {
				String currentPara = getParagraph(i).getText().trim();
				replaceText(i, 0, i, getParagraph(i).length(), currentPara);
			} catch (IndexOutOfBoundsException e) {
				isLast = true;
			}
		}
	}

	public void formatTextFlow() {
		Stack<Character> braces = new Stack<>();
		boolean last = false, openBlock = false;
		String currentPara, tabLength;
		char open = '{', close = '}';
		final String BRACE_PATTERN = "\\{|\\}";
		Pattern pattern = Pattern.compile("(?<BRACE>" + BRACE_PATTERN + ")");
		Matcher matcher;
		for (int i = 0; !last; ++i) {
			try {
				tabLength = "";
				currentPara = getParagraph(i).getText().trim();

				if (!currentPara.equals("")
						&& !getParagraph(i).getStyleSpans().getStyleSpan(0).getStyle().contains("comment")) {

					openBlock = false;
					matcher = pattern.matcher(currentPara);
					while (matcher.find()) {
						if (currentPara.charAt(matcher.start()) == open) {
							braces.push(open);
							openBlock = true;
						} else if (currentPara.charAt(matcher.start()) == close) {
							braces.pop();
						}
					}

					tabLength = IntStream.range(0, (openBlock == true) ? braces.size() - 1 : braces.size())
							.mapToObj(j -> SymbolsType.TAB.toString()).collect(Collectors.joining());
				}
				replaceText(i, 0, i, getParagraph(i).length(), tabLength + currentPara);
			} catch (IndexOutOfBoundsException e) {
				last = true;
			}
		}
	}

}
