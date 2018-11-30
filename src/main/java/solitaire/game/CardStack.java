package solitaire.game;

import org.jetbrains.annotations.Nullable;
import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;
import java.util.Stack;

public class CardStack implements IDrawable {
    public final static int FLIPTYPE_TOP = 0;
    public final static int FLIPTYPE_NONE = 1;
    public final static int FLIPTYPE_ALL = 2;

    public final static int STACKTYPE_MAIN = 0;
    public final static int STACKTYPE_DISPLAYSTOCK = 1;
    public final static int STACKTYPE_HIDDENSTOCK = 2;
    public final static int STACKTYPE_FOUNDATION = 3;
    public final static int STACKTYPE_TEMP = 4;
    public final static int STACKTYPE_HIDDENDISPLAYSTOCK = 5;

    private Stack<Card> cards;
    public final int stackType;

    // If the lower cards peek out of the stack, otherwise only the top card is visible

    private int flipType;
    public int tieredXOffset; // There are the value(s) that the cards will be offset by when they are tiered.
    public int tieredYOffset;

    private Rectangle bounds;


    public CardStack(int flipType, int stackType) {
        this(flipType, stackType, 0, 0);
    }

    public CardStack(int flipType, int stackType, int tieredXOffset, int tieredYOffset) {
        this(null, flipType, stackType, tieredXOffset, tieredYOffset);
    }

    private CardStack(@Nullable List<Card> cards, int flipType, int stackType, int tieredXOffset, int tieredYOffset) {
        this.cards = new Stack<>();
        this.stackType = stackType;
        this.flipType = flipType;
        this.tieredXOffset = tieredXOffset;
        this.tieredYOffset = tieredYOffset;

        if (cards != null) {
            this.cards.addAll(cards);
        }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        if (cards.size() > 0) {
            for (int i = 0; i < this.cards.size(); i++) {
                this.cards.get(i).draw(g, x + (tieredXOffset * i), y + (tieredYOffset * i));
            }
        } else {
            g.drawImage(ImageLoader.emptySpotTexture, x, y, null);

            if (stackType == STACKTYPE_HIDDENSTOCK)
                g.drawImage(ImageLoader.redoSymbol, x + 15, y + 37, null);
        }

        calcBounds(x, y);

    }

    public void addNewCard(int id) {
        this.cards.add(new Card(id));
        solveFlipType(flipType);
    }

    public void solveFlipType(int flipType) {
        if (getCardCount() == 0)
            return;

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

    public int getClickedCardIndex(int x, int y) {
        int maxIndex = 0;
        for (int i = this.getCardCount() - 1; i >= 0; i--) {
            if (this.cards.get(i).inBounds(x, y)) {
                maxIndex = Math.max(maxIndex, i);
            }
        }

        return maxIndex;
    }

    public CardStack getSubstack(int minIndex, int maxIndex) {
        return new CardStack(cards.subList(minIndex, maxIndex), FLIPTYPE_NONE, STACKTYPE_TEMP, 0, 0);
    }

    public void appendStack(CardStack newStack) {
        this.cards.addAll(newStack.cards);
    }

    public void deletePartOfStack(CardStack c) {
        this.cards.removeAll(c.cards);

        if (this.getCardCount() > 0) {
            if (!this.getCard(this.getCardCount() - 1).isFaceUp()) {
                this.getCard(this.getCardCount() - 1).setFaceDir(true);
            }
        }
    }

    public Card getCard(int index) {
        return this.cards.get(index);
    }

    public int getCardCount() {
        return this.cards.size();
    }
}
