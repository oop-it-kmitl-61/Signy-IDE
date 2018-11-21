package signy.ide.core.module;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import signy.ide.controls.buttons.ShowReplaceButton;

public class SSearch {

	private Tab tab;

	private BorderPane searchPane, subSearchPane, searchBox, subSearchBox, replaceBox;
	private AnchorPane buttonPane;

	private HBox menuBox;
	private Label labelSearch;
	private ShowReplaceButton buttonShowReplace;
	private Button button2, buttonRefresh, buttonClear;
	private TextField textFieldSearch, textFieldReplace;
	private ListView<?> resultView;

	public SSearch() {

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

	}

	public Tab getTab() {
		return tab;
	}

}
