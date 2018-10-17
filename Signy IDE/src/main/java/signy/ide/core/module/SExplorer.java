package signy.ide.core.module;

import java.io.File;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;

public class SExplorer {
	private Tab tab;
	private ArrayList<String> directories;
	private ArrayList<String> files;
	private ListView<String> list;
	private String CurrentPath;
	
	public SExplorer() {
		this("C:\\");
	}
	
	public SExplorer(String path) {
		tab = new Tab();
		getDirectory(path);
		list = getList(path);
		CurrentPath = path;
		tab.setContent(list);
		tab.setText("Explorer " + CurrentPath);
		
		list.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				String selected = list.getSelectionModel().getSelectedItem();
				System.out.println(selected);
				if(selected.startsWith(" > ")) {
					CurrentPath = CurrentPath + selected.substring(3);
					
					System.out.println(CurrentPath);
					
					getDirectory(CurrentPath);
					
					list = getList(CurrentPath);
					
					tab.setContent(list);
				}else if(selected.startsWith(" < ")) {
					 CurrentPath = new File(CurrentPath).getParent();
					
					System.out.println(CurrentPath);
					
					getDirectory(CurrentPath);
					
					list = getList(CurrentPath);
					
					tab.setContent(list);
				}
			}
			
		});
	}
	
	public ListView<String> getList(String path){
		list = new ListView<String>();
		if (new File(path).getParentFile() != null) {
			ObservableList<String> dir = FXCollections.observableArrayList(" < Back ");
			dir.addAll(FXCollections.observableArrayList(directories));
			dir.addAll(FXCollections.observableArrayList(files));
			list.setItems(dir);
		}else {
			ObservableList<String> dir = FXCollections.observableArrayList(directories);
			dir.addAll(FXCollections.observableArrayList(files));
			list.setItems(dir);
		}
		list.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				String selected = list.getSelectionModel().getSelectedItem();
				System.out.println(selected);
				if(selected.startsWith(" > ")) {
					CurrentPath = CurrentPath + selected.substring(3);
					
					System.out.println(CurrentPath);
					
					getDirectory(CurrentPath);
					
					list = getList(CurrentPath);
					
					tab.setContent(list);
				}else if(selected.startsWith(" < ")) {
					 CurrentPath = new File(CurrentPath).getParent();
					
					System.out.println(CurrentPath);
					
					getDirectory(CurrentPath);
					
					list = getList(CurrentPath);
					
					tab.setContent(list);
				}
			}
			
		});
		return list;
		
	}
	
	public void getDirectory(String path) {
		File file = new File(path);
		directories = new ArrayList<String>();
		files = new ArrayList<String>();
		if(file.isDirectory()){
			
			for(File f:file.listFiles()){
				if(f.isDirectory()) {
					directories.add(" > " + f.getName());
				}else {
					files.add(f.getName());
				}
			}
		}
		
	}
	
	public Tab getTab() {
		
		return this.tab;
	}
}
