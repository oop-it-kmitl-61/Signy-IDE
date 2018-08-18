package SignyIDE;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SignyIDE {
	
	public final static String APP_NAME = "Signy IDE";
	public static String APP_VER = "0.1";
	public static String FILE_NAME = "Untitled";

	public static void main(String[] args) {
		// Set Default look
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new MainPane().setVisible(true);
	}
}
