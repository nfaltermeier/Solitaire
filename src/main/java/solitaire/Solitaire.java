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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Solitaire {
    private JFrame frame;

    public static void main(String[] args) {
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
        Solitaire s = new Solitaire();
        Game g = null;

        if (save == null) {
            g = new Game(s);
        } else {
            boolean error = false;
            String errorMessage = "";

            try {
                Scanner scanner = new Scanner(save);
                String gameData = scanner.nextLine();
                String gameHash = scanner.nextLine();

                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    if(!gameHash.equals(new String(messageDigest.digest(gameData.getBytes(StandardCharsets.UTF_8))))) {
                        JOptionPane.showMessageDialog(null,
                                "Save data appears to be corrupted. Please try to load a new save or start a new game.",
                                "Error loading save", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                } catch (NoSuchAlgorithmException e1) {
                    System.err.println("SHA-256 MessageDigest is not supported :(");
                }

                Gson gson = new Gson();
                g = gson.fromJson(gameData, Game.class);
                g.loadedFrom = save;
                g.setSolitaire(s);
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
                    g = new Game(s);
                } else {
                    System.exit(0);
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

        s.displayGame(g);
    }

    public void displayGame(Game game) {
        boolean createNewFrame = frame == null;
        if (createNewFrame) {
            frame = new JFrame((GraphicsConfiguration) null);
            frame.setResizable(false);
            frame.setTitle("Solitaire");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        } else {
            frame.getContentPane().removeAll();
        }

        JPanel panel = new GameDisplay(game);
        frame.add(panel);

        if (createNewFrame) {
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } else {
            frame.revalidate();
        }
    }
}
