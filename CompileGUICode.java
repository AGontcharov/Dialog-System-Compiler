import java.io.FileWriter;

/**
 * A class which writes the Gui and Interface java files for the config files.
 * @author Alexander Gontcharov
 */
public class CompileGUICode {
    private FileWriter fw;                                                                      //Declare the main file writer
    private FileWriter aw;                                                                      //Declare another file writer for action listeners
    private static final String DEFAULT_LABEL_NAME = "FieldLabel";                              //Define part of the label name
    private static final String DEFAULT_TEXT_FIELD_NAME = "TextField";                          //Define part of the text field name
    private static final String DEFAULT_BUTTON__NAME = "Button";                                //Define part of the button name
    private static final String DEFAULT_INTERFACE_NAME = "FieldEdit";                           //Define part of the interface name
    private static final String DEFAULT_EXCEPTION_CLASS_NAME = "IllegalFieldValueException";    //Define name of the IllegalFieldValueException class
    private static final String DEFAULT_ADD_ACTIONLISTENER = "AddActionListener";               //Define name of the Add ActionListener class
    private static final String DEFAULT_QUERY_ACTIONLISTENER = "QueryActionListener";           //Define name of the Query ActionListener class
    private static final String DEFAULT_DELETE_ACTIONLISTENER = "DeleteActionListener";         //Define name of the Delete ActionListener class
    private static final String DEFAULT_UPDATE_ACTIONLISTENER = "UpdateActionListener";         //Define name of the Update ActionListener class

    /**
     * Creates the Java interface file for the config file.
     * @return - True if generated false otherwise.
     */
    public boolean writeInterface() {
        try {
            //Create and write the GUI Java interface file in the specified directory
            fw = new FileWriter(DialogcMenuBar.currentFileDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + ".java");

            //Write declaration for GUI interface class
            fw.write("public interface " + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " {\n");
            fw.write("\n");

            //Write get methods for fields
            fw.write("\t//Get methods for text fields\n");
            for (String s: DialogcMenuBar.guiFields) {
                fw.write("\tpublic String getDC" + s + "();\n");
            }
            fw.write("\n");

            //Write get methods for fields
            fw.write("\t//Set methods for text fields\n");
            for (String s: DialogcMenuBar.guiFields) {
                fw.write("\tpublic void setDC" + s + "(String message);\n");
            }
            fw.write("\n");

            //Write status method
            fw.write("\t//Method to append messages to status area\n");
            fw.write("\tpublic void appendToStatusArea(String message);\n");
            fw.write("}\n");
        }
        //Catch IOException
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        //Close the file writer
        finally {
            if (fw != null)
                try {
                    fw.close();
                }
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    return false;
                }
        }

