package solitare.game;

import solitare.graphics.IDrawable;

import java.awt.*;
import java.util.Stack;

public class CardStack implements IDrawable {
    private Stack<Card> cards;
    // If the lower cards peek out of the stack, otherwise only the top card is visible
    private boolean tieredStack;

    @Override
    public void draw(Graphics g, int x, int y) {

    }
}
