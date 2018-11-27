package solitaire;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import solitaire.game.Game;
import solitaire.graphics.GameDisplay;
import solitaire.graphics.ImageLoader;
import solitaire.graphics.Setup;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.GraphicsConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Solitaire {
    public static void main(String[] args) {
        /*
            TODO:
            1. Asynchronously load images
            2. ✓ Set look and feel ✓
            3. ✓ Open menu to start new game / load game ✓
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

        File save = Setup.showSetupWindow();
        Game g = null;

        if (save == null) {
            g = new Game();
        } else {
            boolean error = false;
            String errorMessage = "";

            try {
                Gson gson = new Gson();
                g = gson.fromJson(new FileReader(save), Game.class);
                g.setLoadedFrom(save);
            } catch (FileNotFoundException e) {
                error = true;
                errorMessage = "The save file specified could not be loaded. Would you like to begin a new game?";
            } catch (JsonSyntaxException er) {
                error = true;
                errorMessage = "An error occurred while parsing the specified save file. Would you like to begin a new game?";
            }

            if (error) {
                int response = JOptionPane.showConfirmDialog(null, errorMessage, "An error has occurred",
                        JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    g = new Game();
                } else {
                    return;
                }
            }
        }

        try {
            while (!imageLoading.isDone()) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startGUI(g);
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
