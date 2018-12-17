package signy.ide.core.module.autocomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import signy.ide.lang.symbols.DocumentType;
import signy.ide.lang.symbols.SymbolsType;

public class AutocompleteDatabase {

	public static List<AutocompleteItem> cache;
	public static List<AutocompleteCluster> clusters;
	public static List<AutocompleteItem> keywords;
	public static List<AutocompleteItem> method;

	static {

		method = new ArrayList<>();
		cache = new ArrayList<>();
		clusters = new ArrayList<>();
		IntStream.range(0, 8).forEach(i -> clusters.add(new AutocompleteCluster()));

		keywords = Arrays.stream(DocumentType.KEYWORDS.get()).map(word -> new AutocompleteItem(0, word, "", 0, 0))
				.collect(Collectors.toList());

	}

	public static void addItem(int cluster, AutocompleteItem item, String classN, int classNum, boolean isNew) {
		char letter = classN.charAt(0);

		clusters.get(cluster).searchLetter(letter).searchClass(classNum).addItem(item, isNew);
	}

	public static void addMethod(String[] returnName) {
		String returnTypeS = returnName[0];
		String name = returnName[1];
		int flag = "int".equals(returnTypeS) || "double".equals(returnTypeS) || "short".equals(returnTypeS)
				|| "byte".equals(returnTypeS) || "long".equals(returnTypeS) || "float".equals(returnTypeS)
				|| "char".equals(returnTypeS) || "boolean".equals(returnTypeS) ? 5 : 6;
		int returnS = CollectorAutocompleteText.addReturnTypeS(returnTypeS);
		method.add(new AutocompleteItem(flag, name, "", returnS));
	}

	public static List<AutocompleteItem> searchClusters(String userCl) {
		if (EditWord.classN != null) {
			return normalSearch();
		}

		String word = EditWord.word.toString();

		List<AutocompleteItem> result = new ArrayList<>(method);
		for (int i = 0; i < 8; i++)
			if (i == 0) {
				if (cache.size() > 0)
					return doFilter(cache, word);
			} else if (i == 4) {
				if (EditWord.classN == null)
					result.addAll(doFilter(keywords, word));
			} else {
				AutocompleteCluster cluster = clusters.get(i);
				result.addAll(cluster.globalSearch(word));
			}

		return result;
	}

	private static List<AutocompleteItem> doFilter(List<AutocompleteItem> items, String word) {
		return items.stream().filter(item -> item.text.startsWith(word)).collect(Collectors.toList());
	}

	public static void weaveWeb() {
		for (int i = 1; i < 8; i++) {
			if (i != 4) {
				AutocompleteCluster cluster = clusters.get(i);

				cluster.autocompleteClusterLetters
						.forEach(letters -> letters.autocompleteClusterLetterClasses.forEach(classes -> {
							classes.items.forEach(
									item -> item.returnType = AutocompleteDatabase.globalSearch(item.getReturnTypeS()));
							classes.classN.returnType = classes;
						}));
			}
		}
	}

	public static void weaveWebMethod() {
		method.forEach(a -> a.returnType = AutocompleteDatabase.globalSearch(a.getReturnTypeS()));
	}

	public static List<AutocompleteItem> normalSearch() {
		return EditWord.classN.items.stream().filter(item -> item.text.startsWith(EditWord.word.toString()))
				.collect(Collectors.toList());
	}

	public static AutocompleteClusterLetterClass globalSearch(String type) {
		int needed = CollectorAutocompleteText.addReturnTypeS(type);

		if (!"long".equals(type) && !"short".equals(type) && !"int".equals(type) && !"byte".equals(type)
				&& !"double".equals(type) && !"float".equals(type) && !"char".equals(type) && !"void".equals(type))
			for (int i = 1; i < 8; i++)
				if (i != 4) {
					List<AutocompleteClusterLetter> letters = clusters.get(i).autocompleteClusterLetters;
					for (AutocompleteClusterLetter letter : letters)
						if (letter.letter == type.charAt(0)) {
							List<AutocompleteClusterLetterClass> classes = letter.autocompleteClusterLetterClasses;
							for (AutocompleteClusterLetterClass classN : classes)
								if (classN.name == needed)
									return classN;
						}
				}

		return null;
	}
}