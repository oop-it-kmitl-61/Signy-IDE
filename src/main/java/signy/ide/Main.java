package signy.ide;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.file.Path;

import javax.swing.UIManager;

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

	static {

		FXMLSplashController.showSplashScreen();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {

			mainApp = this;

			Parent root = FXMLLoader.load(getClass().getResource("view/FXMLDocument.fxml"));

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

			width = (int) dim.getWidth() - 10;
			height = (int) dim.getHeight() - 65;

			scene = new Scene(root, width, height);
			scene.getStylesheets().add(getClass().getResource("css/application.css").toExternalForm());

			stage = primaryStage;
			primaryStage.initStyle(StageStyle.DECORATED);
			primaryStage.setScene(scene);
//			primaryStage.getIcons().add(new Image(""));
			setTitle(null);

			FXMLDocumentController.init();
			primaryStage.show();

			FXMLSplashController.hideSplashScreen();

//			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//				public void handle(WindowEvent event) {
//					event.consume();
//                    FXMLDocumentController.methodupdateInterrupt();
//					System.exit(0);
//				}
//			});

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

	public void exit() {
		Platform.exit();
	}

	public void setTitle(Path path) {
		if (path == null) {
			stage.setTitle("signy-workspace - Signy IDE [Administrator]");
		} else {
			stage.setTitle("signy-workspace - " + path + " - Signy IDE [Administrator]");
		}

	}

	public static void main(String[] args) {

		launch(args);

		System.out.println("Hello World!");

	}

}
