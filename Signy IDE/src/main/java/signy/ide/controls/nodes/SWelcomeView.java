package signy.ide.controls.nodes;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SWelcomeView {

	private GridPane welcomeView;

	public SWelcomeView() {

		GridPane grid = new GridPane();
		grid.setMinSize(0, 0);
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(28);
		grid.setVgap(16);
		grid.setPadding(new Insets(25, 25, 25, 25));

		ImageView im = new ImageView(new Image("icons/logo.png", 229, 259, false, true));   
		grid.add(im, 0, 0, 2, 1);

		Text sceneHelp00 = new Text("New Project");
		sceneHelp00.setFill(Color.web("#838383"));
		sceneHelp00.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp00, 0, 1);
		GridPane.setHalignment(sceneHelp00, HPos.RIGHT);

		Text sceneHelp01 = new Text("Alt+Shift+N");
		sceneHelp01.setFill(Color.web("#838383"));
		sceneHelp01.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp01, 1, 1);
		GridPane.setHalignment(sceneHelp01, HPos.LEFT);

		Text sceneHelp10 = new Text("Go to File Explorer");
		sceneHelp10.setFill(Color.web("#838383"));
		sceneHelp10.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp10, 0, 2);
		GridPane.setHalignment(sceneHelp10, HPos.RIGHT);

		Text sceneHelp11 = new Text("Ctrl+P");
		sceneHelp11.setFill(Color.web("#838383"));
		sceneHelp11.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp11, 1, 2);
		GridPane.setHalignment(sceneHelp11, HPos.LEFT);

		Text sceneHelp20 = new Text("Find in Files");
		sceneHelp20.setFill(Color.web("#838383"));
		sceneHelp20.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp20, 0, 3);
		GridPane.setHalignment(sceneHelp20, HPos.RIGHT);

		Text sceneHelp21 = new Text("Ctrl+F");
		sceneHelp21.setFill(Color.web("#838383"));
		sceneHelp21.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp21, 1, 3);
		GridPane.setHalignment(sceneHelp21, HPos.LEFT);

		Text sceneHelp30 = new Text("Toggle Terminal");
		sceneHelp30.setFill(Color.web("#838383"));
		sceneHelp30.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp30, 0, 4);
		GridPane.setHalignment(sceneHelp30, HPos.RIGHT);

		Text sceneHelp31 = new Text("Ctrl+'");
		sceneHelp31.setFill(Color.web("#838383"));
		sceneHelp31.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp31, 1, 4);
		GridPane.setHalignment(sceneHelp31, HPos.LEFT);

		Text sceneHelp40 = new Text("Build and Run");
		sceneHelp40.setFill(Color.web("#838383"));
		sceneHelp40.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp40, 0, 5);
		GridPane.setHalignment(sceneHelp40, HPos.RIGHT);

		Text sceneHelp41 = new Text("F5");
		sceneHelp41.setFill(Color.web("#838383"));
		sceneHelp41.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
		grid.add(sceneHelp41, 1, 5);
		GridPane.setHalignment(sceneHelp41, HPos.LEFT);

		welcomeView = grid;

	}

	public GridPane getContentPane() {
		return welcomeView;
	}

}
