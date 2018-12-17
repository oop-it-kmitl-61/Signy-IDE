package signy.ide;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import signy.ide.lang.Lang;
import signy.ide.settings.PropertySetting;

/**
 * Hello world!
 *
 */
public class Main extends Application {

	private static Main mainApp;
	private int width;
	private int height;

	Stage stage;
	Scene scene;

	private final String APP_NAME = "Signy IDE";
	private String USERNAME;

	static {

		FXMLSplashController.splashScreen();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			LoadingController.setStage(primaryStage);

			mainApp = this;

			USERNAME = "Administrator";

			Parent root = FXMLLoader.load(getClass().getResource("view/FXMLDocument.fxml"));

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

			width = (int) dim.getWidth() - 10;
			height = (int) dim.getHeight() - 65;

			scene = new Scene(root, width, height);
			scene.getStylesheets().add("css/application.css");

			stage = primaryStage;
			primaryStage.initStyle(StageStyle.DECORATED);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.setMinWidth(235);
			primaryStage.setMinHeight(155);
			primaryStage.getIcons().add(new Image("icons/logo.png"));
			setTitle(null);
			primaryStage.show();

//			FXMLDocumentController.init();

			FXMLSplashController.hideSplashScreen();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent event) {
					event.consume();
					FXMLDocumentController.endProcess();
					System.exit(0);
				}
			});
			primaryStage.requestFocus();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Main getMainApp() {
		return mainApp;
	}

	public Stage getStage() {
		return stage;

	}

	public Scene getScene() {
		return scene;
	}

	String getAppName() {
		return APP_NAME;
	}

	public void exit() {
		stage.close();
	}

	public void setTitle(Path path) {
		if (path == null) {
			stage.setTitle("signy-workspace - " + getAppName() + " [" + USERNAME + "]");
		} else {
			stage.setTitle("signy-workspace - " + path + " - " + getAppName() + " [" + USERNAME + "]");
		}

	}

	public static void main(String[] args) {

		Arguments.check(args);
		Internal.init();
		launch(args);
		PropertySetting.writeProperty();

		System.out.println("Hello World!");

	}

}
