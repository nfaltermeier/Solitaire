/*
    Indicates that implementing classes are able to be drawn with a Graphics object at specified coordinates
 */

package solitaire.graphics;

import java.awt.Graphics;

public interface IDrawable {
    /**
     * Draws the implementing class where x and y form the top left corner of the image
     * @param g The Graphics to draw the image on
     * @param x The x coordinate of top left corner to start drawing the image at
     * @param y The y coordinate of top left corner to start drawing the image at
     */
    void draw(Graphics g, int x, int y);
}
