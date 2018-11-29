package solitaire.graphics;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    public static BufferedImage[] cardTextures;
    public static BufferedImage backTexture; //This is the texture of the back of a card
    public static BufferedImage emptySpotTexture; //This is the texture of a stack when it's empty

    public static int cardTexWidth;
    public static int cardTexHeight;


    public static void init(String resFolderPath){
        cardTextures = new BufferedImage[52];
        cardTexWidth = 100; //2.5 * 16
        cardTexHeight = 145; //3.5 * 16

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        String filepath = resFolderPath + "/empty.png";
        emptySpotTexture = readImage(filepath, cl);

        filepath = resFolderPath + "/back.png";
        backTexture = readImage(filepath, cl);

        String extension = ".png";
        String bridging = "_of_";
        String[] suitNames = {"clubs", "diamonds", "hearts", "spades"};
        String[] numberNames = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

        for(int i = 0; i < cardTextures.length; i++) {
            filepath = resFolderPath + "/" + numberNames[i % 13] + bridging + suitNames[i / 13] + extension;
            cardTextures[i] = readImage(filepath, cl);
        }
    }

    private static @Nullable BufferedImage readImage(String filepath, ClassLoader cl) {
        try {
            // Works properly when the files are in a jar or not in a jar
            InputStream inputStream = cl.getResourceAsStream(filepath);
            if (inputStream != null) {
                return ImageIO.read(inputStream);
            } else {
                // null indicates it could not read the file
                System.err.println("Failed to load file '" + filepath + "'");
            }
        } catch (IOException e) {
            System.err.println("Failed to load file '" + filepath + "':");
            e.printStackTrace();
        }

        // Something went wrong
        return null;
    }
}
