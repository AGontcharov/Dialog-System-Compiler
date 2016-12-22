import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A Class that creates a confirm window on exit.
 * @author Alexander Gontcharov
 * @see javax.swing.JFrame
 */
public class ConfirmWindow extends JFrame {
    private static final int WIDTH = 250;    //Width of the window
    private static final int HEIGHT = 100;   //Height of the window
    
        /**
         * Create a confirm window.
         */
        public ConfirmWindow() {
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());
            setLocationRelativeTo (null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            //Create and add confirm label to Center
            JLabel confirmLabel = new JLabel("Are you sure you want to exit?");
            confirmLabel.setHorizontalAlignment(JLabel.CENTER);
            add(confirmLabel, BorderLayout.CENTER);
            
            //Button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            
            //Exit button
            JButton exitButton = new JButton("Yes");
            exitButton.addActionListener(new ExitListener());
            buttonPanel.add(exitButton);
            
            //Cancel button
            JButton cancelButton = new JButton("No");
            cancelButton.addActionListener(new CancelListener());
            buttonPanel.add(cancelButton);
            
            //Add button panel to South
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        /**
         * ActionListener class for the exit button.
         */
        private class ExitListener implements ActionListener {
            @Override
            /**
             * Close the application and all its windows.
             */
            public void actionPerformed(ActionEvent e) {
            System.exit(0);
            }
        }
        
        /**
         * ActionListener class for the cancel button.
         */
        private class CancelListener implements ActionListener {
            @Override
            /**
             * Close the confirm window.
             */
            public void actionPerformed(ActionEvent e) {
            dispose();
            }
        }
}
