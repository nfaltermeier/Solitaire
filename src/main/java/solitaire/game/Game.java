/*
    Represents the current state of the game. Handles interaction when the cards are clicked on.
 */

package solitaire.game;

import org.jetbrains.annotations.Nullable;
import solitaire.Solitaire;
import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import javax.swing.JOptionPane;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Game implements IDrawable {
    // Represents a complete game with all of its CardStacks and data

    private CardStack[] foundationStacks;
    private CardStack[] mainPiles;
    private CardStack hiddenStock;
    private CardStack displayStock;
    private CardStack hiddenDisplayStock;

    private SelectedStackResult highlightedStack;

    @Nullable
    public transient File loadedFrom;
    private transient Solitaire solitaire;

    public int seconds;
    public int minutes;

    /**
     * Creates a new game with a reference to the controlling Solitaire instance
     * @param solitaire the instance of Solitaire that started the game
     */
    public Game(Solitaire solitaire) {
        loadedFrom = null;
        this.solitaire = solitaire;
        //Add more here later, for now it always initializes a new game
        initNewGame();
    }

    /**
     * Sets the reference to the controlling Solitaire instance
     * @param solitaire the instance of Solitaire that started the game
     */
    public void setSolitaire(Solitaire solitaire) {
        this.solitaire = solitaire;
    }

    /**
     * Draws the state of the game
     * @param g The graphics instance to draw the game on to
     * @param x The x coordinate of the top left corner to start drawing from
     * @param y The y coordinate of the top left corner to start drawing from
     */
    @Override
    public void draw(Graphics g, int x, int y) {
        hiddenStock.draw(g, x + 20, y + 10);
        displayStock.draw(g, x + 20, y + 185);

        for (int i = 0; i < mainPiles.length; i++) {
            mainPiles[i].draw(g, x + 190 + i * 120, y + 10);
        }

        for (int i = 0; i < foundationStacks.length; i++) {
            foundationStacks[i].draw(g, x + 1080, y + 40 + i * 165);
        }

        if (highlightedStack != null && highlightedStack.fullStack.getCardCount() != 0) {
            int xPos = highlightedStack.subStack.getCard(0).lastX;
            int yPos = highlightedStack.subStack.getCard(0).lastY;
            int width = ImageLoader.cardTexWidth;
            int height = ImageLoader.cardTexHeight + (mainPiles[0].tieredYOffset * (highlightedStack.subStack.getCardCount() - 1));

            g.drawImage(ImageLoader.highlightTexture, xPos, yPos, width, height, null, null);
        }

    }

    /**
     * Sets the state of the game to that of a new game
     */
    public void initNewGame() {
        seconds = 0;
        minutes = 0;

        highlightedStack = null;
        hiddenDisplayStock = new CardStack(CardStack.FLIPTYPE_NONE, CardStack.STACKTYPE_HIDDENDISPLAYSTOCK);

        foundationStacks = new CardStack[4];
        mainPiles = new CardStack[7];

        // Creates a list of cards to be distributed into the different piles
        ArrayList<Integer> remainingCards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            remainingCards.add(i);
        }

        for (int i = 0; i < foundationStacks.length; i++) {
            foundationStacks[i] = new CardStack(CardStack.FLIPTYPE_ALL, CardStack.STACKTYPE_FOUNDATION);
        }

        Random rand = new Random();

        for (int i = 0; i < mainPiles.length; i++) {
            mainPiles[i] = new CardStack(CardStack.FLIPTYPE_TOP, CardStack.STACKTYPE_MAIN, 0, 40);
            for (int j = 0; j < i + 1; j++) {
                int indexChoice = rand.nextInt(remainingCards.size());
                mainPiles[i].addNewCard(remainingCards.get(indexChoice));
                remainingCards.remove(indexChoice);
            }
        }

        hiddenStock = new CardStack(CardStack.FLIPTYPE_NONE, CardStack.STACKTYPE_HIDDENSTOCK);
        displayStock = new CardStack(CardStack.FLIPTYPE_ALL, CardStack.STACKTYPE_DISPLAYSTOCK, 0, 40);
        while (remainingCards.size() > 0) {
            int indexChoice = rand.nextInt(remainingCards.size());
            hiddenStock.addNewCard(remainingCards.get(indexChoice));
            remainingCards.remove(indexChoice);
        }

    }

    /**
     * Gets the instance of the CardStack that was clicked on
     * @param clickedX The x coordinate of where the mouse was clicked
     * @param clickedY The y coordinate of where the mouse was clicked
     * @return The full stack that was clicked on and every card below the card that was clicked on, or null if no stack
     *         was clicked
     */
    public @Nullable SelectedStackResult getSelectedCardstack(int clickedX, int clickedY) {
        //Check through all main piles, use an individual card version for the display stock pile

        for (CardStack c : mainPiles) {
            if (c.inBounds(clickedX, clickedY)) {
                int clickedCardIndex = c.getClickedCardIndex(clickedX, clickedY);
                if (c.getCardCount() == 0 || c.getCard(clickedCardIndex).isFaceUp()) {
                    return new SelectedStackResult(c, c.getSubstack(clickedCardIndex, c.getCardCount()));
                }
            }
        }

        if (displayStock.inBounds(clickedX, clickedY)) {
            // Only the last displayStock card can be selected
            if (displayStock.getCardCount() > 0 &&
                    displayStock.getClickedCardIndex(clickedX, clickedY) == displayStock.getCardCount() - 1) {
                return new SelectedStackResult(displayStock,
                        displayStock.getSubstack(displayStock.getCardCount() - 1, displayStock.getCardCount()));
            }

            return null;
        }

        for (CardStack c : foundationStacks) {
            if (c.inBounds(clickedX, clickedY)) {
                if (c.getCardCount() == 0) {
                    return new SelectedStackResult(c, c.getSubstack(0, 0));
                } else {
                    return new SelectedStackResult(c, c.getSubstack(c.getCardCount() - 1, c.getCardCount()));
                }
            }
        }

        return null;
    }

    /**
     * Is called when the game display is clicked on. Handles selecting a stack or moving cards from the previously
     * selected stack
     * @param x The x coordinate of where the mouse was clicked
     * @param y The y coordinate of where the mouse was clicked
     */
    public void onClick(int x, int y) {
        if (hiddenStock.inBounds(x, y)) {
            cycleStock();

            highlightedStack = null;

            if (checkWinConditions()) {
                onGameWon();
            }
        } else {
            SelectedStackResult clickedStack = getSelectedCardstack(x, y);

            if (clickedStack == null) {
                highlightedStack = null;
            } else {
                if (highlightedStack == null) {
                    highlightedStack = clickedStack;
                } else {
                    switch (clickedStack.fullStack.stackType) {
                        case CardStack.STACKTYPE_MAIN:
                        case CardStack.STACKTYPE_DISPLAYSTOCK:
                            case CardStack.STACKTYPE_FOUNDATION:
                            conditionallyMoveStack(clickedStack);
                            break;
                    }

                    highlightedStack = null;
                }

                if (checkWinConditions()) {
                    onGameWon();
                }
            }
        }
    }

    /**
     * Gets called when the game is won. Notifies the user they won and gives them the option of playing a new game
     * or quitting
     */
    private void onGameWon() {
        int response = JOptionPane.showConfirmDialog(null,
                "Congratulations! You won! Would you like to start a new game?", "You won!",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            System.exit(0);
        }
    }

    /**
     * Causes the controlling solitaire instance to start a new game
     */
    public void startNewGame() {
        solitaire.displayGame(new Game(solitaire));
    }

    /**
     * Moves a card from the hidden stock to the display stock, and if the display stock is full moves a card from
     * the display stock to the hidden display stock. If the hidden stock is empty moves all cards from the
     * display stock and the hidden display stock back to the hidden stock.
     */
    public void cycleStock() {
        if (hiddenStock.getCardCount() > 0) {

            // grab bottom card and add it to temp stack while also removing it from the
            // original stack
            CardStack tempStack = hiddenStock.getSubstack(0, 1);
            hiddenStock.deletePartOfStack(tempStack);

            // add tempStack to displayStock
            displayStock.appendStack(tempStack);
            displayStock.solveFlipType(CardStack.FLIPTYPE_ALL);

            // if displayStock is full
            if (displayStock.getCardCount() > 3) {
                // Remove bottom card and put it back into hiddenstock
                CardStack tempStack2 = displayStock.getSubstack(0, 1);
                displayStock.deletePartOfStack(tempStack2);
                hiddenDisplayStock.appendStack(tempStack2);
            }

            hiddenStock.solveFlipType(CardStack.FLIPTYPE_NONE);
        } else if (hiddenDisplayStock.getCardCount() > 0) {
            hiddenStock.appendStack(hiddenDisplayStock);
            hiddenStock.appendStack(displayStock);
            hiddenStock.solveFlipType(CardStack.FLIPTYPE_NONE);
            hiddenDisplayStock.deletePartOfStack(hiddenDisplayStock);
            displayStock.deletePartOfStack(displayStock);
        }
    }

    /**
     * Moves a CardStack to the highlightedStack if it is valid to move it there
     * @param refStack The stack to move to the highlightedStack
     */
    private void conditionallyMoveStack(SelectedStackResult refStack) {
        if (highlightedStack.subStack.getCardCount() > 0) {
            if (canMoveStack(highlightedStack.subStack, refStack.fullStack)) {
                refStack.fullStack.appendStack(highlightedStack.subStack);
                highlightedStack.fullStack.deletePartOfStack(highlightedStack.subStack);
            }
        }
    }

    /**
     * Checks whether the receivingStack
     * @param stackToMove The stack that is potentially being moved
     * @param receivingStack The stack that is having cards moved onto
     * @return True if the stack can be moved
     */
    private boolean canMoveStack(CardStack stackToMove, CardStack receivingStack) {
        if (receivingStack.stackType == CardStack.STACKTYPE_MAIN) {
            if (receivingStack.getCardCount() == 0) {
                Card attemptCard = stackToMove.getCard(0);
                // If it's a king
                return attemptCard.val == 12 && attemptCard.isFaceUp();
            } else {
                Card destCard = receivingStack.getCard(receivingStack.getCardCount() - 1);
                Card attemptCard = stackToMove.getCard(0);

                if (destCard.val == (attemptCard.val + 1) && attemptCard.isFaceUp()) {
                    return !destCard.isSameColor(attemptCard);
                }
            }
        } else if (receivingStack.stackType == CardStack.STACKTYPE_FOUNDATION) {
            if (stackToMove.getCardCount() == 1) {
                Card cardBeingMoved = stackToMove.getCard(0);

                if (receivingStack.getCardCount() == 0) {
                    // If it's an ace
                    return cardBeingMoved.val == 0;
                } else {
                    Card cardBeingMovedOnto = receivingStack.getCard(receivingStack.getCardCount() - 1);

                    return cardBeingMoved.val == cardBeingMovedOnto.val + 1 &&
                            cardBeingMoved.suit == cardBeingMovedOnto.suit;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the player has depleted the display stock and hidden stock and revealed every card in the main piles
     * because if that has been done winning is inevitable
     * @return True if the above conditions have been met
     */
    private boolean checkWinConditions() {
        if (hiddenStock.getCardCount() != 0 || displayStock.getCardCount() != 0) {
            return false;
        }

        for (CardStack stack : mainPiles) {
            for (int i = 0; i < stack.getCardCount(); i++) {
                if (!stack.getCard(i).isFaceUp()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * The result of calling getSelectedCardstack. Represents a pair of CardStacks where one is contained within the other
     */
    private class SelectedStackResult {
        public final CardStack fullStack;
        public final CardStack subStack;

        /**
         * Pairs two CardStacks together
         * @param fullStack The stack containing the card that was selected
         * @param subStack The card that was selected and every card below it
         */
        public SelectedStackResult(CardStack fullStack, CardStack subStack) {
            this.fullStack = fullStack;
            this.subStack = subStack;
        }
    }
}
