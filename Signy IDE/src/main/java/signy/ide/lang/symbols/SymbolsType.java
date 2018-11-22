package signy.ide.lang.symbols;

public enum SymbolsType {

	TAB("    ");

	private final String text;

	SymbolsType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
