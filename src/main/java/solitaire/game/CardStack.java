package solitaire.game;

import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import java.awt.*;
import java.util.Stack;

public class CardStack implements IDrawable {
    public final static int FLIPTYPE_TOP = 0;
    public final static int FLIPTYPE_NONE = 1;
    public final static int FLIPTYPE_ALL = 2;


    private Stack<Card> cards;

    // If the lower cards peek out of the stack, otherwise only the top card is visible

    private int flipType;
    private int tieredXOffset; // There are the value(s) that the cards will be offset by when they are tiered.
    private int tieredYOffset;

    private Rectangle bounds;


    public CardStack(int flipType) {
        this.cards = new Stack<>();

        this.flipType = flipType;

        this.tieredXOffset = 0;
        this.tieredYOffset = 0;
    }

    public CardStack(int flipType, int tieredXOffset, int tieredYOffset) {
        this(flipType);

        this.tieredXOffset = tieredXOffset;
        this.tieredYOffset = tieredYOffset;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        if (cards.size() > 0) {
            for (int i = 0; i < this.cards.size(); i++) {
                this.cards.get(i).draw(g, x + (tieredXOffset * i), y + (tieredYOffset * i));
            }
        } else {
            g.drawImage(ImageLoader.emptySpotTexture, x, y, null);
        }
        calcBounds(x, y);
    }


    public void addNewCard(int id) {
        this.cards.add(new Card(id));
        solveFlipType(flipType);
    }

    public void solveFlipType(int flipType) {
        switch (flipType) {
            case FLIPTYPE_TOP:
                for (int i = 0; i < cards.size() - 1; i++) {
                    cards.get(i).setFaceDir(false);
                }
                cards.get(cards.size() - 1).setFaceDir(true);
                break;
            case FLIPTYPE_ALL:
                for (int i = 0; i < cards.size(); i++) {
                    cards.get(i).setFaceDir(true);
                }
                break;
            case FLIPTYPE_NONE:
                for (int i = 0; i < cards.size(); i++) {
                    cards.get(i).setFaceDir(false);
                }
                break;
        }
    }

    private void calcBounds(int x, int y) {
        int width = (this.tieredXOffset * (Math.max(0, this.getCardCount() - 1))) + ImageLoader.cardTexWidth;
        int height = (this.tieredYOffset * (Math.max(0, this.getCardCount() - 1))) + ImageLoader.cardTexHeight;

        this.bounds = new Rectangle(x, y, width, height);
    }

    public boolean inBounds(int x, int y) {
        return bounds.contains(x, y);
    }

    public int getClickedCardID(int x, int y) {
        int maxIndex = 0;
        for (int i = this.getCardCount()-1; i >= 0; i--) {
            if (this.cards.get(i).inBounds(x, y)) {
                maxIndex = Math.max(maxIndex, i);
            }
        }

        return maxIndex;
    }

    public CardStack getWholeCardStack(int minIndex, int maxIndex) {
        //System.out.println("minIndex: " + minIndex + "  maxIndex: " + maxIndex);
        CardStack newStack = new CardStack(this.flipType);
        for (int i = minIndex; i < maxIndex; i++) {
            newStack.addNewCard(this.cards.get(i).getIDNum());
        }

        return newStack;
    }

    public void deletePartOfStack(int maxIndex) {
        for (int i = 0; i < maxIndex; i++) {
            this.cards.remove(i);
        }
    }

    public Card getCard(int index){
        return this.cards.get(index);
    }

    public int getCardCount(){
        return this.cards.size();
    }

    public int getLastCardValue(){

        int val = 0;
        if (cards.size() > 0) {
             val = cards.get(cards.size() - 1).getVal();
        }
        return val;
    }

}
