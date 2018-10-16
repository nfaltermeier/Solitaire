package solitare.graphics;

import solitare.game.Game;

import javax.swing.*;

public class GameDisplay extends JPanel {
    // Shows all the stacks of cards, the background, and any UI components. The top level GUI component.

    private Game game;

    public GameDisplay(Game game) {
        this.game = game;
    }
}
