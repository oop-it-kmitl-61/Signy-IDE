package signy.ide.core.module;

import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import signy.ide.FXMLDocumentController;
import signy.ide.controls.buttons.ShowReplaceButton;
import signy.ide.controls.nodes.SCodeArea;
import signy.ide.controls.panes.SEditorPane;

public class SSearch {

	private FXMLDocumentController controller;
	private SEditorPane editor;

	private Tab tab;

	private BorderPane searchPane, subSearchPane, searchBox, subSearchBox, replaceBox;
	private AnchorPane buttonPane;

	private HBox menuBox;
	private Label labelSearch;
	private ShowReplaceButton buttonShowReplace;
	private Button button2, buttonRefresh, buttonClear;
	private TextField textFieldSearch, textFieldReplace;
	private ListView<?> resultView;
	

	public SSearch(FXMLDocumentController controller) {

		this.controller = controller;
		editor = controller.getEditorPane();
		
		tab = new Tab("Search");

		searchPane = new BorderPane();
		searchPane.getStyleClass().add("search-pane");
		labelSearch = new Label("SEARCH");

		menuBox = new HBox();
		menuBox.getStyleClass().add("menu-box");
		Region region = new Region();
		buttonRefresh = new Button("R");
		buttonClear = new Button("C");
		menuBox.getChildren().addAll(labelSearch, region, buttonRefresh, buttonClear);
		HBox.setHgrow(region, Priority.ALWAYS);

		searchBox = new BorderPane();
		subSearchBox = new BorderPane();
		searchBox.getStyleClass().add("search-box");

		replaceBox = new BorderPane();
		replaceBox.getStyleClass().add("replace-box");
		buttonShowReplace = new ShowReplaceButton(subSearchBox, replaceBox);
		buttonPane = new AnchorPane();
		buttonPane.getChildren().addAll(buttonShowReplace);
		AnchorPane.setTopAnchor(buttonShowReplace, 0.0);
		AnchorPane.setRightAnchor(buttonShowReplace, 0.0);
		AnchorPane.setLeftAnchor(buttonShowReplace, 0.0);
		AnchorPane.setBottomAnchor(buttonShowReplace, 0.0);
		searchBox.setLeft(buttonPane);
		BorderPane.setMargin(buttonPane, new Insets(0, 2, 0, 0));

		textFieldSearch = new TextField();
		textFieldSearch.setPromptText("Search");

		textFieldReplace = new TextField();
		textFieldReplace.setPromptText("Replace");
		button2 = new Button("H");
		replaceBox.setCenter(textFieldReplace);
		replaceBox.setRight(button2);

		subSearchBox.setCenter(textFieldSearch);

		searchBox.setCenter(subSearchBox);
		resultView = new ListView<Object>();
		resultView.getStyleClass().add("result-pane");

		subSearchPane = new BorderPane();
		subSearchPane.getStyleClass().add("sub-search-pane");
		subSearchPane.setTop(menuBox);
		subSearchPane.setCenter(searchBox);

		searchPane.setTop(subSearchPane);
		searchPane.setCenter(resultView);

		tab.setContent(searchPane);
		ImageView img = new ImageView(new Image("signy/ide/resources/icons/search.png", 14, 14, false, true));
		tab.setGraphic(img);
		
		buttonRefresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String find = textFieldSearch.getText().trim().toLowerCase();
				ArrayList<Integer> pos = new ArrayList<Integer>();
				ArrayList<Integer> li = new ArrayList<Integer>();
				int index = 0, count = 0, line = 0;
				editor.getCurrentActiveTab().getTextArea().getLength();
				System.out.println(editor.getCurrentActiveTab().getTextArea().getText().split("\n").length);
				
				while (line <= editor.getCurrentActiveTab().getTextArea().getText().split("\n").length) {
					if (editor.getCurrentActiveTab().getTextArea().getParagraph(line).getText().toLowerCase().indexOf(find, index) != -1) {
						index = editor.getCurrentActiveTab().getTextArea().getParagraph(line).getText().toLowerCase().indexOf(find, index);
						pos.add(index); li.add(line);
						// System.out.println(index +" "+ line);
						index += 1; count += 1;
					}
					else if (line + 1 == editor.getCurrentActiveTab().getTextArea().getText().split("\n").length) { 
						break;
					}
					else {
						index = 0;
						line += 1;
					}
				}
				
				if (count != 0) {
					for (int i = 0; i < count; i++) {
						System.out.println(pos.get(i) + "," + (pos.get(i)+find.length()+", Line = " + li.get(i)));
			        }
				}
			}

		});
		
		button2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String find = textFieldSearch.getText().trim().toLowerCase();
				String replace = textFieldReplace.getText().trim();
				editor.getCurrentActiveTab().getTextArea().getText().replaceAll(find, replace);
			}

		});
		
		buttonClear.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				textFieldSearch.setText("");
				textFieldReplace.setText("");
			}

		});
		
	}

	public Tab getTab() {
		return tab;
	}

}
