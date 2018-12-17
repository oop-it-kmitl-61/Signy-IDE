package signy.ide.lang;

import javafx.stage.Stage;
import signy.ide.core.module.autocomplete.JavaAutocomplete;
import signy.ide.core.module.autocomplete.JavaAutocompleteAnalyser;

public class Lang {

	public static JavaAutocompleteAnalyser autocompleteAnalyser;
	public static JavaAutocomplete autocomplete;

	public static void init() {

		autocompleteAnalyser = new JavaAutocompleteAnalyser();

	}

	public static void initAutocomplete(Stage stage) {
		autocomplete = new JavaAutocomplete(stage);
	}

}
