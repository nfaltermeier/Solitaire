package solitaire;

import solitaire.graphics.ImageLoader;

public abstract class Solitaire {
    public static void main(String[] args) {
        /*
            TODO:
            1. Asynchronously load images
            2. Set look and feel
            3. Open menu to start new game / load game (later?)
            4. Open main game GUI (Until saving/loading is implemented)
         */

        String resourceFolderPath = ""; //Make this String be whatever path needed to point to the 'images' folder
        ImageLoader.init(resourceFolderPath);
    }
}
