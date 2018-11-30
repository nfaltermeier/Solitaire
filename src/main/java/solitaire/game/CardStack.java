/*  This file defines the CardStack object, which is used to store and manipulate multiple Card
    objects. It also can draw all stored Card objects at an offset using offset values.
 */
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

    private final int flipType;
    public int tieredXOffset; // There are the value(s) that the cards will be offset by when they are tiered.
    public int tieredYOffset;

    private Rectangle bounds;

    /**
     * Creates a new CardStack with the specified flip type and stack type, but with
     * no cards in it, and no drawing offset values.
     * @param flipType The type of flip type this CardStack will use, which determines which cards are face up.
     * @param stackType The type of CardStack this is, which is used to determine how cards are allowed to move.
     */
    public CardStack(int flipType, int stackType) {
        this(flipType, stackType, 0, 0);
    }

    /**
     * Creates a new CardStack with the specified flip type, stack type, and
     * the specified drawing offsets, but with no cards in it.
     * @param flipType The type of flip type this CardStack will use, which determines which cards are face up.
     * @param stackType The type of CardStack this is, which is used to determine how cards are allowed to move.
     * @param tieredXOffset The horizontal drawing offset that each card in the stack will be drawn at.
     * @param tieredYOffset The vertical drawing offset that each card in the stack will be drawn at.
     */
    public CardStack(int flipType, int stackType, int tieredXOffset, int tieredYOffset) {
        this(null, flipType, stackType, tieredXOffset, tieredYOffset);
    }

    /**
     * Creates a new CardStack with the specified flip type, stack type,
     * the specified drawing offsets, and the passed in List of Card objects.
     * @param cards A List containing Card objects.
     * @param flipType The type of flip type this CardStack will use, which determines which cards are face up.
     * @param stackType The type of CardStack this is, which is used to determine how cards are allowed to move.
     * @param tieredXOffset The horizontal drawing offset that each card in the stack will be drawn at.
     * @param tieredYOffset The vertical drawing offset that each card in the stack will be drawn at.
     */
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

    /**
     * Draws the CardStack to the screen with the specified starting coordinates, where each card's position is
     * the initial position plus the offset values.
     * @param g The Graphics object that allows images to be drawn to the screen.
     * @param x The horizontal position that the CardStack is drawn at.
     * @param y The vertical position that the CardStack is drawn at.
     */
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

    /**
     * Creates a new Card object in the CardStack based on the passed in ID value, then
     * solves which cards should be drawn face up or not.
     * @param id The ID of the Card to be created, from 0 to 51.
     */
    public void addNewCard(int id) {
        this.cards.add(new Card(id));
        solveFlipType(flipType);
    }

    /**
     * Solves which cards should be drawn face up or not based on the passed in flip type.
     * @param flipType The type of logic to be used when solving which cards should be face up.
     */
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
                for (Card card : cards) {
                    card.setFaceDir(true);
                }
                break;
            case FLIPTYPE_NONE:
                for (Card card : cards) {
                    card.setFaceDir(false);
                }
                break;
        }
    }

    /**
     * Calculates the overall bounding box of the CardStack.
     * @param x The top left x-coordinate to be used in calculating the bounding box.
     * @param y The top left y-coordinate to be used in calculating the bounding box.
     */
    private void calcBounds(int x, int y) {
        int width = (this.tieredXOffset * (Math.max(0, this.getCardCount() - 1))) + ImageLoader.cardTexWidth;
        int height = (this.tieredYOffset * (Math.max(0, this.getCardCount() - 1))) + ImageLoader.cardTexHeight;

        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Checks if the passed in position is within the CardStack's bounding box.
     * @param x The x-coordinate of the position being checked.
     * @param y The x-coordinate of the position being checked.
     * @return A boolean representing if the position is within the bounding box or not.
     */
    public boolean inBounds(int x, int y) {
        return bounds.contains(x, y);
    }

    /**
     * Finds the forward-most card between all cards the passed in position is within.
     * @param x The x-coordinate to check for in each card's bounding box.
     * @param y The x-coordinate to check for in each card's bounding box.
     * @return The index of the forward-most card.
     */
    public int getClickedCardIndex(int x, int y) {
        int maxIndex = 0;
        for (int i = this.getCardCount() - 1; i >= 0; i--) {
            if (this.cards.get(i).inBounds(x, y)) {
                maxIndex = Math.max(maxIndex, i);
            }
        }

        return maxIndex;
    }

    /**
     * Finds and returns a CardStack containing of the cards from a minimum index (inclusive)
     * to a maximum index (exclusive) within this CardStack.
     * @param minIndex The minimum index to get cards from in this CardStack.
     * @param maxIndex The maximum index to get cards from in this CardStack.
     * @return A CardStack containing the cards between the two passed in index values.
     */
    public CardStack getSubstack(int minIndex, int maxIndex) {
        return new CardStack(cards.subList(minIndex, maxIndex), FLIPTYPE_NONE, STACKTYPE_TEMP, 0, 0);
    }

    /**
     * Appends all of the Cards in the passed in CardStack to the current one.
     * @param newStack The CardStack that the cards being added to the current CardStack are pulled from.
     */
    public void appendStack(CardStack newStack) {
        this.cards.addAll(newStack.cards);
    }

    /**
     * Deletes all Cards in the passed in CardStack from the current one. It then flips the new top
     * card in the CardStack face up if it is face down.
     * @param c The CardStack containing the Cards to be deleted from this CardStack.
     */
    public void deletePartOfStack(CardStack c) {
        this.cards.removeAll(c.cards);

        if (this.getCardCount() > 0) {
            if (!this.getCard(this.getCardCount() - 1).isFaceUp()) {
                this.getCard(this.getCardCount() - 1).setFaceDir(true);
            }
        }
    }

    /**
     * Gets the Card at the specified index value.
     * @param index The index value of the Card in this CardStack.
     * @return The Card object at the specified index value.
     */
    public Card getCard(int index) {
        return this.cards.get(index);
    }

    /**
     * Returns how many Cards are stored in this CardStack.
     * @return An integer representing how many Cards are stored in this CardStack.
     */
    public int getCardCount() {
        return this.cards.size();
    }
}
