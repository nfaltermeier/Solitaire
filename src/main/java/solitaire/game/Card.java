/*
The card class holds data mostly unique data used to identity, draw, and perform comparisons on the card.
 */

package solitaire.game;

import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Objects;

public class Card implements IDrawable {
    public static int SUIT_SPADE = 1;
    public static int SUIT_CLUB = 2;
    public static int SUIT_HEART = 3;
    public static int SUIT_DIAMOND = 4;

    private boolean isFaceUp;
    private final int id;
    public final int val;
    public final int suit;

    public int lastX;
    public int lastY;


    /**Card
     * Initializer which sets the unique properties of the object based off the id. All card ID's are 0 - 51; values, and suits are formed from the ID.
      * @param id
     */
    public Card(int id) {
        this.id = id;
        this.val = id % 13;
        this.suit = id / 13 + 1;

        this.isFaceUp = false;
    }

    /**draw
     * Draws the card using the x and y coordinates.
     * @param g
     * @param x
     * @param y
     */
    @Override
    public void draw(Graphics g, int x, int y) {
        if (isFaceUp) {
	        /*
            Caching card images in the card causes problems because game is created before card images are guaranteed
            to be loaded and may, when the game is saved, cause all the images to be included in the game save
            */
            g.drawImage(ImageLoader.cardTextures[id], x, y, null);
        } else {
            g.drawImage(ImageLoader.backTexture, x, y, null);
        }

        lastX = x;
        lastY = y;
    }

    /**setFaceDir
     * Setter method that makes the card either face up or face down.
     * @param isUp
     */
    public void setFaceDir(boolean isUp) {
        this.isFaceUp = isUp;
    }

    /**isFaceUp
     * Getter method for face direction of card.
     * @return
     */
    public boolean isFaceUp() {
        return isFaceUp;
    }

    public boolean inBounds(int x, int y) {
        Rectangle bounds = new Rectangle(lastX, lastY, ImageLoader.cardTexWidth, ImageLoader.cardTexHeight);
        return bounds.contains(x, y);
    }

    /**isSameColor
     * Determines if this objects card color (red or black) is the same as the card color passed in.
     * Used for validating moves.
     * @param c
     * @return
     */
    public boolean isSameColor(Card c) {
        switch (suit) {
            case 1:
            case 2:
                return (c.suit == SUIT_SPADE || c.suit == SUIT_CLUB);
            case 3:
            case 4:
                return (c.suit == SUIT_HEART || c.suit == SUIT_DIAMOND);
        }

        return true;
    }

    /**equals
     * Determines if this card's ID is the same as the card passed in.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return id == card.id;
    }

    /**hashCode
     * Makes hash code representation of the card.
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
