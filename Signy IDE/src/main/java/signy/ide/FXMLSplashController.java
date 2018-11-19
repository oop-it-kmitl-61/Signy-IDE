package signy.ide;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXMLSplashController {

	private static Stage stage;

	private static AnchorPane root;

	public static float pbalLevel = 0.1f;

	public void initialize(URL url, ResourceBundle rb) {

		

	}

	public static void showSplashScreen() {

		root = new AnchorPane();
		Scene scene = new Scene(root, 300, 350);

//		image = new Image("com/protectsoft/ide/assets/logomed.png");
//
//		imageview = new ImageView();
//
//		imageview.setImage(image);
//
//		root.getChildren().add(imageview);
//

		stage = new Stage();
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(scene);
		stage.show();

	}

	public static void setText(String s) {

		

	}

	public static void hideSplashScreen() {
		stage.hide();
	}

}
