package solitaire.graphics;

import solitaire.game.Game;

import javax.swing.JLabel;
import java.awt.event.ActionEvent;

public class Timer extends JLabel {

    public Timer(Game game) {
        javax.swing.Timer timer = new javax.swing.Timer(1000, (ActionEvent e) -> {
            if (game.seconds == 59) {
                game.seconds = 0;
                game.minutes++;
            } else {
                game.seconds++;
            }

            updateText(game);
        });
        timer.setRepeats(true);
        timer.start();

        updateText(game);
    }

    private void updateText(Game game) {
        setText("Elapsed Time: " + game.minutes + ":" + (game.seconds < 10 ? "0" + game.seconds : game.seconds));
        repaint();
    }
}
