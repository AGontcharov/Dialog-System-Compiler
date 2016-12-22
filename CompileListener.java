import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

/**
 * ActionListener class for Compile.
 * @author Alexander Gontcharov
 */
public class CompileListener implements ActionListener {
    private static final String DEFAULT_GUI_TITLE = "title";                        //Define parameter name for title 
    private static final String DEFAULT_GUI_FIELDS = "fields";                      //Define parameter name for fields
    private static final String DEFAULT_GUI_BUTTONS = "buttons";                    //Define parameter name for buttons
    private static final char DEFAULT_GUI_COMMENT_CHARACTER = '#';                  //Define comment character
    public static final int STRING_TYPE = 3;                                        //Define string types
    public static final int LIST_TYPE = 4;                                          //Define list types

    /**
     * Parses the file the first time. 
     * @return - returns true if parsed else false
     */
    public boolean firstParse() {
        WrapperParameterManager parameterManager = new WrapperParameterManager();
        DialogcMenuBar.guiButtons = new ArrayList<String>();
        DialogcMenuBar.guiFields = new ArrayList<String>();

        //Create the Parameter Manager
        if (!parameterManager.create(3)) {
            return false;
        }

        // Manage the title of the GUI to be compiled
        if (!parameterManager.manage(DEFAULT_GUI_TITLE, STRING_TYPE, 1)) {
            return false;
        }

        //Manage the fields of the GUI to be compiled
        if (!parameterManager.manage(DEFAULT_GUI_FIELDS, LIST_TYPE, 1)) {
            return false;
        }
        
        //Manage the buttons of the GUI to be compiled
        if (!parameterManager.manage(DEFAULT_GUI_BUTTONS, LIST_TYPE, 1)) {
            return false;
        }

        //Parse the file
        if (!parameterManager.parseFrom(DialogcMenuBar.absoluteFilePath, DEFAULT_GUI_COMMENT_CHARACTER)) {
            return false;
        }
        
        //Check if title has a value
        if (parameterManager.hasValue(DEFAULT_GUI_TITLE)) {
            //Get title value
            DialogcMenuBar.guiTitle = parameterManager.getString(DEFAULT_GUI_TITLE);
            if (DialogcMenuBar.guiTitle.length() == 0) {
                System.err.println("Title is empty");
                return false;
            }
        }

        //Check if fields have values
        if (parameterManager.hasValue(DEFAULT_GUI_FIELDS)) {
            String list = "";
            while (list != null) {
                //Store the field values into an ArrayList
                list = parameterManager.getList(DEFAULT_GUI_FIELDS);
                if (list != null) {
                    DialogcMenuBar.guiFields.add(list);
                }
            }
        }

        //Check if buttons have values
        if (parameterManager.hasValue(DEFAULT_GUI_BUTTONS)) {
            String list = "";
            while (list != null) {
                //Store the button values into an ArrayList
                list = parameterManager.getList(DEFAULT_GUI_BUTTONS);
                if (list != null) {
                    DialogcMenuBar.guiButtons.add(list);
                }
            }
        }

        //Destroy the ParameterManager
        if (!parameterManager.destroy()) {
            return false;
        }

        //Success
        return true;
    }

