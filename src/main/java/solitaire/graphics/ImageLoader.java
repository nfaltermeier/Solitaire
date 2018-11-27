package solitaire.graphics;

import java.awt.image.BufferedImage;

public class ImageLoader {

    public static BufferedImage[] cardTextures;
    public static BufferedImage backTexture; //This is the texture of the back of a card
    public static BufferedImage emptySpotTexture; //This is the texture of a stack when it's empty



    public static void init(String resFolderPath){
        cardTextures = new BufferedImage[52];
        int cardTexWidth = 40; //2.5 * 16
        int cardTexHeight = 56; //3.5 * 16

        // Max- Here's how i thought about loading the images into the program
        String extension = ".png";
        String bridging = "_of_";
        String[] suitNames = {"clubs", "diamonds", "hearts", "spades"};
        String[] numberNames = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

        // Change^^  Lets make a function that returns the image based upon the ID of the card passed in

        for(int i=1;i<=cardTextures.length;i++) {
            //Create a BufferedImage for each card's texture
        }

        // Create a BufferedImage for the texture of the back of each card
        // Create a BufferedImage for the texture of a stack when it's empty
    }

}
