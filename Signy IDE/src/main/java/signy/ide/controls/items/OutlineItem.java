package signy.ide.controls.items;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import signy.ide.utils.Utils;

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
			toString = Utils.listToString(((TypeDeclaration) node).modifiers()) + " " + KEYWORD_CLASS + " "
					+ ((TypeDeclaration) node).getName().getFullyQualifiedName();
		} else if (node instanceof FieldDeclaration) {
			toString = Utils.listToString(((FieldDeclaration) node).modifiers()) + " "
					+ Utils.listToString(((FieldDeclaration) node).fragments()) + " : "
					+ ((FieldDeclaration) node).getType();
		} else if (node instanceof MethodDeclaration) {
			toString = Utils.listToString(((MethodDeclaration) node).modifiers()) + " "
					+ ((MethodDeclaration) node).getName().getFullyQualifiedName() + "("
					+ Utils.listToString(((MethodDeclaration) node).parameters()) + ")" + " : "
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

}