    /**
     * Parses the file the second time. 
     * @return - returns true if parsed else false
     */
    public boolean secondParse() {
        WrapperParameterManager parameterManager = new WrapperParameterManager();
        DialogcMenuBar.guiFieldTypes = new ArrayList<String>();
        DialogcMenuBar.guiActionListeners = new ArrayList<String>();

        //Create the ParameterManager
        if (!parameterManager.create(10)) {
            return false;
        }

        //Manage the values of the fields of the GUI to be compiled
        for (String s: DialogcMenuBar.guiFields) {
            if (!parameterManager.manage(s, STRING_TYPE, 1)) {
                return false;
            }
        }

        //Manage the values of the buttons of the GUI to be compiled
        for (String s: DialogcMenuBar.guiButtons) {
            if (!parameterManager.manage(s, STRING_TYPE, 1)) {
                return false;
            }
        }

        //Parse the file
        if (!parameterManager.parseFrom(DialogcMenuBar.absoluteFilePath, DEFAULT_GUI_COMMENT_CHARACTER)) {
            return false;
        }

        //Get the field type values of the fields of the GUI to be compiled
        for (String s: DialogcMenuBar.guiFields) {
            if (parameterManager.hasValue(s)) {
                //Store them into an ArrayList
                String list = parameterManager.getString(s);
                DialogcMenuBar.guiFieldTypes.add(list);
            }
        }

        //Get the action listeners of the buttons of the GUI to be compiled
        for (String s: DialogcMenuBar.guiButtons) {
            if (parameterManager.hasValue(s)) {
                //Store them into an ArrayList
                String list = parameterManager.getString(s);
                DialogcMenuBar.guiActionListeners.add(list);
            }
        }

        //Destroy the Parameter Manager
        if (!parameterManager.destroy()) {
            return false;
        }

        //Success
        return true;
    }

    /**
     * Compiles the config file. 
     * @return - returns true if compiled else false
     */
    public boolean compile() {
        Process p;
        File directory;
        int exitValue = 0;

        //New file and not modified
        if (DialogcMenuBar.newFile == 1 && DialogcMenuBar.fileModified == 0) {
            return false;
        }

        //File modified
        if (DialogcMenuBar.fileModified == 1) {
            int option = JOptionPane.showConfirmDialog(null, new JLabel("The current file has been modified. Would you like to save it?"), "File Modified", JOptionPane.YES_NO_OPTION);
            //Save
            if (option == JOptionPane.YES_OPTION) {
                //Existing file
                if (DialogcMenuBar.newFile == 0) {
                    DialogcMenuBar.saveFile();
                }
                //New file
                else {
                    new DialogcMenuBar().saveAsFile();
                } 
            }
            //Dont save
            else {
                if (DialogcMenuBar.newFile == 1) {
                    return false;
                }
            }
        }

        //Create directory
        try {
            directory = new File(DialogcMenuBar.currentWorkingDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName));
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        if (DialogcMenuBar.IDECompiler == 1) {
            //Do the first parse
            if (!firstParse()) {
                return false;
            }

            //Do the second parse
            if (!secondParse()) {
                return false;
            }

            //Generate the GUI file    
            if (!new CompileGUICode().writeGUI()) {
                return false;
            }

            //Generate the interface
            if (!new CompileGUICode().writeInterface()) {
                return false;
            }

            //Generate the exception
            if (!new CompileGUICode().writeException()) {
                return false;
            }
        }

        if (DialogcMenuBar.lexYaccCompiler == 1) {
            //Create class files
            try {
                String command = DialogcMenuBar.yadcPath + " "
                + DialogcMenuBar.absoluteFilePath + " " 
                + MainWindow.removeExtension(MainWindow.fileName) + " " + directory.getPath();
                System.out.println("\nRunning: " + command + "\n");
                p = Runtime.getRuntime().exec(command);
                exitValue = p.waitFor();
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
                return false;
            }

            //Print the error stream from the processor to the screen
            BufferedReader errStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = "";
            try {
                while ((line = errStream.readLine()) != null) {
                    System.err.println(line);
                }
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
                return false;
            }

            if (p.exitValue() != 0) {
                return false;
            }
        }

        //sucess
        return true;
    }

    @Override
    /**
     * Invokes the first and second parse methods. If the file is new or modified a dialog is prompt with a message.
     */
    public void actionPerformed(ActionEvent e) {
        //Compile the config files
        boolean result = compile();
        if (!result && DialogcMenuBar.newFile == 1) {
            JOptionPane.showMessageDialog(null, "No file has been created or opened.", "No File", JOptionPane.INFORMATION_MESSAGE);
        }
        else if (!result) {
            JOptionPane.showMessageDialog(null, "An unexpected error occured during compilation.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "File succesfully compiled.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}