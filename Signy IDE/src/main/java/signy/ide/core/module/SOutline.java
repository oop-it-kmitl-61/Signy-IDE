package signy.ide.core.module;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.fxmisc.richtext.CodeArea;

import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import signy.ide.FXMLDocumentController;
import signy.ide.controls.items.OutlineItem;

public class SOutline {

	private Tab tab;
	private TreeView treeView;
	private static TreeItem treeRoot;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SOutline(FXMLDocumentController controller) {

		tab = new Tab();

		treeRoot = new TreeItem();
		treeView = new TreeView(treeRoot);
		treeView.setShowRoot(false);

		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					OutlineItem item = (OutlineItem) treeView.getSelectionModel().getSelectedItem();
					if (item == null) {
						return;
					}
					if (item.getNode() instanceof TypeDeclaration) {
						item.setExpanded(true);

					}
					int startPos = ((ASTNode) item.getNode()).getStartPosition();
					int length = ((ASTNode) item.getNode()).getLength();
					CodeArea textArea = controller.getEditorPane().getCurrentActiveTab().getTextArea();
					textArea.moveTo(startPos);
					textArea.selectRange(startPos + length, startPos);
					textArea.requestFollowCaret();
					textArea.requestFocus();
				}
			}
		});

		tab.setText("Outline");
		tab.setContent(treeView);
		ImageView img = new ImageView(new Image("signy/ide/resources/icons/outline.png", 14, 16, false, false));
		tab.setGraphic(img);

	}

	public Tab getTab() {
		return tab;
	}

	@SuppressWarnings("rawtypes")
	public static void createOutline(String input) {

		treeRoot.getChildren().clear();

		if (input == null) {
			return;
		}

		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(input.toCharArray());
		ASTNode cu = parser.createAST(null);

		Stack<PackageDeclaration> stackPackage = new Stack<PackageDeclaration>();
		Stack<TypeDeclaration> stackType = new Stack<TypeDeclaration>();
//		Queue<OutlineItem> queue = new LinkedList<>();
//		Queue<OutlineItem> queueStatic = new LinkedList<>();

		cu.accept(new ASTVisitor() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean visit(PackageDeclaration node) {
				stackPackage.push(node);
				OutlineItem packageItem = new OutlineItem(node);
				treeRoot.getChildren().add(packageItem);
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				stackType.push(node);
				return super.visit(node);
			}

			@SuppressWarnings("unchecked")
			@Override
			public void endVisit(TypeDeclaration node) {
				for (TypeDeclaration rootClass : stackType) {
					OutlineItem rootClassItem = new OutlineItem(rootClass);
					for (FieldDeclaration field : rootClass.getFields()) {
						OutlineItem nodeClass = new OutlineItem(field);
						rootClassItem.getChildren().add(nodeClass);
					}

					for (MethodDeclaration method : rootClass.getMethods()) {
						OutlineItem nodeClass = new OutlineItem(method);
						rootClassItem.getChildren().add(nodeClass);
					}

					treeRoot.getChildren().add(rootClassItem);
					rootClassItem.setExpanded(true);

				}
				super.endVisit(node);
			}

//			@Override
//			public boolean visit(FieldDeclaration node) {
//				for (Object v : node.fragments()) {
//					if (v instanceof VariableDeclarationFragment) {
//						VariableDeclarationFragment vdf = (VariableDeclarationFragment) v;
//						String o = vdf.getName().getFullyQualifiedName();
//						OutlineItem<VariableDeclarationFragment> item = new OutlineItem<VariableDeclarationFragment>(
//								vdf);
//						if (isStaticDeclaration(node.modifiers())) {
//							queueStatic.add(item);
//						} else {
//							queue.add(item);
//						}
//
//					}
//				}
//				return super.visit(node);
//			}

//			@Override
//			public boolean visit(MethodDeclaration node) {
//				String o = node.getName().getFullyQualifiedName();
//				OutlineItem item = new OutlineItem(node);
//				if (isStaticDeclaration(node.modifiers())) {
//					queueStatic.add(item);
//				} else {
//					queue.add(item);
//				}
//
//				return super.visit(node);
//			}

		});

	}

	public String parseJavaFile(String filePath) throws IOException {
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

//	static boolean isStaticDeclaration(List<?> modifiers) {
//		for (Object item : modifiers) {
//			if (item.toString().equals("static")) {
//				return true;
//			}
//		}
//		return false;
//	}

}
