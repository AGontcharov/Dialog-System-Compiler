import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.BoxLayout;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * A class that creates the menu for the Dialog compiler.
 * @author Alexander Gontcharov
 * @see javax.swing.JMenuBar
 */
public class DialogcMenuBar extends JMenuBar {
    public static String guiTitle;                                          //Title of the gui to be compiled
    public static ArrayList<String> guiFields;                              //The fields of the gui to be compiled
    public static ArrayList<String> guiButtons;                             //The buttons of the gui to be compiled
    public static ArrayList<String> guiFieldTypes;                          //The label types of the fields
    public static ArrayList<String> guiActionListeners;                     //The action listeners of the buttons
    public static int fileModified = 0;                                     //Flag for changes in a file
    public static int newFile = 1;                                          //Flag for new file
    public static String absoluteFilePath;                                  //The absolute path of the file
    public static String currentWorkingDirectory;                           //The current working directory of the Dialog Compiler
    public static String yadcPath;                                          //The path to the yadc executable
    private static String readMePath;                                       //The path to the README.txt
    public static String currentFileDirectory;                              //The current file directory of the config file
    public static String javaCompilerPath = "/usr/bin/javac";               //The Java compiler path
    public static String javaCompilerOptions;                               //The Java compiler arguments/options
    public static String javaRunTimePath = "/usr/bin/java";                 //The Java run time path
    public static String javaRunTimeOptions;                                //The Java run time arguments/options
    public static int lexYaccCompiler = 1;                                  //Lex Yacc compiler flag.
    public static int IDECompiler = 0;                                      //IDE compiler flag.
    private JLabel currentJavaCompiler;                                     //The current java compiler path
    private JLabel currentJavaCompilerOptionsSetting;                       //The current java compiler arguments/options
    private JLabel currentJavaRunTime;                                      //The current java run time path
    private JLabel currentJavaRunTimeOptionsSetting;                        //The current java run time arguments/options
    private JLabel currentWorkingDirectorySetting;                          //The current working directory of the Dialog Compiler
    private JLabel compileMode;                                             //Label for compile mode menu item
    private JRadioButtonMenuItem lexYaccRadioButton;                        //The radio button for Lex and Yacc version of the compiler
    private JRadioButtonMenuItem IDERadioButton;                            //Teh radio button for IDE version of the compiler
    private JFileChooser chooser;                                           //The default JFileChooser
    private JMenu fileMenu;
    private JMenu compilerMenu;
    private JMenu configMenu;
    private JMenu helpMenu;
    
    /**
     * Constructor for this class.
     */
    public DialogcMenuBar() {
        super();
        
        //Get the absolute path to the current diretory and set it for the JFIleChooser
        currentWorkingDirectory = System.getProperty("user.dir");
        yadcPath = currentWorkingDirectory + "/yadc";
        readMePath = currentWorkingDirectory + "/README.txt";
        chooser = new JFileChooser(currentWorkingDirectory);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Config file", "config"));

        //Initialize the Java compiler arguments
        javaCompilerOptions = "-cp " + currentWorkingDirectory + "/" + " -d " + currentWorkingDirectory + "/ "
        + currentWorkingDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName) + "/" + MainWindow.removeExtension(MainWindow.fileName) + ".java";

