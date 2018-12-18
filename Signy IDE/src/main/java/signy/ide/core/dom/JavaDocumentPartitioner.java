package signy.ide.core.dom;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class JavaDocumentPartitioner {

	public JavaDocumentPartitioner() {

	}

	private static final String[] KEYWORDS = new String[] { "abstract", "assert", "break", "case", "catch", "class",
			"const", "continue", "default", "do", "else", "enum", "extends", "final", "finally", "for", "goto", "if",
			"implements", "import", "instanceof", "interface", "native", "new", "package", "private", "protected",
			"public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
			"throws", "transient", "try", "void", "volatile", "while" };

	private static final String[] PRIMITIVE_TYPES = new String[] { "byte", "short", "int", "long", "float", "double",
			"boolean", "char" };

	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", JavaDocumentPartitioner.getKeywords())
			+ ")\\b";
	private static final String PRIMITIVE_PATTERN = "\\b(" + String.join("|", JavaDocumentPartitioner.getPrimitive())
			+ ")\\b";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String ANNOTATION_PATTERN = "@[^\n]*";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
	private static final String NUMBERS_PATTERN = "(\\d+)";

	private static final String METHOD_PATTTERN = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])";
	private static final String VARIABLE_PATTERN = ".+\\s(.+?)(;|=)";

	private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PRIMITIVE>"
			+ PRIMITIVE_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<ANNOTATION>" + ANNOTATION_PATTERN
			+ ")" + "|(?<PAREN>" + PAREN_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>"
			+ BRACKET_PATTERN + ")" + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN
			+ ")" + "|(?<NUMBERS>" + NUMBERS_PATTERN + ")");

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {

		Matcher matcher = JavaDocumentPartitioner.getPattern().matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("KEYWORD") != null ? "keyword"
					: matcher.group("PRIMITIVE") != null ? "primitive"
							: matcher.group("STRING") != null ? "string"
									: matcher.group("ANNOTATION") != null ? "annotation"
											: matcher.group("PAREN") != null ? "paren"
//													: matcher.group("BRACE") != null ? "brace"
													: matcher.group("BRACKET") != null ? "bracket"
															: matcher.group("SEMICOLON") != null ? "semicolon"
																	: matcher.group("COMMENT") != null ? "comment"
																			: matcher.group("NUMBERS") != null
																					? "numbers"
																					: null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();

	}

	public final static String[] getKeywords() {
		return KEYWORDS;
	}

	public final static String[] getPrimitive() {
		return PRIMITIVE_TYPES;
	}

	public final static Pattern getPattern() {
		return PATTERN;
	}

	public static StyleSpans<Collection<String>> getSyntaxHighlighting(String text) {
		return computeHighlighting(text);
	}

}