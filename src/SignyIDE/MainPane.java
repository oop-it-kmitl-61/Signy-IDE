package SignyIDE;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainPane extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TextEditor textEditor;

	protected JPanel contentPane;
	protected final JMenuBar menuBar;
	protected final JMenu mnFile;
	protected final JMenuItem mntmNew, mntmOpen, mntmSave, mntmSaveAs, mntmExit;
	protected File file;

	public MainPane() {

		// Exit when close
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set initial size
		setBounds(0, 0, 900, 600);

		// Set initial title
		setTitle(SignyIDE.FILE_NAME + " - " + SignyIDE.APP_NAME);

		// Centering the windows
		setLocationRelativeTo(null);

		// Create contentPane
		contentPane = new JPanel();
		final JFXPanel fxPanel = new JFXPanel();
		contentPane.add(fxPanel);
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// Set menu bar
		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		setJMenuBar(menuBar);

		// Set menu
		mnFile = new JMenu("File");

		mnFile.setMnemonic(KeyEvent.VK_F);

		menuBar.add(mnFile);

		// Set menu item
		mntmNew = new JMenuItem("New");
		mntmOpen = new JMenuItem("Open...");
		mntmSave = new JMenuItem("Save");
		mntmSaveAs = new JMenuItem("Save As...");
		mntmExit = new JMenuItem("Exit");

		// New file
		mntmNew.setMnemonic(KeyEvent.VK_N);
		mntmNew.setAccelerator(KeyStroke.getKeyStroke('N', KeyEvent.CTRL_DOWN_MASK));
		mntmNew.addActionListener(this);
		mnFile.add(mntmNew);

		// Open file
		mntmOpen.setMnemonic(KeyEvent.VK_O);
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
		mntmOpen.addActionListener(this);
		mnFile.add(mntmOpen);

		// Save file
		mntmSave.setMnemonic(KeyEvent.VK_S);
		mntmSave.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
		mnFile.add(mntmSave);

		// Save file as
		mntmSaveAs.setMnemonic(KeyEvent.VK_A);
		mntmSaveAs.setDisplayedMnemonicIndex(5);
		mntmSaveAs.addActionListener(this);
		mnFile.add(mntmSaveAs);

		// Exit
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mntmExit.addActionListener(this);
		mnFile.add(mntmExit);

		// Add Text Editor Component
		textEditor = new TextEditor();
		contentPane.add(textEditor);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		// Action for New
		if (ev.getSource() == mntmNew) {
			if (textEditor.getText() != null) {

			}

			SignyIDE.FILE_NAME = "Untitled";
			setTitle(SignyIDE.FILE_NAME + " - " + SignyIDE.APP_NAME);
			textEditor.setText(null);
		}

		// ActionListener for Open
		else if (ev.getSource() == mntmOpen) {
			openFileChooser();

		}

		// ActionListener for Save
		else if (ev.getSource() == mntmSave) {

		}

		// ActionListener for Save As
		else if (ev.getSource() == mntmSaveAs) {
			saveFileChooser();
			
		}

		// ActionListener for Exit
		else if (ev.getSource() == mntmExit) {
			System.exit(0);
		}
	}

	public void openFileChooser() {
		// File Chooser Open mode
		setEnabled(false);
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	FileChooser fileChoose = new FileChooser();
        		fileChoose.getExtensionFilters().addAll(
        				new ExtensionFilter("Text Documents (*.txt)", "*.txt"), 
        				new ExtensionFilter("All Files (*.*)", "*.*"));
                file = fileChoose.showOpenDialog(null);

                if (file != null) {
    	        	setTitle(file.getName() + " - " + SignyIDE.APP_NAME);
    	        	textEditor.setText(textEditor.readFile(file));
    	        }

                setEnabled(true);
	        	requestFocus();
            }
       });
	}

	public void saveFileChooser() {
		// File Chooser Save mode
		setEnabled(false);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				FileChooser fileChoose = new FileChooser();
				fileChoose.getExtensionFilters().add(
						new ExtensionFilter("Text Documents (*.txt)", "*.txt"));
				file = fileChoose.showSaveDialog(null);

				if (file != null) {
					textEditor.saveFile(textEditor.getText(), file);
    	        	setTitle(file.getName() + " - " + SignyIDE.APP_NAME);
    	        }

				setEnabled(true);
				requestFocus();
			}
		});
	}
}
