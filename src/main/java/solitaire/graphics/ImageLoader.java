package solitaire.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    public static BufferedImage[] cardTextures;
    public static BufferedImage backTexture; //This is the texture of the back of a card
    public static BufferedImage emptySpotTexture; //This is the texture of a stack when it's empty



    public static void init(String resFolderPath){
        cardTextures = new BufferedImage[52];
        int cardTexWidth = 40; //2.5 * 16
        int cardTexHeight = 56; //3.5 * 16

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        try {
            // Works properly when the files are in a jar or not in a jar
            InputStream inputStream = cl.getResourceAsStream(resFolderPath + "/back.png");
            if (inputStream != null) {
                backTexture = ImageIO.read(inputStream);
            } else {
                // null indicates it could not read the file
                System.err.println("Failed to load file '" + resFolderPath + "/back.png'");
            }
        } catch (IOException e) {
            System.err.println("Failed to load file '" + resFolderPath + "/back.png':");
            e.printStackTrace();
        }

        for(int i = 1; i <= cardTextures.length; i++) {
            //Create a BufferedImage for each card's texture
        }

        // Create a BufferedImage for the texture of the back of each card
        // Create a BufferedImage for the texture of a stack when it's empty
    }

}
