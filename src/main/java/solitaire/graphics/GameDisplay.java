package solitaire.graphics;

import solitaire.game.Game;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameDisplay extends JPanel {
    // Shows all the stacks of cards, the background, and any UI components. The top level GUI component.
    private Game game;

    public GameDisplay(Game game) {
        this.game = game;

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

        toolbarContainer.add(new Timer());

        JPanel gameDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 102, 0));
                g.fillRect(0, 0, 1180, 720);

                game.draw(g, 0, 0);
            }
        };
        gameDisplay.setPreferredSize(new Dimension(1180, 720));
        gameDisplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked at (" + e.getX() + ", " + e.getY() + ")");
                game.onClick(e.getX(), e.getY(), gameDisplay);
            }
        });
        add(gameDisplay, BorderLayout.SOUTH);
    }
}
