import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * This class creates the main window for the dialog compiler.
 * @author Alexander Gontcharov
 * @see javax.swing.JFrame
 */
public class MainWindow extends JFrame {
    private static final int WIDTH = 1000;                                          //Width of window
    private static final int HEIGHT = 500;                                          //Height of window
    private static final int NUMBER_OF_CHAR = 20;                                   //Number of visible characters for the text area
    private static final int NUMBER_OF_LINES = 10;                                  //Number of lines for the text area
    private static final String DEFAULT_FILE_NAME = "Dialogc";                      //Default file name
    public static final String DEFAULT_FILE_EXTENSION = ".config";                  //Default file extension

    public static JTextArea message;
    public static JLabel status;
    public static JLabel editor;
    public static String fileName = DEFAULT_FILE_NAME + DEFAULT_FILE_EXTENSION;     //Set to the default name "Dialogc.config"
    private JPanel northPanel;
    private JPanel centerPanel;
    private DialogcMenuBar menuBar;
    
    /**
     * Constructor for this class.
     */
    public MainWindow() {
        super();
        setSize(WIDTH, HEIGHT);
        setTitle("Dialogc");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new CheckOnExit());
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        //Create and set the menu bar
        menuBar = new DialogcMenuBar();
        setJMenuBar(menuBar);
        
        //Create the main window
        createNorthPanel();
        add(northPanel, BorderLayout.NORTH);
        createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Returns the string of the file name without the extension if it contained one.
      *Otherwise return the original string.
     * @param fileName- String containing the file name
     * @return - The string of the file name with or without the extension
     */
    public static String removeExtension(String fileName) {
        int index = fileName.indexOf(".");
        if (index >= 0 ) {
            fileName = fileName.substring(0, index);
        }
        return fileName;
    }

    /**
     * Create the North panel which consists of the button panel and all of its buttons and
     * the editor bar which displays the current file name and its extension.
     */
    private void createNorthPanel() {
        //Create North Panel
        northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        
        //Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(SwingConstants.LEADING));
        
        //New projet button
        ImageIcon newIcon = new ImageIcon("Icons/NewProject.png");
        JButton newButton = new JButton(newIcon);
        newButton.setBackground(buttonPanel.getBackground());
        newButton.setContentAreaFilled(false);
        newButton.setOpaque(true);
        newButton.addActionListener(menuBar.new NewProjectListener());
        
        //Open project button
        ImageIcon openIcon = new ImageIcon("Icons/OpenProject.png");
        JButton openButton = new JButton(openIcon);
        openButton.setBackground(buttonPanel.getBackground());
        openButton.setContentAreaFilled(false);
        openButton.setOpaque(true);
        openButton.addActionListener(menuBar.new OpenListener());
        
        //Save projet button
        ImageIcon saveIcon = new ImageIcon("Icons/SaveProject.png");
        JButton saveButton = new JButton(saveIcon);
        saveButton.setBackground(buttonPanel.getBackground());
        saveButton.setContentAreaFilled(false);
        saveButton.setOpaque(true);
        saveButton.addActionListener(menuBar.new SaveListener());
        
        //Save as project button
        ImageIcon saveAsIcon = new ImageIcon("Icons/SaveAsProject.png");
        JButton saveAsButton = new JButton(saveAsIcon);
        saveAsButton.setBackground(buttonPanel.getBackground());
        saveAsButton.setContentAreaFilled(false);
        saveAsButton.setOpaque(true);
        saveAsButton.addActionListener(menuBar.new SaveAsListener());
        
        //Compile button
        ImageIcon compileIcon = new ImageIcon("Icons/Compile.png");
        JButton compileButton = new JButton(compileIcon); 
        compileButton.setBackground(buttonPanel.getBackground());
        compileButton.setContentAreaFilled(false);
        compileButton.setOpaque(true);
        compileButton.addActionListener(new CompileListener());
        
        //Compile project button
        ImageIcon compileProjectIcon = new ImageIcon("Icons/CompileProject.png");
        JButton compileProjectButton = new JButton(compileProjectIcon);
        compileProjectButton.setBackground(buttonPanel.getBackground());
        compileProjectButton.setContentAreaFilled(false);
        compileProjectButton.setOpaque(true);
        compileProjectButton.addActionListener(null);
        compileProjectButton.addActionListener(menuBar.new CompileAndRunListener());
        
        //Add button panel to Center in North panel
        buttonPanel.add(newButton);
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(saveAsButton);
        buttonPanel.add(compileButton);
        buttonPanel.add(compileProjectButton);
        northPanel.add(buttonPanel, BorderLayout.CENTER);
        
        //Create and add editor bar to South in North panel
        JPanel editorPanel = new JPanel();
        editorPanel.setBorder(new BevelBorder (BevelBorder.LOWERED));
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.X_AXIS));
        editor = new JLabel("File: " + fileName);
        editorPanel.add(editor);
        northPanel.add(editorPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the Center panel which consists of the text area and 
     * the status bar -  displays the current file name and if it has been modified.
     */
    private void createCenterPanel() {
        //Create Center panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        
        //Create and add text area to Center in Center panel
        message = new JTextArea(NUMBER_OF_LINES, NUMBER_OF_CHAR);
        message.getDocument().addDocumentListener(menuBar.new ConfigDocumentListener());
        message.setEditable(true);
        JScrollPane scrolledText = new JScrollPane(message);
        scrolledText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrolledText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(scrolledText, BorderLayout.CENTER);
        
        //Create and add status bar to South in Center panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setLayout(new GridLayout(0, 3));
        status = new JLabel("Current Project: " + removeExtension(fileName));
        status.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(new JPanel());
        statusPanel.add(status);
        statusPanel.add(new JPanel());
        centerPanel.add(statusPanel, BorderLayout.SOUTH);
    }   
    
    /**
     * WindowListener class for exit.
     */
    private class CheckOnExit implements WindowListener {
        @Override
        public void windowOpened(WindowEvent e) {
        }
        @Override
        /**
         * Create and display the confirm window.
         */
        public void windowClosing(WindowEvent e) {
            //Existing file and modified
            if (DialogcMenuBar.newFile == 0 && DialogcMenuBar.fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modified. Would you like to save it?"), "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save
                if (option == JOptionPane.YES_OPTION) {
                    menuBar.saveFile();
                }
            }
            //New file and modified
            else if (DialogcMenuBar.newFile == 1 && DialogcMenuBar.fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modified. Would you like to save it?"), "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save
                if (option == JOptionPane.YES_OPTION) {
                    menuBar.saveAsFile();
                }
            }
            //Create exit confirm window and set it to visible
            ConfirmWindow checkers = new ConfirmWindow();
            checkers.setVisible(true);
        }
        @Override
        public void windowClosed(WindowEvent e) {
        }
        @Override
        public void windowIconified(WindowEvent e) {
        }
        @Override
        public void windowDeiconified(WindowEvent e) {
        }
        @Override
        public void windowActivated(WindowEvent e) {
        }
        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    }
}