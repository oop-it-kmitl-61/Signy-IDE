package signy.ide.lang.symbols;

public enum DocumentType {

	MIRROR(new String[] { "{}", "[]", "<>", "()" }), SAME(new String[] { "\"\"", "\'\'" }),
	KEYWORDS(new String[] { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
			"continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for",
			"goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package",
			"private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
			"synchronized", "this", "throw", "throws", "transient", "try", "var", "void", "volatile", "while" });

	private final String[] text;

	DocumentType(final String[] text) {
		this.text = text;
	}

	public String[] get() {
		return text;
	}

}
