package solitaire.game;

import org.jetbrains.annotations.Nullable;
import solitaire.graphics.GameDisplay;
import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import javax.swing.*;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Game implements IDrawable {
    // Represents a complete game with all of its CardStacks and data

    private CardStack[] foundationStacks;
    private CardStack[] mainPiles;
    private CardStack hiddenStock;
    private CardStack displayStock;

    public boolean isWon;

    private int mainPileYOffset; // Set this with graphics stuff
    private int displayStockXOffset; // Set this with graphics stuff

    private CardStack highlightedStack;
    private int lastHighlightedStackID;

    @Nullable
    private File loadedFrom;

    public Game() {
        loadedFrom = null;
        // Add more here later, for now it always initializes a new game
        initNewGame();
    }

    public void setLoadedFrom(@Nullable File loadedFrom) {
        this.loadedFrom = loadedFrom;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        hiddenStock.draw(g, x + 20, y + 10);
        displayStock.draw(g, x + 20, y + 185);

        for (int i = 0; i < mainPiles.length; i++) {
            mainPiles[i].draw(g, x + 180 + i * 120, y + 10);
        }

        for (int i = 0; i < foundationStacks.length; i++) {
            foundationStacks[i].draw(g, x + 1060, y + 40 + i * 165);
        }

        if (highlightedStack != null) {
            int xPos = highlightedStack.getCard(0).lastX;
            int yPos = highlightedStack.getCard(0).lastY;
            int width = ImageLoader.cardTexWidth;
            int height = ImageLoader.cardTexHeight
                    + (mainPiles[0].tieredYOffset * (highlightedStack.getCardCount() - 1));

            g.drawImage(ImageLoader.highlightTexture, xPos, yPos, width, height, null, null);
        }

    }

    private void initNewGame() {
        isWon = false;

        highlightedStack = null;
        lastHighlightedStackID = -1;

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
            mainPiles[i] = new CardStack(CardStack.FLIPTYPE_TOP, 0, 40, CardStack.STACKTYPE_MAIN);
            for (int j = 0; j < i + 1; j++) {
                int indexChoice = rand.nextInt(remainingCards.size());
                mainPiles[i].addNewCard(remainingCards.get(indexChoice));
                remainingCards.remove(indexChoice);
            }
        }

        hiddenStock = new CardStack(CardStack.FLIPTYPE_NONE, CardStack.STACKTYPE_HIDDENSTOCK);
        displayStock = new CardStack(CardStack.FLIPTYPE_ALL, 0, 40, CardStack.STACKTYPE_DISPLAYSTOCK);
        while (remainingCards.size() > 0) {
            int indexChoice = rand.nextInt(remainingCards.size());
            hiddenStock.addNewCard(remainingCards.get(indexChoice));
            remainingCards.remove(indexChoice);
        }

    }

    private CardStack getSelectedCardstack(int clickedX, int clickedY) {
        // Check through all main piles, use an individual card version for the display
        // stock pile
        CardStack highlightedStack = null;
        int stackType = getStackType(clickedX, clickedY);

        switch (stackType) {
        case CardStack.STACKTYPE_MAIN:
            int mainStackIndex = getStackID(clickedX, clickedY);
            int minCardID = mainPiles[mainStackIndex].getClickedCardID(clickedX, clickedY);
            if (mainPiles[mainStackIndex].getCard(minCardID).getFaceUp()) {
                highlightedStack = mainPiles[mainStackIndex].getWholeCardStack(minCardID,
                        mainPiles[mainStackIndex].getCardCount() - 1);
                lastHighlightedStackID = mainStackIndex;
            }
            break;
        }

        /*
         * for(int i=0;i<mainPiles.length;i++){ if(mainPiles[i].inBounds(clickedX,
         * clickedY)){ int minCardID = mainPiles[i].getClickedCardID(clickedX,
         * clickedY); highlightedStack = mainPiles[i].getWholeCardStack(minCardID,
         * mainPiles[i].getCardCount()-1); lastHighlightedStackID = i; } }
         * 
         * if(highlightedStack == null){ if(displayStock.inBounds(clickedX, clickedY)){
         * if(displayStock.getCardCount() > 2){
         * if(displayStock.getCard(2).inBounds(clickedX, clickedY)){ highlightedStack =
         * displayStock.getWholeCardStack(2, 2); lastHighlightedStackID = 99; } } } }
         */

        return highlightedStack;
    }

    private int getStackType(int clickedX, int clickedY) {
        for (int i = 0; i < mainPiles.length; i++) {
            if (mainPiles[i].inBounds(clickedX, clickedY)) {
                return CardStack.STACKTYPE_MAIN;
            }
        }

        if (displayStock.inBounds(clickedX, clickedY)) {
            return CardStack.STACKTYPE_DISPLAYSTOCK;
        }

        if (hiddenStock.inBounds(clickedX, clickedY)) {
            return CardStack.STACKTYPE_HIDDENSTOCK;
        }

        for (int i = 0; i < foundationStacks.length; i++) {
            if (foundationStacks[i].inBounds(clickedX, clickedY)) {
                return CardStack.STACKTYPE_FOUNDATION;
            }
        }

        return -1;
    }

    private int getStackID(int clickedX, int clickedY) {
        int id = -1;

        for (int i = 0; i < mainPiles.length; i++) {
            if (mainPiles[i].inBounds(clickedX, clickedY)) {
                id = i;
            }
        }

        return id;
    }

    public void onClick(int x, int y, JPanel gd) {
        if (highlightedStack == null) {
            highlightedStack = getSelectedCardstack(x, y);
        } else {
            int refStackType = getStackType(x, y);

            switch (refStackType) {
            case CardStack.STACKTYPE_MAIN:
                int refStackID = getStackID(x, y);
                internalStackMove(mainPiles[refStackID]);
                break;
            case CardStack.STACKTYPE_DISPLAYSTOCK:
                internalStackMove(displayStock);
                break;
            case CardStack.STACKTYPE_HIDDENSTOCK:
                cycleStock();
                break;
            }

            highlightedStack = null;
        }

        isWon = checkWinConditions();

        gd.repaint();
    }

    public void cycleStock() {

        if (hiddenStock.getCardCount() > 0) {

            // grab bottom card and add it to temp stack while also removing it from the
            // original stack
            CardStack tempStack = hiddenStock.getWholeCardStack(0, 0);
            hiddenStock.deletePartOfStack(tempStack);

            // add tempStack to displayStock
            displayStock.appendStack(tempStack);
            displayStock.solveFlipType(CardStack.FLIPTYPE_ALL);

            // if displayStock is full
            if (displayStock.getCardCount() > 3) {
                // Remove bottom card and put it back into hiddenstock
                CardStack tempStack2 = displayStock.getWholeCardStack(0, 0);
                displayStock.deletePartOfStack(tempStack2);
                hiddenStock.appendStack(tempStack2);

            }

            hiddenStock.solveFlipType(CardStack.FLIPTYPE_NONE);

        }
    }

    private void internalStackMove(CardStack refStack) {
        if (highlightedStack.getCardCount() > 0) {
            if (canMoveStack(highlightedStack, refStack)) {
                refStack.appendStack(highlightedStack);

                if (lastHighlightedStackID == 99) {
                    displayStock.deletePartOfStack(highlightedStack);
                } else {
                    mainPiles[lastHighlightedStackID].deletePartOfStack(highlightedStack);
                }
            }
        }
    }

    private boolean canMoveStack(CardStack movedStack, CardStack placedStack) {
        boolean b = false;

        if (placedStack.stackType == CardStack.STACKTYPE_MAIN) {
            if (placedStack.getCardCount() == 0) {
                Card attemptCard = movedStack.getCard(0);
                if (attemptCard.getVal() == 12 && attemptCard.getFaceUp()) { // If it's a king
                    b = true;
                }
            } else {
                Card destCard = placedStack.getCard(placedStack.getCardCount() - 1);
                Card attemptCard = movedStack.getCard(0);

                if (destCard.getVal() == (attemptCard.getVal() + 1) && attemptCard.getFaceUp()) {
                    if (!destCard.isSameColor(attemptCard)) {
                        b = true;
                    }
                }
            }
        }

        return b;
    }

    private boolean checkWinConditions() {
        for (int i = 0; i < foundationStacks.length; i++) {
            if (foundationStacks[i].getCardCount() != 12) {
                return false;
            }
        }
        return true;
    }

}
