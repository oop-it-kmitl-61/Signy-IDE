package signy.ide;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import signy.ide.controls.panes.dialogs.SLauncherDialog;
import signy.ide.settings.PropertySetting;

public class FXMLSplashController {

	private static Stage stage;

	private static AnchorPane root;

	public static float pbalLevel = 0.1f;

	public void initialize(URL url, ResourceBundle rb) {

	}

	public static void splashScreen() {

		root = new AnchorPane();
		Scene scene = new Scene(root, 300, 350);

		Image image = new Image("icons/splash.png");
		ImageView imageview = new ImageView();
		imageview.setImage(image);

		root.getChildren().add(imageview);

		stage = new Stage();
		stage.getIcons().add(new Image("icons/logo512-16.png"));
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(scene);
		stage.show();
		try {
			Thread.sleep(100L);
			if (PropertySetting.getProperty("usedefaultpath").equals("false")) {
				showLauncherDialog();
			}

			LoadingController.setWorkspacePath(Paths.get(PropertySetting.getProperty("path")));
		} catch (InterruptedException e) {

		}

	}

	public static void hideSplashScreen() {
		stage.hide();
	}

	public static void showSplashScreen() {
		stage.show();
	}

	private static void showLauncherDialog() {
		new SLauncherDialog();
	}

}
