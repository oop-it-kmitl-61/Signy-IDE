package signy.ide.core.module;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.fxmisc.richtext.CodeArea;

import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import signy.ide.controls.panes.SEditorPane;

public class SOutline {

	private Tab tab;
	private static TreeView<String> treeView;

	private SEditorPane sEditor;

	public SOutline() {

		tab = new Tab();

		TreeItem<String> rootItem = new TreeItem<String>("Inbox");
		rootItem.setExpanded(true);
		treeView = new TreeView<String>();

		tab.setText("Outline");
		tab.setContent(treeView);

	}

	public void listenToEditor(SEditorPane sEditor) {
		this.sEditor = sEditor;
		this.sEditor.getTabPane().getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			if (newTab != null) {
				createOutline(this.sEditor.getCurrentActiveTab().getTextArea().getText(), sEditor.getCurrentActiveTab().getTextArea());
			}
			else {

			}
		});
	}

	public Tab getTab() {
		return this.tab;

	}

	public static void createOutline(String input, CodeArea textArea) {
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(input.toCharArray());
		ASTNode cu = parser.createAST(null);

		TreeItem<String> rootClass = new TreeItem<String>();
		Stack<String> stack = new Stack<>();
		Queue<String> queue = new LinkedList<>();
		Queue<String> queueStatic = new LinkedList<>();

		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(TypeDeclaration node) {
				String o = node.getName().getFullyQualifiedName();
				stack.push(o);

				return super.visit(node);
			}

			@Override
			public void endVisit(TypeDeclaration node) {
				rootClass.setValue(stack.peek());
				while (!queueStatic.isEmpty()) {
					TreeItem<String> nodeClass = new TreeItem<String>(queueStatic.peek());
					rootClass.getChildren().add(nodeClass);
					queueStatic.remove();
				}
				while (!queue.isEmpty()) {
					TreeItem<String> nodeClass = new TreeItem<String>(queue.peek());
					rootClass.getChildren().add(nodeClass);
					queue.remove();
				}

				super.endVisit(node);
			}

			@Override
			public boolean visit(FieldDeclaration node) {
				for (Object v : node.fragments()) {
					if (v instanceof VariableDeclarationFragment) {
						VariableDeclarationFragment vdf = (VariableDeclarationFragment) v;
						String o = vdf.getName().getFullyQualifiedName();
						if (isStaticDeclaration(node.modifiers())) {
							queueStatic.add(o);
						}
						else {
							queue.add(o);
						}

					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				String o = node.getName().getFullyQualifiedName();
				if (isStaticDeclaration(node.modifiers())) {
					queueStatic.add(o);
				}
				else {
					queue.add(o);
				}
	

				return super.visit(node);
			}

		});

		if (rootClass != null && !stack.isEmpty()) {
			treeView.setRoot(rootClass);
			rootClass.setExpanded(true);
		} else {
			treeView.setRoot(null);
		}

	}

	public static void updateTreeView() {

		TreeItem<String> rootclass = new TreeItem<String>("Test");
		treeView.setRoot(rootclass);

	}

	public static String parseJavaFile(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	static boolean isStaticDeclaration(List<?> modifiers) {
		for (Object item : modifiers) {
			if (item.toString().equals("static")) {
				return true;
			}
		}
		return false;
	}

}
