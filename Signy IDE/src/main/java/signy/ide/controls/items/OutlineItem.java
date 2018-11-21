package signy.ide.controls.items;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class OutlineItem<T> extends TreeItem<T> {

	private T node;
	private String toString;
	private final String KEYWORD_PACKAGE = "package";
	private final String KEYWORD_CLASS = "class";

	public OutlineItem() {

		this(null);

	}

	public OutlineItem(T node) {

		this(node, (Node) null);

	}

	@SuppressWarnings("unchecked")
	public OutlineItem(final T node, final Node graphic) {

		this.node = node;
		if (node instanceof PackageDeclaration) {
			toString = KEYWORD_PACKAGE + " " + ((PackageDeclaration) node).getName().getFullyQualifiedName();
		} else if (node instanceof TypeDeclaration) {
			toString = listToString(((TypeDeclaration) node).modifiers()) + " " + KEYWORD_CLASS + " "
					+ ((TypeDeclaration) node).getName().getFullyQualifiedName();
		} else if (node instanceof FieldDeclaration) {
			toString = listToString(((FieldDeclaration) node).modifiers()) + " "
					+ listToString(((FieldDeclaration) node).fragments()) + " : " + ((FieldDeclaration) node).getType();
		} else if (node instanceof MethodDeclaration) {
			toString = listToString(((MethodDeclaration) node).modifiers()) + " "
					+ ((MethodDeclaration) node).getName().getFullyQualifiedName() + "("
					+ listToString(((MethodDeclaration) node).parameters()) + ")" + " : "
					+ ((MethodDeclaration) node).getReturnType2();
		} else if (node instanceof VariableDeclarationFragment) {
			toString = ((VariableDeclarationFragment) node).toString();
		}

		toString = toString.trim();
		setValue((T) toString);
		setGraphic(graphic);

	}

	public T getNode() {
		return node;
	}

	@Override
	public String toString() {
		return this.toString;
	}

	private String listToString(List<?> list) {
		String result = "";
		for (int i = 0; i < list.size(); ++i) {
			result += list.get(i) + " ";
		}
		return result.trim();
	}

}
