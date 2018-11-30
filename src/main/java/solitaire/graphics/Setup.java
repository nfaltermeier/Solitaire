package solitaire.graphics;

import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.File;

public abstract class Setup {
    /**
     * Prevents the program from continuing until the user goes through the window this method opens
     *
     * @return The file representing the save file the game should be started from, or null to start from a new file
     */
    public static @Nullable File showSetupWindow() {
        JDialog dialog = new JDialog(null, "Solitaire Setup", Dialog.ModalityType.DOCUMENT_MODAL);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Only gets called if a file is selected or cancel is hit
        fileChooser.addActionListener((ActionEvent e) -> {
            if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                fileChooser.setSelectedFile(null);
            }

            dialog.dispose();
        });
        fileChooser.setVisible(false);

        JPanel questionBox = new JPanel();
        questionBox.setLayout(new BoxLayout(questionBox, BoxLayout.X_AXIS));

        questionBox.add(new JLabel("Would you like to continue a saved game?"));

        JButton yes = new JButton("Yes");
        yes.addActionListener((ActionEvent e) -> {
            fileChooser.setVisible(true);
            dialog.pack();
        });
        questionBox.add(yes);

        JButton no = new JButton("No");
        no.addActionListener((ActionEvent e) -> {
            // incase they hit yes, selected a file, and changed their mind
            fileChooser.setSelectedFile(null);
            dialog.dispose();
        });
        questionBox.add(no);

        content.add(questionBox);
        content.add(fileChooser);

        dialog.add(content);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);

        no.requestFocusInWindow();
        
        dialog.setVisible(true);

        return fileChooser.getSelectedFile();
    }
}
