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
    private int cardCount;

    // If the lower cards peek out of the stack, otherwise only the top card is visible

    private int flipType;
    private int tieredXOffset; // There are the value(s) that the cards will be offset by when they are tiered.
    private int tieredYOffset;


    public CardStack(int flipType){
        this.cards = new Stack<>();

        this.flipType = flipType;
        solveFlipType(flipType);

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
        if(cards.size() > 0){
            for(int i=0;i<this.cards.capacity();i++) {
                this.cards.get(i).draw(g, x + (tieredXOffset*i), y + (tieredYOffset*i));
            }
        }else{
            g.drawImage(ImageLoader.emptySpotTexture, x, y, null);
        }
    }



    public void addNewCard(int id){
        this.cards.add(new Card(id));
    }

    private void solveFlipType(int flipType){
        switch(flipType){
            case FLIPTYPE_TOP:
                cards.get(cards.size() - 1).setFaceDir(true);
                break;
            case FLIPTYPE_ALL:
                for(int i=0;i<cards.size();i++) {
                    cards.get(i).setFaceDir(true);
                }
        }
        //There isn't a case for none because all cards are initialized to be face-down by default
    }

}
