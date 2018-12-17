package signy.ide.core.module.autocomplete;

import javafx.geometry.Bounds;
import javafx.stage.Stage;
import signy.ide.lang.symbols.DocumentType;
import signy.ide.lang.symbols.SymbolsType;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.PlainTextChange;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaAutocomplete extends Autocomplete {

	public JavaAutocomplete(Stage stage) {
		super(stage);
	}

	@Override
	protected void setOptions(List<AutocompleteItem> options) {
		suggestData.clear();

		options.forEach(option -> {
			if (option.packageName == 0) {
				String result = "";
				String text = option.toString();
				int diff = 57 - text.length() - option.getReturnTypeS().length();
				String parameters = diff <= 0
						? option.parameters.split(", ")[0].contains(" ") ? deliver(option.parameters.split(", ")) : ""
						: option.parameters;
				result = option.getFlag() + " " + option.text
						+ (option.flag == 2 || option.flag == 4 ? "(" + parameters + ")" : "");
				diff = 57 - result.length() - option.getReturnTypeS().length();

				String space = diff == 0 ? "" : String.format("%" + diff + "s", "");

				suggestData.add(result + space + option.getReturnTypeS());
			} else
				suggestData.add(option.toString());
		});
		maxLength.set(0);

		AutocompleteDatabase.cache = options;
	}

	@Override
	public void doOption() {
		if (!hideTemporarily) {
			AutocompleteItem item = AutocompleteDatabase.cache.get(mainArea.getSelectionModel().getSelectedIndex());

			// TODO invisible
			String CLASS_NAME = item.text;
			EditWord.classN = item.returnType;
			AutocompleteDatabase.cache.clear();

			codeAreaFocused.insertText(EditWord.end, item.text.substring(EditWord.end - EditWord.begin));

			String packageName = item.getPackageName();
			String insertImport = "";
			int lastPosition = codeAreaFocused.getCaretPosition() - (item.text.contains("()") ? 1 : 0);
			if (!packageName.isEmpty() && !packageName.equals("java.lang")
					&& !codeAreaFocused.getText().contains(packageName)) {
				String text = codeAreaFocused.getText();
				int indexImport = text.indexOf("import");
				int indexPackage = text.indexOf("package");
				int indexInsert = 0;

				if (indexImport != -1) {
					insertImport = "import " + packageName + "." + CLASS_NAME + ";\n";
					indexInsert = indexImport;
				} else if (indexPackage != -1) {
					insertImport = "\n\nimport " + packageName + "." + CLASS_NAME + ";\n\n";
					indexInsert = codeAreaFocused.getText().substring(indexPackage).indexOf(";");
				} else
					insertImport = "import " + packageName + "." + CLASS_NAME + ";\n\n";
				codeAreaFocused.insertText(indexInsert, insertImport);
			}
			codeAreaFocused.moveTo(lastPosition + insertImport.length());
			hideSnippet();
		}
	}

	@Override
	public void callSnippet(List<PlainTextChange> changeList, CodeArea codeArea) {
		codeAreaFocused = codeArea;
		try {
			userCl = JavaAutocompleteAnalyser.RAW.parse(codeAreaFocused.getText()).getType(0).getNameAsString();
		} catch (ParseProblemException ignored) {
		}

		String inserted = changeList.get(0).getInserted();

		if (begin || wasSameSymbol) {
			begin = false;
			wasSameSymbol = false;
			return;
		}

		if (inserted.length() == 1) {
			char firstChar = inserted.charAt(0);
			int position = codeAreaFocused.getCaretPosition();
			String nextChar = codeAreaFocused.getText(position,
					codeAreaFocused.getLength() < position + 1 ? position : position + 1);

			if (Character.isMirrored(firstChar)) {
				if (Arrays.stream(DocumentType.MIRROR.get()).anyMatch(item -> item.endsWith(nextChar))) {
					String text = codeAreaFocused.getText(0, position);
					char target = Arrays.stream(DocumentType.MIRROR.get()).filter(option -> option.endsWith(nextChar))
							.findFirst().get().charAt(0);
					boolean isOpened = false;

					for (int i = 0; i < text.length(); i++)
						if (text.charAt(i) == target)
							isOpened = !isOpened;

					if (!isOpened && position < codeAreaFocused.getText().length()) {
						codeAreaFocused.deleteText(position, position + 1);
						codeAreaFocused.moveTo(position + 1);
					} else
						pasteSimilarSymbol(inserted, DocumentType.MIRROR.get(), true);
				} else
					pasteSimilarSymbol(inserted, DocumentType.MIRROR.get(), true);

				return;
			} else if (Arrays.stream(DocumentType.SAME.get()).anyMatch(item -> item.contains(inserted))) {
				if (Arrays.stream(DocumentType.SAME.get()).anyMatch(item -> item.contains(nextChar))) {
					String text = codeAreaFocused.getText(0, position);
					char target = Arrays.stream(DocumentType.SAME.get()).filter(option -> option.startsWith(nextChar))
							.findFirst().get().charAt(0);
					boolean isOpened = false;

					for (int i = 0; i < text.length(); i++)
						if (text.charAt(i) == target)
							isOpened = !isOpened;

					if (!isOpened) {
						codeAreaFocused.deleteText(position, position + 1);
						codeAreaFocused.moveTo(position + 1);
					} else
						pasteSimilarSymbol(inserted, DocumentType.SAME.get(), false);
				} else {
					String text = codeAreaFocused.getText(0, position);

					if (text.length() > 2 && position - 3 > -1) {
						String previousPair = text.substring(position - 3, position - 1);
						if (previousPair.charAt(0) == previousPair.charAt(1)
								&& previousPair.charAt(0) == inserted.charAt(0))
							codeAreaFocused.insertText(position - 2, "\\");
						else {
							char target = Arrays.stream(DocumentType.SAME.get())
									.filter(option -> option.startsWith(inserted)).findFirst().get().charAt(0);
							boolean isOpened = false;

							for (int i = 0; i < text.length(); i++)
								if (text.charAt(i) == target)
									isOpened = !isOpened;

							if (!isOpened) {
								String additionalCondition = codeAreaFocused.getText(position,
										codeAreaFocused.getText().length());

								for (int i = 0; i < additionalCondition.length(); i++)
									if (additionalCondition.charAt(i) == target) {
										codeAreaFocused.insertText(position - 1, "\\");
										new Thread(() -> {
											try {
												Thread.sleep(1);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											codeAreaFocused.moveTo(position + 1);
										}).start();
										return;
									}
								pasteSimilarSymbol(inserted, DocumentType.SAME.get(), false);
							} else
								pasteSimilarSymbol(inserted, DocumentType.SAME.get(), false);
						}
					} else
						pasteSimilarSymbol(inserted, DocumentType.SAME.get(), false);
				}
				return;
			}
		}

		if (".".equals(inserted)) {
			if (EditWord.begin == EditWord.end) {
				EditWord.begin++;
				EditWord.end++;
			}
			if (EditWord.classN != null) {
				List<AutocompleteItem> options = EditWord.classN.items;
				options.sort((a, b) -> {
					if (a.text.length() != b.text.length())
						return a.text.length() - b.text.length();
					if (a.parameters.length() != b.parameters.length())
						return a.parameters.length() - b.parameters.length();

					return a.parameters.compareTo(b.parameters);
				});

				if (!options.isEmpty()) {
					Bounds bounds = codeAreaFocused.caretBoundsProperty().getValue().get();
					double y = bounds.getMaxY();

					if (EditWord.beginGlobal == -1) {
						EditWord.beginGlobal = bounds.getMaxX() - 30;

						setX(EditWord.beginGlobal);
					}

					setY(y);

					setOptions(options);

					if (hideTemporarily) {
						show(stage);
						hideTemporarily = false;
					}
				} else
					hideTemporarily();
			}
		} else if (" ".equals(inserted) || SymbolsType.TAB.toString().equals(inserted)
				|| !inserted.isEmpty() && inserted.charAt(0) == 10) {
			EditWord.classN = null;
			hideSnippet();
		} else {
			if (!inserted.isEmpty() && !" ".equals(inserted)) {
				if (EditWord.word.length() == 0) {
					int caretPos = codeAreaFocused.getCaretPosition();
					if (caretPos == 0) {
						EditWord.begin = (0);
					} else {
						EditWord.begin = caretPos - 1;
					}
				}
				EditWord.concat(changeList.get(0).getInserted());
			} else {
				AutocompleteDatabase.cache.clear();
				if (EditWord.word.length() != 0) {
					try {
						EditWord.remove(changeList.get(0).getRemoved(), codeAreaFocused.getCaretPosition());
					} catch (StringIndexOutOfBoundsException ignored) {
						EditWord.clear();
					}
				} else {
					EditWord.clear();
					EditWord.classN = null;
				}
			}

			if (EditWord.word.length() != 0) {
				List<AutocompleteItem> options = AutocompleteDatabase.searchClusters(userCl);
				options.sort((a, b) -> {
					if (a.text.length() != b.text.length())
						return a.text.length() - b.text.length();
					if (a.parameters.length() != b.parameters.length())
						return a.parameters.length() - b.parameters.length();

					return a.parameters.compareTo(b.parameters);
				});

				if (!options.isEmpty() && codeAreaFocused.caretBoundsProperty().getValue().isPresent()) {
					Bounds bounds = codeAreaFocused.caretBoundsProperty().getValue().get();
					double y = bounds.getMaxY();

					if (EditWord.beginGlobal == -1) {
						EditWord.beginGlobal = bounds.getMaxX() - 30;

						setX(EditWord.beginGlobal);
					}

					setY(y);

					setOptions(options);

					if (hideTemporarily) {
						show(stage);
						hideTemporarily = false;
					}
				} else
					hideTemporarily();
			} else
				hideTemporarily();
		}
	}

	private void pasteSimilarSymbol(String symbol, String[] options, boolean mirror) {
		Optional<String> findSame = Arrays.stream(options).filter(option -> option.startsWith(symbol)).findFirst();

		if (findSame.isPresent()) {
			wasSameSymbol = !mirror;
			int position = codeAreaFocused.getCaretPosition();
			codeAreaFocused.insertText(position, Character.toString(findSame.get().charAt(1)));
			EditWord.clear();
		}
	}

	private String deliver(String[] fragments) {
		return Arrays.stream(fragments).map(fragment -> fragment.substring(0, fragment.indexOf(" ")))
				.collect(Collectors.joining(", "));
	}
}