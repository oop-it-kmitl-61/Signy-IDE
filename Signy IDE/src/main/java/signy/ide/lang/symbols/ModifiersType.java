package signy.ide.lang.symbols;

public enum ModifiersType {

	KEYWORDS(new String[] { "public", "package", "private", "protected" }),
	TYPES(new String[] { "class", "interface" });

	private final String[] text;

	ModifiersType(final String[] text) {
		this.text = text;
	}

	public String[] get() {
		return text;
	}

}
