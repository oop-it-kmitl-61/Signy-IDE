package signy.ide.controls.panes;

import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class STreeView {

	private TabPane tabPane;

	private Stack<?> i;
	private static TreeItem<String> rootItem;

	public STreeView() {

		this.tabPane = new TabPane();
		tabPane.setSide(Side.LEFT);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(new Tab("Files"));
		tabPane.getTabs().add(new Tab("AST View"));
		tabPane.getTabs().add(new Tab("Search"));

	}

	public TabPane getTabPane() {
		return this.tabPane;
	}

	public static void createOutline(String input) {
	    ASTParser parser = ASTParser.newParser(AST.JLS10);
	    parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    parser.setSource(input.toCharArray());
	    ASTNode cu = parser.createAST(null);

	    Stack<String> i = new Stack<>();
	 
	    cu.accept(new ASTVisitor() {

	        @Override
	        public boolean visit(TypeDeclaration node) {
	            String o = node.getName().getFullyQualifiedName() + " java-class";
	            i.push(o);

	            System.out.println(node.getName().getFullyQualifiedName());
	            return super.visit(node);
	        }

	        @Override
	        public void endVisit(TypeDeclaration node) {
	        	System.out.println(node.getName().getFullyQualifiedName());
	            super.endVisit(node);
	        }

	        @Override
	        public boolean visit(FieldDeclaration node) {
	            for( Object v : node.fragments() ) {
	                if( v instanceof VariableDeclarationFragment ) {
	                    VariableDeclarationFragment vdf = (VariableDeclarationFragment) v;
	                    String o = vdf.getName().getFullyQualifiedName() + " java-field";
	                    
	                    System.out.println(vdf.getName().getFullyQualifiedName());
	                    i.push(o);
	                }
	            }
	            return super.visit(node);
	        }

	        @Override
	        public boolean visit(MethodDeclaration node) {
	            String o = node.getName().getFullyQualifiedName() + " java-method";
	            
	            System.out.println(node.getName().getFullyQualifiedName());
	            i.push(o);
	            return super.visit(node);
	        }

	    });

	    rootItem = new TreeItem<String> ("Inbox");
	    while (! i.isEmpty()) {
	    	System.out.println(i.peek());
	    	i.pop();
	    }

	    updateTreeView();

	}

	public static void updateTreeView() {

		TreeItem<String> rootclass = new TreeItem<String>("Test");

		rootItem.getChildren().add(rootclass);

	}

}
