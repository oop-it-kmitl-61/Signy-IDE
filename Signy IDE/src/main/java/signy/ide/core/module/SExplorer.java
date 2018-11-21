package signy.ide.core.module;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.fxmisc.richtext.CodeArea;
import signy.ide.FXMLDocumentController;
import signy.ide.controls.items.OutlineItem;
import signy.ide.controls.panes.SEditorPane;

public class SExplorer {

	private FXMLDocumentController controller;

	private SEditorPane editor;

	private Tab tab;
	private static String DefaultPath = System.getProperty("user.dir");

	public SExplorer(FXMLDocumentController controller) {
		this.editor = controller.getEditorPane();
		init(DefaultPath);
	}

	private void init(String path) {
		this.tab = new Tab();
		VBox vb = new VBox();
		File[] drives = File.listRoots();
		vb.getChildren().add(getTreeView(DefaultPath));
		if (drives != null && drives.length > 0) {
			for (File aDrive : drives) {
				vb.getChildren().add(getTreeView(aDrive.getAbsolutePath()));
			}
		}
//		tab.setContent(vb);
		tab.setContent(getTreeView(path));
		tab.setText("Explorer");
	}

	public TreeView<File> getTreeView(String path) {
		TreeItem<File> root = createNode(new File(path));
		TreeView<File> treeView = new TreeView<File>(root);
		treeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
			public TreeCell<File> call(TreeView<File> tv) {
				return new TreeCell<File>() {

					@Override
					protected void updateItem(File item, boolean empty) {
						super.updateItem(item, empty);

						setText((empty || item == null) ? ""
								: (item.getParent() == null) ? item.getAbsolutePath() : item.getName());
					}

				};
			}
		});
		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					TreeItem<File> tmp = (TreeItem<File>) treeView.getSelectionModel().getSelectedItem();
					editor.createNewEditorTab(tmp.getValue());
				}
			}
		});
		return treeView;
	}

	private TreeItem<File> createNode(final File f) {
		return new TreeItem<File>(f) {

			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<File>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;
					super.getChildren().setAll(buildChildren(this));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					File f = (File) getValue();
					isLeaf = f.isFile();
				}

				return isLeaf;
			}

			public String getPath() {
				return f.getName();
			}

			private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
				File f = TreeItem.getValue();
				if (f != null && f.isDirectory()) {
					File[] files = f.listFiles();
					if (files != null) {
						ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();
						ObservableList<TreeItem<File>> childrenDir = FXCollections.observableArrayList();
						for (File childFile : files) {
							if (childFile.isDirectory()) {
								childrenDir.add(createNode(childFile));
							} else {
								children.add(createNode(childFile));
							}
						}
						childrenDir.addAll(children);

						return childrenDir;
					}
				}

				return FXCollections.emptyObservableList();
			}
		};
	}

	public Tab getTab() {

		return this.tab;
	}

	public static void setDefaultPath(String path) {
		SExplorer.DefaultPath = path;
	}
}
