package signy.ide.core.module.autocomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutocompleteClusterLetter {

	public char letter;
	public List<AutocompleteClusterLetterClass> autocompleteClusterLetterClasses;

	public AutocompleteClusterLetter(char letter) {
		this.letter = letter;

		autocompleteClusterLetterClasses = new ArrayList<>();
	}

	public AutocompleteClusterLetterClass searchClass(int classN) {

		Optional<AutocompleteClusterLetterClass> classOptional = autocompleteClusterLetterClasses.size() != 0
				? autocompleteClusterLetterClasses.stream().filter(letterClass -> letterClass.name == classN)
						.findFirst()
				: Optional.empty();

		AutocompleteClusterLetterClass class1 = classOptional.orElse(new AutocompleteClusterLetterClass(classN));

		if (!classOptional.isPresent())
			autocompleteClusterLetterClasses.add(class1);

		return class1;
	}

}