        //Initialize the Java run time arguments
        javaRunTimeOptions = " -cp " + currentWorkingDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName) + ":" + currentWorkingDirectory + "/"
        + MainWindow.removeExtension(MainWindow.fileName) +  " " + MainWindow.removeExtension(MainWindow.fileName);

        //Create File menu and add it menu bar
        createFileMenu();
        add(fileMenu);
        
        //Create Compiler menu and add it menu bar
        createCompilerMenu();
        add(compilerMenu);
        
        //Create Config menu and add it menu bar
        createConfigMenu();
        add(configMenu);
        
        //Create Help menu and add it menu bar
        createHelpMenu();
        add(helpMenu);
    }
    
    /**
     * Creates the File menu and all of its menu items.
     */
    private void createFileMenu() {
        fileMenu = new JMenu("File");
        
        //New project menu item CTRL + N shortcut
        JMenuItem newProject = new JMenuItem("New");
        newProject.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        newProject.addActionListener(new NewProjectListener());
        
        //Open project menu item CTRL + O shortcut
        JMenuItem open = new JMenuItem("Open");
        open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        open.addActionListener(new OpenListener());

        //Save project CTRL + S shortcut
        JMenuItem save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        save.addActionListener(new SaveListener());

        //Save As project CTRL + SHIFT + S shortcut
        JMenuItem saveAs = new JMenuItem("Save As");
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, false));
        saveAs.addActionListener(new SaveAsListener());

        //Quit menu item CTRL + Q shortcut
        JMenuItem quit = new JMenuItem("Quit");
        quit.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        quit.addActionListener(new QuitListener());
        
        //Add to File menu
        fileMenu.add(newProject);
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(quit);
    }
    
    /**
     * Creates the Compiler menu and all of its menu items.
     */
    private void createCompilerMenu() {
        compilerMenu = new JMenu("Compile");
        
        //Compile menu item CTRL + SHIFT + C shortcut
        JMenuItem compile = new JMenuItem("Compile");
        compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, false));
        compile.addActionListener(new CompileListener());

        //Compile and Run menu item CTRL + R shortcut
        JMenuItem compileAndRun = new JMenuItem("Compile and Run");
        compileAndRun.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        compileAndRun.addActionListener(new CompileAndRunListener());
        
        //Add to Compile menu
        compilerMenu.add(compile);
        compilerMenu.add(compileAndRun);
    }
    
    /**
     * Creates the Config menu and all of its menu items.
     */
    private void createConfigMenu() {
        configMenu = new JMenu("Config");
        
        //Java Compiler menu item CTRL + 1 shortcut
        JMenuItem javaCompiler = new JMenuItem("Java Compiler");
        javaCompiler.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        javaCompiler.addActionListener(new JavaCompilerListener());

        //Display the current Java compiler
        currentJavaCompiler = new JLabel("    " + javaCompilerPath);
        currentJavaCompiler.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentJavaCompiler.setBackground(Color.LIGHT_GRAY);
        currentJavaCompiler.setOpaque(true);

        //Compiler Options menu item CTRL + 2 shortcut
        JMenuItem compileOptions = new JMenuItem("Compile Options");
        compileOptions.setAccelerator(KeyStroke.getKeyStroke('2', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        compileOptions.addActionListener(new CompileOptionsListener());

        //Display the current Java compiler options/arguments
        currentJavaCompilerOptionsSetting = new JLabel("    " + javaCompilerOptions);
        currentJavaCompilerOptionsSetting.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentJavaCompilerOptionsSetting.setBackground(Color.LIGHT_GRAY);
        currentJavaCompilerOptionsSetting.setOpaque(true);

        //Java Run Time menu item CTRL + 3 shortcut
        JMenuItem javaRunTime = new JMenuItem("Java Run Time");
        javaRunTime.setAccelerator(KeyStroke.getKeyStroke('3', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        javaRunTime.addActionListener(new JavaRunTimeListener());

        //Display the current Java run time
        currentJavaRunTime = new JLabel("    " + javaRunTimePath);
        currentJavaRunTime.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentJavaRunTime.setBackground(Color.LIGHT_GRAY);
        currentJavaRunTime.setOpaque(true);

        //Run Time Options menu item CTRL + 4 shortcut
        JMenuItem runTimeOptions = new JMenuItem("Run-Time Options");
        runTimeOptions.setAccelerator(KeyStroke.getKeyStroke('4', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        runTimeOptions.addActionListener(new RunTimeOptionsListener());

        //Display the current Java run time options/arguments
        currentJavaRunTimeOptionsSetting = new JLabel("    " + javaRunTimeOptions);
        currentJavaRunTimeOptionsSetting.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentJavaRunTimeOptionsSetting.setBackground(Color.LIGHT_GRAY);
        currentJavaRunTimeOptionsSetting.setOpaque(true);

        //Working Directory menu item CTRL + 5 shortcut
        JMenuItem workingDirectory = new JMenuItem("Working Directory");
        workingDirectory.setAccelerator(KeyStroke.getKeyStroke('5', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        workingDirectory.addActionListener(new WorkingDirectoryListener());

        //Display the working directory
        currentWorkingDirectorySetting = new JLabel("    " + currentWorkingDirectory);
        currentWorkingDirectorySetting.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentWorkingDirectorySetting.setBackground(Color.LIGHT_GRAY);
        currentWorkingDirectorySetting.setOpaque(true);

        //Display the compile mode
        JLabel compileMode = new JLabel("     Compile Mode");

        //Lex/Yacc radio button
        lexYaccRadioButton = new JRadioButtonMenuItem("Lex/Yacc compiler", true);
        lexYaccRadioButton.setAccelerator(KeyStroke.getKeyStroke('6', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        lexYaccRadioButton.addActionListener(new LexYaccCompilerListener());

        //IDE radio button
        IDERadioButton = new JRadioButtonMenuItem("IDE compiler");
        IDERadioButton.setAccelerator(KeyStroke.getKeyStroke('7', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        IDERadioButton.addActionListener(new IDECompilerListener());

        //Add to Config menu
        configMenu.add(javaCompiler);
        configMenu.add(currentJavaCompiler);
        configMenu.add(compileOptions);
        configMenu.add(currentJavaCompilerOptionsSetting);
        configMenu.add(javaRunTime);
        configMenu.add(currentJavaRunTime);
        configMenu.add(runTimeOptions);
        configMenu.add(currentJavaRunTimeOptionsSetting);
        configMenu.add(workingDirectory);
        configMenu.add(currentWorkingDirectorySetting);
        configMenu.add(compileMode);
        configMenu.add(lexYaccRadioButton);
        configMenu.add(IDERadioButton);

    }
    
    /**
     * Creates the Help menu and all of its menu items.
     */
    private void createHelpMenu() {
        helpMenu = new JMenu("Help");
        
        //Help menu item CTRL + 6 shortcut
        JMenuItem help = new JMenuItem("Help");
        help.setAccelerator(KeyStroke.getKeyStroke('8', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        help.addActionListener(new HelpListener());

        //About menu item CTRL + 7 shortcut
        JMenuItem about = new JMenuItem("About");
        about.setAccelerator(KeyStroke.getKeyStroke('9', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        about.addActionListener(new AboutListener());
        
        //Add to Help menu
        helpMenu.add(help);
        helpMenu.add(about);
    }
    
    /**
     * Creates a new project.
     */
    public void newProject() {
        //Creates a dialogc with prompting for a filename appending the file extension if it doesn't exist
        JLabel newFileMessage = new JLabel("Enter a name for the project: ");
        JTextField newFileName = new JTextField();
        Object msg[] = {newFileMessage, newFileName};
        int option = JOptionPane.showConfirmDialog(null, msg, "New File", JOptionPane.OK_CANCEL_OPTION);

        //Get the new file name
        if (option == JOptionPane.OK_OPTION) {
            MainWindow.fileName = newFileName.getText();
            if (!MainWindow.fileName.contains(".")) {
                MainWindow.fileName = MainWindow.fileName + MainWindow.DEFAULT_FILE_EXTENSION;
            }
            currentFileDirectory = currentWorkingDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName);

            //Update Java compiler and run time arguments/options
            javaCompilerOptions = "-cp " + currentFileDirectory + "/" + " -d " + currentFileDirectory + "/ "
            + currentFileDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName) + ".java";
            javaRunTimeOptions = "-cp " + currentFileDirectory + ":" + currentFileDirectory + " " + MainWindow.removeExtension(MainWindow.fileName);

            //Reset the document listerner for the text area, update the file flags and settings
            currentJavaCompilerOptionsSetting.setText("    " + javaCompilerOptions);
            currentJavaRunTimeOptionsSetting.setText("    " + javaRunTimeOptions);
            MainWindow.message.setText(null);
            MainWindow.message.getDocument().addDocumentListener(new ConfigDocumentListener());
            MainWindow.editor.setText("File: " + MainWindow.fileName);
            MainWindow.status.setText("Current Project: " + MainWindow.removeExtension(MainWindow.fileName));
            newFile = 1;
            fileModified = 0;
        }
    }
     
    /**
     * Opens an existing project.
     */
    public void openProject() {
        //Get the file to open
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File currentFile = chooser.getSelectedFile();
            absoluteFilePath = currentFile.getPath();
            MainWindow.fileName = currentFile.getName();
            MainWindow.message.setText(null);
            currentFileDirectory = currentWorkingDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName);

            //Update Java compiler and run time arguments/options
            javaCompilerOptions = "-cp " + currentFileDirectory + "/" + " -d " + currentFileDirectory + "/ "
            + currentFileDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName) + ".java";
            javaRunTimeOptions = "-cp " + currentFileDirectory + ":" + currentFileDirectory + " " + MainWindow.removeExtension(MainWindow.fileName);

            //Try to read the file into the text area
            try {
                FileReader reader = new FileReader(currentFile);
                MainWindow.message.read(reader, "Some file");
            }
            //Catch FileNotFoundException
            catch(Exception ex) {
                System.err.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "File was not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Reset the document listerner for the text area, update the file flags and settings
            currentJavaCompilerOptionsSetting.setText("    " + javaCompilerOptions);
            currentJavaRunTimeOptionsSetting.setText("    " + javaRunTimeOptions);
            MainWindow.message.getDocument().addDocumentListener(new ConfigDocumentListener());
            MainWindow.editor.setText("File: " + MainWindow.fileName);
            MainWindow.status.setText("Current Project: " + MainWindow.removeExtension(MainWindow.fileName));
            fileModified = 0;
            newFile = 0;
        }
    }
    
    /**
     * Save current file.
     */
    public static void saveFile() {
        //Get the save file
        File saveFile = new File(absoluteFilePath);
        BufferedWriter outFile = null;

        //Save the file
        try {
            outFile = new BufferedWriter(new FileWriter(saveFile));
            MainWindow.message.write(outFile);
        }
        //Catch IOException
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An unexpected error occured during saving.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //Flush and close the stream
        finally {
            if (outFile != null) {
                try {
                   outFile.close();
                } 
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "An unexpected error occured during saving.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        //Reset the document listerner for the text area and update the file flags
        MainWindow.editor.setText("File: " + MainWindow.fileName);
        MainWindow.status.setText("Current Project: " + MainWindow.removeExtension(MainWindow.fileName));
        fileModified = 0;
        newFile = 0;
    } 
     
    /**
     * Safe current file with new name.
     */
     public void saveAsFile() {
        boolean acceptable = false;

        //Set the file in the JFileChooser text field
        File currentFile = new File(MainWindow.fileName);
        chooser.setSelectedFile(currentFile);

        //Get the save file
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File saveFile = new File(chooser.getSelectedFile().getPath());
            absoluteFilePath = saveFile.getPath();
            BufferedWriter outFile = null;
            MainWindow.fileName = saveFile.getName();
            currentFileDirectory = currentWorkingDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName);

            //Update Java compiler and run time arguments/options
            javaCompilerOptions = "-cp " + currentFileDirectory + "/" + " -d " + currentFileDirectory + "/ "
            + currentFileDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName) + ".java";
            javaRunTimeOptions = "-cp " + currentFileDirectory + ":" + currentFileDirectory + " " + MainWindow.removeExtension(MainWindow.fileName);

            //Save the file
            try {
                outFile = new BufferedWriter(new FileWriter(saveFile));
                MainWindow.message.write(outFile);
            }
            //Catch IOException
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "An unexpected error occured during saving.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //Flush and close the stream
            finally {
                if (outFile != null) {
                    try {
                       outFile.close();
                    } 
                    //Catch IOException
                    catch (Exception ex) {
                        System.err.println(ex.getMessage());
                        JOptionPane.showMessageDialog(null, "An unexpected error occured during saving.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        //Update the file flags and settings
        currentJavaCompilerOptionsSetting.setText("    " + javaCompilerOptions);
        currentJavaRunTimeOptionsSetting.setText("    " + javaRunTimeOptions);
        MainWindow.editor.setText("File: " + MainWindow.fileName);
        MainWindow.status.setText("Current Project: " + MainWindow.removeExtension(MainWindow.fileName));
        fileModified = 0;
        newFile = 0;
        }
    }
    
    /**
     * ActionListener class for New.
     */
    public class NewProjectListener implements ActionListener {
        @Override
        /**
         * Prompts a dialog depending if the file is modified or not and invokes the newProject method.
         */
        public void actionPerformed(ActionEvent e) {
            //Existing file and modified
            if (newFile == 0 && fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modifired. Would you like to save it?"), 
                    "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save file and start new project
                if (option == JOptionPane.YES_OPTION) {
                    saveFile();
                    newProject();
                }
                //Start new project
                else {
                    newProject();
                }
            }
            //New file and modified
            else if (newFile == 1 && fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modifired. Would you like to save it?"), 
                    "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save file and start new project
                if (option == JOptionPane.YES_OPTION) {
                    saveAsFile();
                    newProject();
                }
                //Start new project
                else {
                    newProject();
                }
            }
            //File has not been modified
            else {
                newProject();
            }
        }
    }
    
    /**
     * ActionListener class for Open.
     */
    public class OpenListener implements ActionListener {
        @Override
        /**
         * Prompts a dialog depending if the file is modified or not and invokes the openProject method.
         */
        public void actionPerformed(ActionEvent e) {
            //Existing file and modified
            if (newFile == 0 && fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modified. Would you like to save it?"), 
                    "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save and open file
                if (option == JOptionPane.YES_OPTION) {
                    saveFile();
                    openProject();
                }
                //Open file
                else {
                    openProject();
                }
            }
            //New file and modified
            else if (newFile == 1 && fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modified. Would you like to save it?"), 
                    "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save and open file
                if (option == JOptionPane.YES_OPTION) {
                    saveAsFile();
                    openProject();
                }
                //Open file
                else {
                    openProject();
                }
            }
            //File has not been modified
            else {
                openProject();
            }
        }
    }
    
     /**
      * ActionListener class for Save.
      */
    public class SaveListener implements ActionListener {
        @Override
        /**
         * If the file is new invokes the saveAsFile method otherwise invokes the saveFile method.
         */
        public void actionPerformed(ActionEvent e) {
            //Existing file
            if (newFile == 0) {
                saveFile();
            }
            //New file
            else {
                saveAsFile();
            }
        }
    }
    
    /**
     * ActionListener class for Save As.
     */
    public class SaveAsListener implements ActionListener {
        @Override
        /**
         * Invokes the saveAsFile method.
         */
        public void actionPerformed(ActionEvent e) {
            saveAsFile();
        }
    }
    
    /**
     * ActionListener class for Quit.
     */
    private class QuitListener implements ActionListener {
        @Override
        /**
         * Creates the confirm exit window.
         */
        public void actionPerformed(ActionEvent e) {
            //Existing file and modified
            if (newFile == 0 && fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modified. Would you like to save it?"), 
                    "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save
                if (option == JOptionPane.YES_OPTION) {
                    saveFile();
                }
            }
            //New file and modified
            else if (newFile == 1 && fileModified == 1) {
                int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modified. Would you like to save it?"), 
                    "File Modified", JOptionPane.YES_NO_OPTION);
                
                //Save
                if (option == JOptionPane.YES_OPTION) {
                    saveAsFile();
                }
            }
            //Create exit confirm window and set it to visible
            ConfirmWindow checkers = new ConfirmWindow();
            checkers.setVisible(true);
        }
    } 
    
    /**
     * ActionListener class for Working Directory.
     */
    private class WorkingDirectoryListener implements ActionListener {
        @Override
        /**
         * Creates a dialog that displays the current working directory and components to update it.
         */
        public void actionPerformed(ActionEvent e) {
            File directory;
            JLabel currentWorkingDirectoryMessage = new JLabel("The current working directory is: " + currentWorkingDirectory);
            JTextField newWorkingDirectory = new JTextField(currentWorkingDirectory); 
            Object msg[] = {currentWorkingDirectoryMessage, newWorkingDirectory};
            int option = JOptionPane.showConfirmDialog(null, msg, "Working Directory", JOptionPane.OK_CANCEL_OPTION);
            
            //Get the new directory and create it if it doesn't exist
            if (option == JOptionPane.OK_OPTION) {
                try {
                    directory = new File(newWorkingDirectory.getText());
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                }
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "An unexpected error occured whilst changing working directories.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //Update the current working directory
                currentWorkingDirectory = directory.getPath();
                currentWorkingDirectorySetting.setText("     " + currentWorkingDirectory);
            }
        }
    } 
    
    /**
     * ActionListener class for About.
     */
    private class AboutListener implements ActionListener {
        //About the author  
        private final String aboutMessage = "CIS*2750 - Software Systems Development and Integration - W15\n"
                + "School of Computer Science\n"
                + "University of Guelph\n"
                + "Author: Alexander Gontcharov\n"
                + "Student ID: 0814685";
        @Override
        /**
         * Creates a dialog with the students information.
         */
        public void actionPerformed(ActionEvent e) {
             JOptionPane.showMessageDialog(null, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE);
        }
    } 
    
    /**
     * ActionListener class for Help.
     */
    private class HelpListener implements ActionListener {
        private static final int NUMBER_OF_CHAR = 75;                       //Number of visible characters for the text area
        private static final int NUMBER_OF_LINES = 40;                      //Number of lines for the text area

        @Override
        /**
         * Opens the README.txt in the text area.
         */
        public void actionPerformed(ActionEvent e) {

            JPanel readMe = new JPanel();

            //Text area for the README.txt
            JPanel centerPanel = new JPanel();
            JTextArea message = new JTextArea(NUMBER_OF_LINES, NUMBER_OF_CHAR);
            message.setEditable(false);
            JScrollPane scrolledText = new JScrollPane(message);
            scrolledText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrolledText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            centerPanel.add(scrolledText, BorderLayout.CENTER);

            readMe.add(centerPanel);

            //Try to read the file into the text area
            try {
                FileReader reader = new FileReader(readMePath);
                message.setText(null);
                message.read(reader, "Some file");
            }
            //Catch FileNotFoundException
            catch(Exception ex) {
                System.err.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "Read me was not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(null, readMe, "Help", JOptionPane.PLAIN_MESSAGE);
        }
    } 
    
    /**
     * ActionListener class for the text area.
     */
    public class ConfigDocumentListener implements DocumentListener {
        @Override
        /**
         * Set's the file to modified if something in the text area has been removed.
         */
        public void removeUpdate(DocumentEvent e) {
            fileModified = 1;
            MainWindow.status.setText("Current Project: " + MainWindow.removeExtension(MainWindow.fileName) + "[modified]");
        }

        @Override
        /**
         * Set's the file to modified if something in the text area has been updated.
         */
        public void insertUpdate(DocumentEvent e) {
            fileModified = 1;
            MainWindow.status.setText("Current Project: " + MainWindow.removeExtension(MainWindow.fileName) + "[modified]");
        }
        @Override
        public void changedUpdate(DocumentEvent arg0) {
            
        } 
    }

    /**
     * ActionListener class for Compile and Run.
     */
    public class CompileAndRunListener implements ActionListener {
        @Override
        /**
         * Invokes the first and second parse methods and creates the GUI. 
         * If the file is new or modified a dialog is prompt with a message.
         */
        public void actionPerformed(ActionEvent e) {
            CompileListener listener = new CompileListener();
            Process p;
            String line;

            //Compile the config files
            boolean result = listener.compile();
            if (!result && DialogcMenuBar.newFile == 1) {
                JOptionPane.showMessageDialog(null, "No file has been created or opened.", "No File", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            else if (!result) {
                JOptionPane.showMessageDialog(null, "An unexpected error occured during compilation.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Create class files
            try {
                String command = javaCompilerPath + " " + javaCompilerOptions;
                System.out.println("Running: " + command + "\n");
                p = Runtime.getRuntime().exec(command); 
                p.waitFor();
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "An unexpected error occured during run time.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
               
            if (p.exitValue() != 0) {
                 //Print the error stream from the processor to the screen
                BufferedReader errStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                line = "";
                try {
                    while ((line = errStream.readLine()) != null) {
                        System.err.println(line);
                    }
                }
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "An unexpected error occured during run time.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(null, "An unexpected error occured during run time.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Run the class file
            try {
                String command = javaRunTimePath + " " + javaRunTimeOptions;
                System.out.println("Running: " + command + "\n");
                p = Runtime.getRuntime().exec(command); 
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "An unexpected error occured during run time.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Print the error stream from the processor to the screen
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            line = "";
            try {
                while ((line = errorStream.readLine()) != null) {
                    System.err.println(line);
                }
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
                JOptionPane.showMessageDialog(null, "An unexpected error occured during run time.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } 

    /**
     * ActionListener class for Java Compiler.
     */
    private class JavaCompilerListener implements ActionListener {
        @Override
        /**
         * Create a dialog that displays the current Java compiler and components to update it.
         */
        public void actionPerformed(ActionEvent e) {
            JLabel currentCompiler = new JLabel("Current external java compiler: " + javaCompilerPath);
            JTextField newCompiler = new JTextField(javaCompilerPath);  
            JButton searchCompiler = new JButton("..");
            searchCompiler.addActionListener(new SearchJavaCompiler());

            //Create a panel for the Java compiler dialog 
            JPanel compilePanel = new JPanel();
            compilePanel.setLayout(new BorderLayout());

            //Add the label and text field to the center panel
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.add(currentCompiler);
            centerPanel.add(newCompiler);

            //Add the file dialog button to the south panel
            JPanel southPanel = new JPanel();
            southPanel.add(searchCompiler);

            compilePanel.add(centerPanel, BorderLayout.CENTER);
            compilePanel.add(southPanel, BorderLayout.SOUTH);
        
            //Open the dialogc for Java compiler 
            int option = JOptionPane.showConfirmDialog(null, compilePanel, "Java Compiler", JOptionPane.OK_CANCEL_OPTION);

            //Updated the Java compiler path
            if (option == JOptionPane.OK_OPTION) {
                javaCompilerPath = newCompiler.getText();
                currentJavaCompiler.setText("     " + javaCompilerPath);
            }
        }

        /**
         * ActionListener class for search compiler button.
         */
        public class SearchJavaCompiler implements ActionListener {
            @Override
            /**
             * Open a file dialog button and get the Java compiler path.
             */
            public void actionPerformed(ActionEvent e) {
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File currentFile = chooser.getSelectedFile();
                    javaCompilerPath = currentFile.getPath();
                    JOptionPane.getRootFrame().dispose();
                    currentJavaCompiler.setText("     " + javaCompilerPath);
                }
            }    
        }
    } 

     /**
     * ActionListener class for Java Compile Options.
     */
    private class CompileOptionsListener implements ActionListener {
        @Override
        /**
         * Create a dialog that displays the current Java compiler arguments/options and components to update it.
         */
        public void actionPerformed(ActionEvent e) {
            JLabel currentCompilerOptions = new JLabel("Current java compiler options: " + javaCompilerOptions);
            JTextField newJavaCompilerOptions = new JTextField(javaCompilerOptions);  
            Object msg[] = {currentCompilerOptions, newJavaCompilerOptions};
            int option = JOptionPane.showConfirmDialog(null, msg, "Compile Options", JOptionPane.OK_CANCEL_OPTION);

            //Get the Java run time arguments/options
            if (option == JOptionPane.OK_OPTION) {
                javaCompilerOptions = newJavaCompilerOptions.getText();
                currentJavaCompilerOptionsSetting.setText("     " + javaCompilerOptions);
            }
        }
    } 

     /**
     * ActionListener class for Java Run-time.
     */
    private class JavaRunTimeListener implements ActionListener {
        @Override
        /**
         * Create a dialog that displays the current Java run time and components to update it.
         */
        public void actionPerformed(ActionEvent e) {
            JLabel currentCompiler = new JLabel("Current external java run time command: " + javaRunTimePath);
            JTextField newCompiler = new JTextField(javaRunTimePath);  
            JButton searchRunTime = new JButton("..");
            searchRunTime.addActionListener(new SearchJavaRunTime());

            //Create a panel for the Java run time dialog
            JPanel compilePanel = new JPanel();
            compilePanel.setLayout(new BorderLayout());

            //Add the label and text field to the center panel
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.add(currentCompiler);
            centerPanel.add(newCompiler);

            //Add the file dialog button to the south panel
            JPanel southPanel = new JPanel();
            southPanel.add(searchRunTime);

            compilePanel.add(centerPanel, BorderLayout.CENTER);
            compilePanel.add(southPanel, BorderLayout.SOUTH);
        
            //Open the dialogc for Java run time
            int option = JOptionPane.showConfirmDialog(null, compilePanel, "Java Compiler", JOptionPane.OK_CANCEL_OPTION);

            //Update the Java run time path
            if (option == JOptionPane.OK_OPTION) {
                javaRunTimePath = newCompiler.getText();
                currentJavaRunTime.setText("     " + javaRunTimePath);
            }
        }

        /**
         * ActionListener class for search Java run time button.
         */
        public class SearchJavaRunTime implements ActionListener {
            @Override
            /**
             * Open a file dialog button and get the Java run time path.
             */
            public void actionPerformed(ActionEvent e) {
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File currentFile = chooser.getSelectedFile();
                    javaRunTimePath = currentFile.getPath();
                    JOptionPane.getRootFrame().dispose();
                    currentJavaRunTime.setText("     " + javaRunTimePath);
                }
            }    
        }
    } 

     /**
     * ActionListener class for Java run time options.
     */
    private class RunTimeOptionsListener implements ActionListener {
        @Override
        /**
         * Create a dialog that displays the current Java run time arguments/options and components to update it.
         */
        public void actionPerformed(ActionEvent e) {
            JLabel currentRunTimeOptions = new JLabel();
            currentRunTimeOptions.setText("Current java compiler options: " + javaRunTimeOptions);

            //Create the dialog for Java run time arguments/options
            JTextField newJavaRunTimeOptions = new JTextField(javaRunTimeOptions);
            Object msg[] = {currentRunTimeOptions, newJavaRunTimeOptions};
            int option = JOptionPane.showConfirmDialog(null, msg, "Compile Options", JOptionPane.OK_CANCEL_OPTION);

            //Get the Java run time arguments/options
            if (option == JOptionPane.OK_OPTION) {
                javaRunTimeOptions = newJavaRunTimeOptions.getText();
                currentJavaRunTimeOptionsSetting.setText(javaRunTimeOptions);
            }
        }
    }
    /**
     * ActionListener class for IDE compile mode.
     */
    private class IDECompilerListener implements ActionListener {
        @Override
        /**
         * Set IDE flag on and Lex and Yacc off.
         */
        public void actionPerformed(ActionEvent e) {
            IDECompiler = 1;
            IDERadioButton.setSelected(true);

            lexYaccCompiler = 0;
            lexYaccRadioButton.setSelected(false);
        }
    }  

    /**
     * ActionListener class for Lex and Yacc compiler mode.
     */
    private class LexYaccCompilerListener implements ActionListener {
        @Override
        /**
         * Set Lex and Yacc flag on and IDE off.
         */
        public void actionPerformed(ActionEvent e) {
            lexYaccCompiler = 1;
            lexYaccRadioButton.setSelected(true);

            IDECompiler = 0;
            IDERadioButton.setSelected(false);
        }
    }  
}