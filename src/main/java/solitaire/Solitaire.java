package solitaire;

import solitaire.game.Game;
import solitaire.graphics.GameDisplay;
import solitaire.graphics.ImageLoader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.GraphicsConfiguration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Solitaire {
    public static void main(String[] args) {
        /*
            TODO:
            1. Asynchronously load images
            2. ✓ Set look and feel ✓
            3. Open menu to start new game / load game (later?)
            4. ✓ Open main game GUI (Until saving/loading is implemented) ✓
         */

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.out.println("An error occurred while setting the GUI display style. Program will continue as normal.");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> imageLoading = executor.submit(() -> {
            String resourceFolderPath = "images";
            ImageLoader.init(resourceFolderPath);
        });

        try {
            while (!imageLoading.isDone()) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startGUI();
    }

    public static void startGUI() {
        startGUI(new Game());
    }

    public static void startGUI(Game game) {
        JFrame frame = new JFrame((GraphicsConfiguration) null);
        JPanel panel = new GameDisplay(game);
        frame.add(panel);
        frame.setTitle("Solitaire");
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
