package solitaire.game;

import solitaire.graphics.IDrawable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Card implements IDrawable {
    private boolean isFaceUp;
    private BufferedImage image;
    private int id;

    @Override
    public void draw(Graphics g, int x, int y) {

    }
}
