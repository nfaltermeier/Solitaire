/**
 * The timer class provides a timer so the user knows how long they'be been playing.
 */
package solitaire.graphics;

import solitaire.game.Game;

import javax.swing.JLabel;
import java.awt.event.ActionEvent;

public class Timer extends JLabel {

    /**
     * Initializer method that makes a Timer object and adds to minutes after 60 seconds and adds to seconds every seconds while seconds is less than 60. Then calls update label.
     * @param game The game that this timer is counting for
     */
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

    /**
     * Updates the timer text label each second to the latest time.
     */
    private void updateText(Game game) {
        setText("Elapsed Time: " + game.minutes + ":" + (game.seconds < 10 ? "0" + game.seconds : game.seconds));
        repaint();
    }
}
