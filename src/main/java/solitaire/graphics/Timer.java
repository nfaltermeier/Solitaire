package solitaire.graphics;

import javax.swing.JLabel;
import java.awt.event.ActionEvent;

public class Timer extends JLabel {
    private int seconds;
    private int minutes;

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

    private void updateText() {
        setText("Elapsed Time: " + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
        repaint();
    }
}
