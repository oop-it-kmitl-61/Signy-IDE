package signy.ide.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class FileUtil extends ExceptionUtils {

	public static void createFile(Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFolder(Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFolders(Path... paths) {
		for (Path path : paths) {
			createFolder(path);
		}
	}

	public static void deleteFile(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteFile(child);
			}
			file.delete();
		}

		file.delete();
	}

	public static TreeView<File> createDirectoryHierarchy(File rootDir) {
		TreeView<File> treeView = new TreeView<File>();
		TreeItem<File> root = createNode(rootDir);
		root.setExpanded(true);
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
		treeView.setRoot(root);
		return treeView;
	}

	private static TreeItem<File> createNode(final File f) {
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

	public static void expandTree(TreeItem<File> root, File pathToExpand) {
		for (TreeItem<File> child : root.getChildren()) {
			if (pathToExpand.getAbsolutePath().equals(child.getValue().getAbsolutePath())) {
				child.setExpanded(true);
				return;
			} else if (pathToExpand.getAbsolutePath().indexOf(child.getValue().getAbsolutePath()) != -1) {
				child.setExpanded(true);
				expandTree(child, pathToExpand);
			}
		}
	}

	public static ArrayList<String> getAllExpandedTree(TreeView<File> treeView) {
		ArrayList<String> expandedTrees = new ArrayList<String>();
		traverseTree(expandedTrees, treeView.getRoot());
		return expandedTrees;
	}

	private static void traverseTree(ArrayList<String> expandedTrees, TreeItem<File> root) {
		for (TreeItem<File> child : root.getChildren()) {
			if (child.isExpanded()) {
				expandedTrees.add(child.getValue().getAbsolutePath());
				traverseTree(expandedTrees, child);
			}
		}
	}

	public static TreeItem<File> traverse(TreeItem<File> root, File fileToFind) {
		for (TreeItem<File> child : root.getChildren()) {
			if (fileToFind.getAbsolutePath().equals(child.getValue().getAbsolutePath())) {
				return child;
			} else if (fileToFind.getAbsolutePath().indexOf(child.getValue().getAbsolutePath()) != -1) {
				return traverse(child, fileToFind);
			}
		}
		return null;
	}

	public static ArrayList<File> traverse(ArrayList<File> results, File root) {
		for (File child : root.listFiles()) {
			if (child.isDirectory()) {
				traverse(results, child);
			} else {
				if (FilenameUtils.getExtension(child.getName()).equals("java")) {
					results.add(child);
				}
			}
		}
		return results;
	}

}
