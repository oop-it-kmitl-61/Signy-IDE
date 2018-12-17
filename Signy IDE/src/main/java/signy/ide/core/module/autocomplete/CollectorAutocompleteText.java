package signy.ide.core.module.autocomplete;

import java.util.ArrayList;
import java.util.List;

public class CollectorAutocompleteText {

	public static String[] flag;
	public static List<String> packageName;
	public static List<String> returnTypeS;

	static {

		flag = new String[] { " ", "A", "C", "I", "M", "V", "?" };
		packageName = new ArrayList<>();
		returnTypeS = new ArrayList<>();

	}

	public static int addPackageName(String text) {
		return addToList(packageName, text);
	}

	public static int addReturnTypeS(String text) {
		return addToList(returnTypeS, text);
	}

	private static int addToList(List<String> list, String text) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).equals(text))
				return i;
		list.add(text);

		return list.size() - 1;
	}

}
