/**
 * Draws the toolbar and playing area of the window, loads saved games, and adds mouse listener.
 */
package solitaire.graphics;

import com.google.gson.Gson;
import solitaire.game.Game;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GameDisplay extends JPanel {
    /**
     * Initializer method draws the game (and cards) based from the Game object passed in. Additionally, manages the fileIO for resuming a saved game and adds the mouse listener.
     * @param game
     */
    public GameDisplay(Game game) {
        // Shows all the stacks of cards, the background, and any UI components. The top level GUI component.
        setLayout(new BorderLayout());

        JPanel toolbarContainer = new JPanel();
        add(toolbarContainer, BorderLayout.WEST);

        JButton newGame = new JButton("New Game");
        newGame.addActionListener((ActionEvent e) -> {
            int response = JOptionPane.showConfirmDialog(null,
                    "Are you sure you would like to start a new game?", "Are you sure?",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                game.startNewGame();
            }
        });
        toolbarContainer.add(newGame);

        JButton saveGameButton = new JButton("Save Game");
        saveGameButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            if (game.loadedFrom != null)
                fileChooser.setSelectedFile(game.loadedFrom);
            else
                fileChooser.setSelectedFile(new File("MySolitaireGame.sav"));

            int response = fileChooser.showSaveDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                if (fileChooser.getSelectedFile().exists()) {
                    int overwriteResponse = JOptionPane.showConfirmDialog(null,
                            "The selected file already exists, would you like to overwrite it?",
                            "Overwrite file?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (overwriteResponse != JOptionPane.YES_OPTION)
                        return;
                }
                Gson gson = new Gson();
                String gameString = gson.toJson(game);
                String gameHash = "";
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    gameHash = new String(messageDigest.digest(gameString.getBytes(StandardCharsets.UTF_8)));
                } catch (NoSuchAlgorithmException e1) {
                    System.err.println("SHA-256 MessageDigest is not supported :(");
                }

                try {
                    PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile());
                    writer.println(gameString);
                    writer.print(gameHash);
                    writer.close();

                    if (writer.checkError())
                        throw new IOException("The save PrintWriter ate some IO error :(");
                } catch (IOException error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "An error occurred while saving the game. Please try again.",
                            "An error occurred", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        toolbarContainer.add(saveGameButton);

        toolbarContainer.add(new Timer(game));

        JLabel moveCounterLabel = new JLabel("    Moves: " + game.moves);
        toolbarContainer.add(moveCounterLabel);

        JPanel gameDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 102, 0));
                g.fillRect(0, 0, 1200, 800);

                game.draw(g, 0, 0);
            }
        };
        gameDisplay.setPreferredSize(new Dimension(1200, 800));
        gameDisplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                game.onClick(x, y);

                moveCounterLabel.setText("    Moves: " + game.moves);
                repaint();

                // This is used instead of the mouseClicked method due to mouseClicked not being
                // triggered if the mouse is moved between a press and release.
            }
        });
        add(gameDisplay, BorderLayout.SOUTH);
    }
}
