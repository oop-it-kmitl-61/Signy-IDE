package SignyIDE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextEditor extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TextEditor() {
		// Set initial textEditor
		JTextArea textEditor = new JTextArea(0, 0);
		JScrollPane scrollPane = new JScrollPane();
		textEditor.add(scrollPane);

		add(textEditor);
	}

	String readFile(File file) {
		// Read text file
		StringBuilder stringBuffer = new StringBuilder();
		BufferedReader bufferedRead;

		try {
			bufferedRead = new BufferedReader(new FileReader(file));

			String content;
			while ((content = bufferedRead.readLine()) != null) {
				stringBuffer.append(content + "\n");
			}

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return stringBuffer.toString();
	}

	void saveFile(String content, File file) {
		// Write text file
		try {
			FileWriter fileWrite;
			fileWrite = new FileWriter(file);
			fileWrite.write(content);
			fileWrite.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
