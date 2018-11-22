package signy.ide;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

		FXMLSplashController.showSplashScreen();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			mainApp = this;

			USERNAME = "Administrator";

			Parent root = FXMLLoader.load(getClass().getResource("view/FXMLDocument.fxml"));

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

			width = (int) dim.getWidth() - 10;
			height = (int) dim.getHeight() - 65;

			scene = new Scene(root, width, height);
			scene.getStylesheets().add(getClass().getResource("css/application.css").toExternalForm());

			stage = primaryStage;
			primaryStage.initStyle(StageStyle.DECORATED);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.setMinWidth(235);
			primaryStage.setMinHeight(155);
//          primaryStage.getIcons().add(new Image(""));
			setTitle(null);
			primaryStage.show();

			FXMLDocumentController.init();

			FXMLSplashController.hideSplashScreen();

//          primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//              public void handle(WindowEvent event) {
//                  event.consume();
//                  FXMLDocumentController.methodupdateInterrupt();
//                  System.exit(0);
//              }
//          });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Main getMainApp() {
		return mainApp;
	}

	public Stage getStage() {
		return this.stage;

	}

	public Scene getScene() {
		return this.scene;
	}

	String getAppName() {
		return this.APP_NAME;
	}

	public void exit() {
		Platform.exit();
	}

	public void setTitle(Path path) {
		if (path == null) {
			stage.setTitle("signy-workspace - " + getAppName() + " [" + USERNAME + "]");
		} else {
			stage.setTitle("signy-workspace - " + path + " - " + getAppName() + " [" + USERNAME + "]");
		}

	}

	public static void main(String[] args) {

		launch(args);

		System.out.println("Hello World!");

	}

}
