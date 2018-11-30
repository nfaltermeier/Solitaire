/**
 * The timer class provides a timer so the user knows how long they'be been playing.
 */
package solitaire.graphics;

import javax.swing.JLabel;
import java.awt.event.ActionEvent;

public class Timer extends JLabel {
    private int seconds;
    private int minutes;

    /**
     * Initializer method that makes a Timer object and adds to minutes after 60 seconds and adds to seconds every seconds while seconds is less than 60. Then calls update label.
     */
    public Timer() {
        seconds = 0;
        minutes = 0;

        javax.swing.Timer timer = new javax.swing.Timer(1000, (ActionEvent e) -> {
            if (seconds == 59) {
                seconds = 0;
                minutes++;
            } else {
                seconds++;
            }

            updateText();
        });
        timer.setRepeats(true);
        timer.start();

        updateText();
    }

    /**
     * Updates the timer text label each second to the latest time.
     */
    private void updateText() {
        setText("Elapsed Time: " + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
        repaint();
    }
}