        //Sucess
        return true;
    }

    /**
     * Creates the Java IllegalFieldValueException file for the config file.
     * @return - True if generated false otherwise.
     */
    public boolean writeException() {
        try {
            //Create and write the IllegalFieldValueException Java file in the specified directory
            fw = new FileWriter(DialogcMenuBar.currentFileDirectory + "/" + DEFAULT_EXCEPTION_CLASS_NAME + ".java");

            //Write declaration of IllegalFieldValueException class
            fw.write("/**\n");
            fw.write("* IllegalFieldValueException class for Dialogc.\n");
            fw.write("* @author Alexander Gontcharov\n");
            fw.write("*/\n");
            fw.write("public class IllegalFieldValueException extends RuntimeException {\n\n");

            //Write constructor for class
            fw.write("\t/**\n");
            fw.write("\t *\n");
            fw.write("\t */\n");
            fw.write("\tpublic IllegalFieldValueException(String message) {\n");
            fw.write("\t\tsuper(message);\n");
            fw.write("\t}\n");
            fw.write("}");

        }
        //Catch IOException
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        //Close the file writer
        finally {
            if (fw != null)
                try {
                    fw.close();
                }
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    return false;
                }
        }

        //Sucess
        return true;
    }

    /**
     * Creates the Java interface file for the config file.
     * @return - True if generated false otherwise.
     */
    public boolean writeGUI() {
        try {
            int i = 0;
            fw = new FileWriter(DialogcMenuBar.currentFileDirectory + "/" + MainWindow.removeExtension(MainWindow.fileName) + ".java");

            //Write Java class imports
            fw.write("import javax.swing.JFrame;\n");
            fw.write("import javax.swing.JPanel;\n");
            fw.write("import javax.swing.JLabel;\n");
            fw.write("import javax.swing.JScrollPane;\n");
            fw.write("import javax.swing.JTextArea;\n");
            fw.write("import javax.swing.JTextField;\n");
            fw.write("import javax.swing.border.BevelBorder;\n");
            fw.write("import javax.swing.JButton;\n");
            fw.write("import javax.swing.BorderFactory;\n");
            fw.write("import java.awt.BorderLayout;\n");
            fw.write("import java.awt.FlowLayout;\n");
            fw.write("import java.awt.GridLayout;\n\n");

            //Write declaration of class
            fw.write("public class " + MainWindow.removeExtension(MainWindow.fileName) + " extends JFrame implements "
                + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " {\n");

            //Write declaration of final primitive types
            fw.write("\tpublic static final int WIDTH = 600;\n");
            fw.write("\tpublic static final int HEIGHT = 400;\n");
            fw.write("\tpublic static final int NUMBER_OF_CHAR = 20;\n");
            fw.write("\tpublic static final int NUMBER_OF_LINES = 10;\n\n");

            //Write declaration of fields
            for (String s : DialogcMenuBar.guiFields) {
                fw.write("\tprivate JTextField " + s + DEFAULT_TEXT_FIELD_NAME + ";\n");
            }

            //Write declaration of buttons
            for (String s : DialogcMenuBar.guiButtons) {
                fw.write("\tprivate JButton " + s + DEFAULT_BUTTON__NAME + ";\n");
            }
            fw.write("\tprivate JTextArea statusMessage;\n");
            fw.write("\tprivate JPanel centerPanel;\n");
            fw.write("\tprivate JPanel southPanel;\n\n");

            //Write constructor for class
            fw.write("\tpublic " + MainWindow.removeExtension(MainWindow.fileName) + "() {\n");
            fw.write("\t\tsuper();\n");
            fw.write("\t\tsetSize(WIDTH, HEIGHT);\n");
            fw.write("\t\tsetTitle(" + "\"" +DialogcMenuBar.guiTitle + "\"" + ");\n");
            fw.write("\t\tsetDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);\n");
            fw.write("\t\tsetLayout(new BorderLayout());\n");
            fw.write("\t\tsetLocationRelativeTo(null);\n\n");

            //Write invocation of methods to create the GUI
            fw.write("\t\t//Create the compiled window\n");
            fw.write("\t\tcreateCenterPanel();\n");
            fw.write("\t\tadd(centerPanel, BorderLayout.CENTER);\n");
            fw.write("\t\tcreateSouthPanel();\n");
            fw.write("\t\tadd(southPanel, BorderLayout.SOUTH);\n");
            fw.write("\t}\n\n");

            i = 0;
            //Write get methods for fields
            for (String s: DialogcMenuBar.guiFields) {
                fw.write("\t//Get method for " + s + "\n");
                fw.write("\tpublic String getDC" + s + "() {\n");

                //integer field type
                if (DialogcMenuBar.guiFieldTypes.get(i).equals("integer")) {
                    fw.write("\t\tif (!" + s + "TextField.getText().matches(\"^-?[0-9]+$\")) {\n");
                    fw.write("\t\t\tthrow new IllegalFieldValueException(" + "\"" + s + " = incorrect type\");\n");
                    fw.write("\t\t}\n\n");
                }
                //Float field type
                else if (DialogcMenuBar.guiFieldTypes.get(i).equals("float")) {
                    fw.write("\t\tif (!" + s + "TextField.getText().matches(\"^-?[0-9]+.?[0-9]*$\")) {\n");
                    fw.write("\t\t\tthrow new IllegalFieldValueException(" + "\"" + s + " = incorrect type\");\n");
                    fw.write("\t\t}\n\n");
                }
                //String field type
                else if (DialogcMenuBar.guiFieldTypes.get(i).equals("string")) {
                    /*Exception here?.*/
                }
                //Unknown field type;
                else {
                    System.err.println(s + " is not an integer, float or string.");
                    return false;
                }

                //Return the string from field
                fw.write("\t\treturn " + s + DEFAULT_TEXT_FIELD_NAME + ".getText();\n");
                fw.write("\t}\n\n");
                i++;
            }

            //Write set methods for fields
            for (String s: DialogcMenuBar.guiFields) {
                fw.write("\t//Set method for " + s + "\n");
                fw.write("\tpublic void setDC" + s + "(String message) {\n");
                fw.write("\t\t" + s + DEFAULT_TEXT_FIELD_NAME + ".setText(message);\n");
                fw.write("\t}\n\n");
            }

            //Write append to status area method
            fw.write("\tpublic void appendToStatusArea(String message) {\n");
            fw.write("\t\tstatusMessage.append(message + \"\\n\");\n");
            fw.write("\t}\n\n");

            //Write create Center Panel method
            fw.write("\t//Create and the fields to the Center in the Center Panel\n");
            fw.write("\tprivate void createCenterPanel() {\n");
            fw.write("\t\tcenterPanel = new JPanel();\n");
            fw.write("\t\tcenterPanel.setLayout(new BorderLayout());\n\n");

            //Write field panel
            fw.write("\t\tJPanel fieldPanel = new JPanel();\n");
            fw.write("\t\tfieldPanel.setLayout(new GridLayout(" + DialogcMenuBar.guiFields.size() + ", 2));\n");
            fw.write("\t\tfieldPanel.setBorder(BorderFactory.createEmptyBorder(20,20,0,20));\n\n");

            //Write declaration of all the field labels and text fields and add them
            for (String s: DialogcMenuBar.guiFields) {
                fw.write("\t\t" + s + DEFAULT_TEXT_FIELD_NAME +" = new JTextField();\n");
                fw.write("\t\tfieldPanel.add(new JLabel(" + "\"" + s + "\"" + "));\n");
                fw.write("\t\tfieldPanel.add(" + s + DEFAULT_TEXT_FIELD_NAME + ");\n\n");
            }
            fw.write("\t\tcenterPanel.add(fieldPanel, BorderLayout.CENTER);\n\n");

            //Write button panel
            fw.write("\t\t//Create and add the buttons to the SOUTH in the Center Panel\n");
            fw.write("\t\tJPanel buttonPanel = new JPanel();\n");
            fw.write("\t\tbuttonPanel.setLayout(new FlowLayout());\n\n");
            
            i = 0;
            //Declare all the button names and attach action listener to them
            for (String s: DialogcMenuBar.guiButtons) {
                fw.write("\t\t" + s + DEFAULT_BUTTON__NAME + " = new JButton(" + "\"" + s + "\"" + ");\n");
                
                //Write and attach the pre-defined ADD Action Listener
                if (s.equals("ADD")) {
                    if (!writeAddActionListener()) {
                        return false;
                    }
                    fw.write("\t\t" + s + DEFAULT_BUTTON__NAME + ".addActionListener(new AddActionListener(this));\n");
                    fw.write("\t\tbuttonPanel.add(" + s + DEFAULT_BUTTON__NAME + ");\n\n");
                    i++;
                }
                //Write and attach the pre-defined DELETE Action Listener
                else if (s.equals("DELETE")) {
                    if (!writeDeleteActionListener()) {
                        return false;
                    }
                    fw.write("\t\t" + s + DEFAULT_BUTTON__NAME + ".addActionListener(new DeleteActionListener(this));\n");
                    fw.write("\t\tbuttonPanel.add(" + s + DEFAULT_BUTTON__NAME + ");\n\n");
                    i++;
                }
                //Write and attach the pre-defined ADD Action Listener
                else if (s.equals("QUERY")) {
                    if (!writeQueryActionListener()) {
                        return false;
                    }
                    fw.write("\t\t" + s + DEFAULT_BUTTON__NAME + ".addActionListener(new QueryActionListener(this));\n");
                    fw.write("\t\tbuttonPanel.add(" + s + DEFAULT_BUTTON__NAME + ");\n\n");
                    i++;
                }
                //Write and attach the pre-defined UPDATE Action Listener
                else if (s.equals("UPDATE")) {
                    if (!writeUpdateActionListener()) {
                        return false;
                    }
                    fw.write("\t\t" + s + DEFAULT_BUTTON__NAME + ".addActionListener(new UpdateActionListener(this));\n");
                    fw.write("\t\tbuttonPanel.add(" + s + DEFAULT_BUTTON__NAME + ");\n\n");
                    i++;
                }
                //Attach the user's action listener.
                else {
                    fw.write("\t\t" + s + DEFAULT_BUTTON__NAME + ".addActionListener(new " + DialogcMenuBar.guiActionListeners.get(i) + "(this));\n");
                    fw.write("\t\tbuttonPanel.add(" + s + DEFAULT_BUTTON__NAME + ");\n\n");
                    i++;
                }
            }
            fw.write("\t\tcenterPanel.add(buttonPanel, BorderLayout.SOUTH);\n");
            fw.write("\t}\n\n");

            //Write South panel
            fw.write("\tprivate void createSouthPanel() {\n");
            fw.write("\t\tsouthPanel = new JPanel();\n");
            fw.write("\t\tsouthPanel.setLayout(new BorderLayout());\n\n");

            //Write status panel
            fw.write("\t\t//Create and add editor bar to north in South panel\n");
            fw.write("\t\tJPanel statusPanel = new JPanel();\n");
            fw.write("\t\tstatusPanel.setBorder(new BevelBorder (BevelBorder.LOWERED));\n");
            fw.write("\t\tJLabel editor = new JLabel(\"Status\");\n");
            fw.write("\t\tstatusPanel.add(editor);\n");
            fw.write("\t\tsouthPanel.add(statusPanel, BorderLayout.NORTH);\n\n");

            //Write status area and add it
            fw.write("\t\t//Create and add text area to Center in South panel\n");
            fw.write("\t\tstatusMessage = new JTextArea(NUMBER_OF_LINES, NUMBER_OF_CHAR);\n");
            fw.write("\t\tstatusMessage.setEditable(true);\n");
            fw.write("\t\tJScrollPane scrolledText = new JScrollPane(statusMessage);\n");
            fw.write("\t\tscrolledText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);\n");
            fw.write("\t\tscrolledText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);\n");
            fw.write("\t\tsouthPanel.add(scrolledText, BorderLayout.CENTER);\n");
            fw.write("\t}\n\n");

            //Write main
            fw.write("\tpublic static void main(String[] args) {\n");
            fw.write("\t\t" + MainWindow.removeExtension(MainWindow.fileName) + " compiledGUI = new " + MainWindow.removeExtension(MainWindow.fileName) + "();\n");
            fw.write("\t\tcompiledGUI.setVisible(true);\n");
            fw.write("\t}\n");
            fw.write("}");
        }
        //Catch IOException
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        //Close the file writer
        finally {
            if (fw != null)
                try {
                    fw.close();
                }
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    return false;
                }
        }   

        //Success
        return true;
    }

    /**
     * Creates the ADD ActionListener file for the config file.
     * @return - True if generated false otherwise.
     */
    public boolean writeAddActionListener() {
         try {
            //Create and write the ADD ActionListener file in the specified directory
            aw = new FileWriter(DialogcMenuBar.currentFileDirectory + "/" + DEFAULT_ADD_ACTIONLISTENER + ".java");
            aw.write("import java.awt.event.ActionListener;\n");
            aw.write("import java.awt.event.ActionEvent;\n\n");

            //Write declaration of the ADD ActionListener class
            aw.write("public class " + DEFAULT_ADD_ACTIONLISTENER + " implements ActionListener" + " {\n");
            aw.write("\t" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d;\n\n");

            //Write constructor for class
            aw.write("\tpublic " + DEFAULT_ADD_ACTIONLISTENER + "(" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d) {\n");
            aw.write("\t\t this.d = d;\n");
            aw.write("\t}\n\n");

            //Write invocation of append to status area
            aw.write("\tpublic void actionPerformed(ActionEvent e) {\n");
            aw.write("\t\ttry {\n");
            aw.write("\t\t\td.appendToStatusArea(\"button pressed.\");\n");
            aw.write("\t\t}\n");

            //Write catch of append to status area
            aw.write("\t\tcatch(Exception tr) {\n");
            aw.write("\t\t\td.appendToStatusArea(\"ERROR\");\n");
            aw.write("\t\t}\n");

            aw.write("\t}\n");
            aw.write("}");
        }
        //Catch IOException
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        //Close the file writer
        finally {
            if (aw != null)
                try {
                    aw.close();
                }
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    return false;
                }
        }

        //Success
        return true;
    }

    /**
     * Creates the DELETE ActionListener file for the config file.
     * @return - True if generated false otherwise.
     */
    public boolean writeDeleteActionListener() {
        
        try {
            //Create and write the DELETE ActionListener file in the specified directory
            aw = new FileWriter(DialogcMenuBar.currentFileDirectory + "/" + DEFAULT_DELETE_ACTIONLISTENER + ".java");
            aw.write("import java.awt.event.ActionListener;\n");
            aw.write("import java.awt.event.ActionEvent;\n\n");

            //Write declaration of the DELETE ActionListener class
            aw.write("public class " + DEFAULT_DELETE_ACTIONLISTENER + " implements ActionListener" + " {\n");
            aw.write("\t" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d;\n\n");

            //Write constructor for class
            aw.write("\tpublic " + DEFAULT_DELETE_ACTIONLISTENER + "(" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d) {\n");
            aw.write("\t\t this.d = d;\n");
            aw.write("\t}\n\n");

            //Write invocation of append to status area
            aw.write("\tpublic void actionPerformed(ActionEvent e) {\n");
            aw.write("\t\ttry {\n");
            aw.write("\t\t\td.appendToStatusArea(\"button pressed.\");\n");
            aw.write("\t\t}\n");

            //Write catch of append to status area
            aw.write("\t\tcatch(Exception tr) {\n");
            aw.write("\t\t\td.appendToStatusArea(\"ERROR\");\n");
            aw.write("\t\t}\n");

            aw.write("\t}\n");
            aw.write("}");
        }
        //Catch IOException
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        //Close the file writer
        finally {
            if (aw != null)
                try {
                    aw.close();
                }
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    return false;
                }
        }

        //Success
        return true;
    }

    /**
     * Creates the QUERY ActionListener file for the config file.
     * @return - True if generated false otherwise.
     */
    public boolean writeQueryActionListener() {

        try {
            //Create and write the QUERY ActionListener file in the specified directory
            aw = new FileWriter(DialogcMenuBar.currentFileDirectory + "/" + DEFAULT_QUERY_ACTIONLISTENER + ".java");
            aw.write("import java.awt.event.ActionListener;\n");
            aw.write("import java.awt.event.ActionEvent;\n\n");

            //Write declaration of the QUERY ActionListener class
            aw.write("public class " + DEFAULT_QUERY_ACTIONLISTENER + " implements ActionListener" + " {\n");
            aw.write("\t" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d;\n\n");

            //Write constructor for class
            aw.write("\tpublic " + DEFAULT_QUERY_ACTIONLISTENER + "(" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d) {\n");
            aw.write("\t\t this.d = d;\n");
            aw.write("\t}\n\n");

            //Write invocation of append to status area
            aw.write("\tpublic void actionPerformed(ActionEvent e) {\n");
            aw.write("\t\ttry {\n");
            aw.write("\t\t\td.appendToStatusArea(\"button pressed.\");\n");
            aw.write("\t\t}\n");

            //Write catch of append to status area
            aw.write("\t\tcatch(Exception tr) {\n");
            aw.write("\t\t\td.appendToStatusArea(\"ERROR\");\n");
            aw.write("\t\t}\n");

            aw.write("\t}\n");
            aw.write("}");
        }
        //Catch IOException
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        //Close the file writer
        finally {
            if (aw != null)
                try {
                    aw.close();
                }
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    return false;
                }
        }
        
        //Success
        return true;
    }

    /**
     * Creates the UPDATE ActionListener file for the config file.
     * @return - True if generated false otherwise.
     */
    public boolean writeUpdateActionListener() {
        
        try {
            //Create and write the UPDATE ActionListener file in the specified directory
            aw = new FileWriter(DialogcMenuBar.currentFileDirectory + "/" + DEFAULT_UPDATE_ACTIONLISTENER + ".java");
            aw.write("import java.awt.event.ActionListener;\n");
            aw.write("import java.awt.event.ActionEvent;\n\n");

            //Write declaration of the UPDATE ActionListener class
            aw.write("public class " + DEFAULT_UPDATE_ACTIONLISTENER + " implements ActionListener" + " {\n");
            aw.write("\t" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d;\n\n");

            //Write constructor for class
            aw.write("\tpublic " + DEFAULT_UPDATE_ACTIONLISTENER + "(" + MainWindow.removeExtension(MainWindow.fileName) + DEFAULT_INTERFACE_NAME + " d) {\n");
            aw.write("\t\t this.d = d;\n");
            aw.write("\t}\n\n");

            //Write invocation of append to status area
            aw.write("\tpublic void actionPerformed(ActionEvent e) {\n");
            aw.write("\t\ttry {\n");
            aw.write("\t\t\td.appendToStatusArea(\"button pressed.\");\n");
            aw.write("\t\t}\n");

            //Write catch of append to status area
            aw.write("\t\tcatch(Exception tr) {\n");
            aw.write("\t\t\td.appendToStatusArea(\"ERROR\");\n");
            aw.write("\t\t}\n");

            aw.write("\t}\n");
            aw.write("}");
        }
        //Catch IOException
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        //Close the file writer
        finally {
            if (aw != null)
                try {
                    aw.close();
                }
                //Catch IOException
                catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    return false;
                }
        }

        //Success
        return true;
    }
}