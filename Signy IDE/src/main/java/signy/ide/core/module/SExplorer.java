package signy.ide.core.module;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import signy.ide.FXMLDocumentController;
import signy.ide.controls.panes.SEditorPane;

public class SExplorer {

	private FXMLDocumentController controller;

	private BorderPane explorerPane;
	private HBox menuBox;
	private Button buttonRefresh;
	private Label labelExplorer;

	private Tab tab;
	private SEditorPane editor;
	private String defaultPath;

	public SExplorer(FXMLDocumentController controller) {
		this(controller, controller.getRootDirectory());
	}

	SExplorer(FXMLDocumentController controller, String path) {

		this.controller = controller;
		this.editor = controller.getEditorPane();

		this.defaultPath = controller.getRootDirectory();
		this.tab = new Tab();
		VBox vb = new VBox();
		File[] drives = File.listRoots();
		vb.getChildren().add(getTreeView(defaultPath));
		if (drives != null && drives.length > 0) {
			for (File aDrive : drives) {
				vb.getChildren().add(getTreeView(aDrive.getAbsolutePath()));
			}
		}

		explorerPane = new BorderPane();
		menuBox = new HBox();
		menuBox.getStyleClass().add("menu-box");
		labelExplorer = new Label("EXPLORER");
		Region region = new Region();
		buttonRefresh = new Button("R");
		menuBox.getChildren().addAll(labelExplorer, region, buttonRefresh);
		HBox.setHgrow(region, Priority.ALWAYS);

		explorerPane.setTop(menuBox);
		explorerPane.setCenter(getTreeView(path));
		tab.setContent(explorerPane);
		tab.setText("Explorer");
		ImageView img = new ImageView(new Image("signy/ide/resources/icons/explorer.png", 14, 16, false, false));
		tab.setGraphic(img);
		
		buttonRefresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getTreeView(path);
			}
			
		});

	}
	
	public TreeView<File> getTreeView(String path) {
		TreeItem<File> root = createNode(new File(path));
		root.setExpanded(true);
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
			private TreeItem<File> previous;

			@Override
			public void handle(MouseEvent event) {
				TreeItem<File> tmp = (TreeItem<File>) treeView.getSelectionModel().getSelectedItem();
				if (event.getClickCount() == 2 && tmp.getValue().isFile() && !tmp.getValue().isDirectory()) {
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

	public void setDefaultPath(String path) {
		defaultPath = path;
	}
}
