package signy.ide.core.module;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;
import signy.ide.FXMLDocumentController;
import signy.ide.LoadingController;
import signy.ide.controls.panes.SEditorPane;
import signy.ide.controls.panes.dialogs.NewClassDialog;
import signy.ide.controls.panes.dialogs.NewFileDialog;
import signy.ide.controls.panes.dialogs.NewJavaPackageDialog;
import signy.ide.controls.panes.dialogs.NewJavaProjectDialog;
import signy.ide.core.resources.SFile;
import signy.ide.core.resources.SProject;
import signy.ide.utils.FileUtil;

public class SExplorer {

	private FXMLDocumentController controller;

	private BorderPane explorerPane;
	private HBox menuBox;
	private Button buttonRefresh;
	private Label labelExplorer;
	private TreeView<File> explorerView;
	private static ArrayList<String> expandTmp;

	private Tab tab;
	private SEditorPane editor;
	private String defaultPath;

	static {

		expandTmp = new ArrayList<String>();

	}

	public SExplorer(FXMLDocumentController controller) {
		this(controller, controller.getRootDirectory());
	}

	SExplorer(FXMLDocumentController controller, String path) {

		this.controller = controller;
		this.editor = controller.getEditorPane();

		this.defaultPath = controller.getRootDirectory();
		this.tab = new Tab();

		explorerPane = new BorderPane();
		menuBox = new HBox();
		menuBox.getStyleClass().add("menu-box");
		labelExplorer = new Label("EXPLORER");
		Region region = new Region();
		buttonRefresh = new Button();
		buttonRefresh.setGraphic(new ImageView(new Image("icons/reffresh64.png", 16, 16, true, false)));
		menuBox.getChildren().addAll(labelExplorer, region, buttonRefresh);
		HBox.setHgrow(region, Priority.ALWAYS);

		explorerView = getTreeView(LoadingController.getWorkspacePath().toString());

		explorerPane.setTop(menuBox);
		explorerPane.setCenter(explorerView);
		tab.setContent(explorerPane);
		tab.setText("Explorer");
		ImageView img = new ImageView(new Image("icons/explorer.png", 14, 16, false, false));
		tab.setGraphic(img);

		buttonRefresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				refresh(LoadingController.getWorkspacePath().toString());
			}

		});

		explorerView.setContextMenu(createContextMenu());

	}

	private ContextMenu createContextMenu() {
		ContextMenu menu = new ContextMenu();
		Menu newMenu = new Menu("New");
		MenuItem newJavaProjectMnItem = new MenuItem("Java Project");
		newJavaProjectMnItem.setOnAction(e -> {
			new NewJavaProjectDialog();
		});
		MenuItem newPackageMnItem = new MenuItem("Package");
		newPackageMnItem.setOnAction(e -> {
			new NewJavaPackageDialog();
		});
		MenuItem newClassMnItem = new MenuItem("Class");
		newClassMnItem.setOnAction(e -> {
			new NewClassDialog();
		});
		MenuItem newFolderMnItem = new MenuItem("Folder");
		MenuItem newFileMnItem = new MenuItem("File");
		newFileMnItem.setOnAction(e -> {
			new NewFileDialog();
		});

		newMenu.getItems().addAll(newJavaProjectMnItem, newPackageMnItem, newClassMnItem, newFolderMnItem,
				newFileMnItem);

		MenuItem deleteMnItem = new MenuItem("Delete");
		deleteMnItem.setOnAction(e -> {
			TreeItem<File> selectedItem = explorerView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.getDialogPane().getStylesheets().add("css/dialog.css");
				alert.getDialogPane().getStyleClass().add("content-panel");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.resizableProperty().setValue(true);
				stage.getIcons().add(new Image("icons/logo.png"));
				alert.setTitle("Delete");

				if (selectedItem.getValue().isDirectory()) {
					alert.setHeaderText(
							"Are you sure you want to delete folder '" + selectedItem.getValue().getName() + "'?");
				} else {
					alert.setHeaderText(
							"Are you sure you want to delete file '" + selectedItem.getValue().getName() + "'?");
				}

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					FileUtil.deleteFile(selectedItem.getValue());
					refresh(LoadingController.getWorkspacePath().toString());
				}
			}
		});
		deleteMnItem.setAccelerator(KeyCombination.keyCombination("DELETE"));

		menu.getItems().addAll(newMenu, deleteMnItem);
		return menu;
	}

	public void refresh(String path) {
		expandTmp = FileUtil.getAllExpandedTree(explorerView);
		explorerView.getRoot().getChildren().clear();
		explorerView = getTreeView(path);
		Platform.runLater(() -> explorerPane.setCenter(explorerView));
		explorerView.setContextMenu(createContextMenu());
	}

	public TreeView<File> getTreeView(String path) {
		TreeItem<File> root = new TreeItem<File>(new File(path));
		for (File projectDir : new File(path).listFiles()) {
			if (Files.exists(Paths.get(projectDir.getAbsolutePath(), ".project"))) {
				SProject project = new SProject(projectDir.getName(), projectDir.toPath());
				TreeItem<File> projectItem = new TreeItem<File>(project);
				if (expandTmp.contains(project.getAbsolutePath())) {
					projectItem.setExpanded(true);
				}
				root.getChildren().add(projectItem);
				for (File child : project.listFiles()) {
					if (!child.getName().equals(".project")) {
						projectItem.getChildren().add(createNode(project, child));
					}
				}
			}
		}
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
				if (event.getClickCount() == 2) {
					if (tmp != null) {
						if (tmp.getValue() instanceof SFile && tmp.getValue().isFile()) {
							SFile file = (SFile) tmp.getValue();
							editor.createNewEditorTab(file.getRootProject(), file);
						}
					}
				}

			}

		});
		return treeView;
	}

	private TreeItem<File> createNode(SProject rootProject, final File f) {
		SFile sFile = new SFile(rootProject, f.toPath());
		TreeItem<File> item = new TreeItem<File>(sFile) {

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
								childrenDir.add(createNode(rootProject, childFile));
							} else {
								children.add(createNode(rootProject, childFile));
							}
						}
						childrenDir.addAll(children);

						return childrenDir;
					}
				}

				return FXCollections.emptyObservableList();
			}

		};

		if (expandTmp.contains(sFile.getAbsolutePath())) {
			item.setExpanded(true);
		}
		return item;
	}

	public Tab getTab() {
		return this.tab;
	}

	public TreeView<File> getExplorerView() {
		return explorerView;
	}

	public void setDefaultPath(String path) {
		defaultPath = path;
	}
}
