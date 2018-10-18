package signy.ide.core.module;

import java.io.File;
import java.util.ArrayList;

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
import signy.ide.controls.panes.SEditorPane;

public class SExplorer {
	private Tab tab;
	private SEditorPane editor;
	private static String DefaultPath = "C:\\";

	public SExplorer() {
		this(DefaultPath);
	}

	public SExplorer(String path) {
		this.tab = new Tab();
//		VBox vb = new VBox();
//		File[] drives = File.listRoots();
//		vb.getChildren().add(getTreeView(DefaultPath));
//		if (drives != null && drives.length > 0) {
//		    for (File aDrive : drives) {
//		    	vb.getChildren().add(getTreeView(aDrive.getAbsolutePath()));
//		    }
//		}
//		tab.setContent(vb);
		tab.setContent(getTreeView(DefaultPath));
		tab.setText("Explorer");
	}

	public void addListenerToEditor(SEditorPane editor) {
		this.editor = editor;
	}
	
	public TreeView<File> getTreeView(String path){
		TreeItem<File> root = createNode(new File(path));	
		TreeView<File> treeView = new TreeView<File>(root);
		treeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
		    public TreeCell<File> call(TreeView<File> tv) {
		        return new TreeCell<File>() {

		            @Override
		            protected void updateItem(File item, boolean empty) {
		                super.updateItem(item, empty);

		                setText((empty || item == null) ? "" : (item.getParent() == null)? item.getAbsolutePath() :item.getName());
		            }

		        };
		    }
		});
		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			private TreeItem<File> previous;

			@Override
			public void handle(MouseEvent event) {
				TreeItem<File> tmp = (TreeItem<File>) treeView.getSelectionModel().getSelectedItem();
				if (previous == tmp && tmp.getValue().isFile()) {
					editor.createNewEditorTab(tmp.getValue());
					previous = null;
				} else {
					previous = tmp;
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
