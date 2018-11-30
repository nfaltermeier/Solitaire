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

    public Game(Solitaire solitaire) {
        loadedFrom = null;
        this.solitaire = solitaire;
        //Add more here later, for now it always initializes a new game
        initNewGame();
    }

    public void setSolitaire(Solitaire solitaire) {
        this.solitaire = solitaire;
    }

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

    public void initNewGame() {
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

    public @Nullable SelectedStackResult getSelectedCardstack(int clickedX, int clickedY) {
        //Check through all main piles, use an individual card version for the display stock pile

        for (CardStack c : mainPiles) {
            if (c.inBounds(clickedX, clickedY)) {
                int clickedCardIndex = c.getClickedCardIndex(clickedX, clickedY);
                return new SelectedStackResult(c, c.getSubstack(clickedCardIndex, c.getCardCount()));
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
                            internalStackMove(clickedStack);
                            break;
                        case CardStack.STACKTYPE_FOUNDATION:
                            if (highlightedStack.subStack.getCardCount() == 1) {
                                Card cardBeingMoved = highlightedStack.subStack.getCard(0);

                                if (clickedStack.fullStack.getCardCount() == 0) {
                                    // If it's an ace
                                    if (cardBeingMoved.getVal() == 0) {
                                        clickedStack.fullStack.appendStack(highlightedStack.subStack);
                                        highlightedStack.fullStack.deletePartOfStack(highlightedStack.subStack);
                                    }
                                } else {
                                    Card cardBeingMovedOnto = clickedStack.subStack.getCard(0);

                                    if (cardBeingMoved.getVal() == cardBeingMovedOnto.getVal() + 1 &&
                                            cardBeingMoved.getSuit() == cardBeingMovedOnto.getSuit()) {
                                        clickedStack.fullStack.appendStack(highlightedStack.subStack);
                                        highlightedStack.fullStack.deletePartOfStack(highlightedStack.subStack);
                                    }
                                }
                            }
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

    private void onGameWon() {
        int response = JOptionPane.showConfirmDialog(null,
                "Congratulations! You won! Would you like to start a new game?", "You won!",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            solitaire.killDisplay();
        }
    }

    public void startNewGame() {
        solitaire.displayGame(new Game(solitaire));
    }

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

    private void internalStackMove(SelectedStackResult refStack) {
        if (highlightedStack.subStack.getCardCount() > 0) {
            if (canMoveStack(highlightedStack.subStack, refStack.fullStack)) {
                refStack.fullStack.appendStack(highlightedStack.subStack);

                highlightedStack.fullStack.deletePartOfStack(highlightedStack.subStack);
            }
        }
    }

    private boolean canMoveStack(CardStack movedStack, CardStack placedStack) {
        boolean b = false;

        if (placedStack.stackType == CardStack.STACKTYPE_MAIN) {
            if (placedStack.getCardCount() == 0) {
                Card attemptCard = movedStack.getCard(0);
                if (attemptCard.getVal() == 12 && attemptCard.isFaceUp()) { // If it's a king
                    b = true;
                }
            } else {
                Card destCard = placedStack.getCard(placedStack.getCardCount() - 1);
                Card attemptCard = movedStack.getCard(0);

                if (destCard.getVal() == (attemptCard.getVal() + 1) && attemptCard.isFaceUp()) {
                    if (!destCard.isSameColor(attemptCard)) {
                        b = true;
                    }
                }
            }
        }

        return b;
    }

    private boolean checkWinConditions() {
        if(hiddenStock.getCardCount() != 0 || displayStock.getCardCount() != 0){
            return false;
        }

        for(CardStack stack : mainPiles){
            for(int i=0;i<stack.getCardCount();i++){
                if(!stack.getCard(i).isFaceUp()){
                    return false;
                }
            }
        }
        return true;
    }

    private class SelectedStackResult {
        public final CardStack fullStack;
        public final CardStack subStack;

        public SelectedStackResult(CardStack fullStack, CardStack subStack) {
            this.fullStack = fullStack;
            this.subStack = subStack;
        }
    }
}
