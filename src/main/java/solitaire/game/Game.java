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

public class Game implements IDrawable {
    // Represents a complete game with all of its CardStacks and data

    private CardStack[] foundationStacks;
    private CardStack[] mainPiles;
    private CardStack hiddenStock;
    private CardStack displayStock;

    private int mainPileYOffset; //Set this with graphics stuff
    private int displayStockXOffset; //Set this with graphics stuff

    private CardStack highlightedStack;
    private int lastHighlightedStackID;


    @Nullable
    private File loadedFrom;


    public Game() {
        loadedFrom = null;
        //Add more here later, for now it always initializes a new game
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

        if(highlightedStack != null){
            for(int i=0;i<highlightedStack.getCardCount();i++){
                g.drawImage(ImageLoader.highlightTexture, highlightedStack.getCard(i).lastX,
                        highlightedStack.getCard(i).lastY, ImageLoader.cardTexWidth,
                        ImageLoader.cardTexHeight, null, null);
            }
        }

    }

    public void initNewGame() {
        highlightedStack = null;
        lastHighlightedStackID = -1;

        foundationStacks = new CardStack[4];
        mainPiles = new CardStack[7];

        //Creates a list of cards to be distributed into the different piles
        ArrayList<Integer> remainingCards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            remainingCards.add(i);
        }


        for (int i = 0; i < foundationStacks.length; i++) {
            foundationStacks[i] = new CardStack(CardStack.FLIPTYPE_ALL);
        }

        Random rand = new Random();

        for (int i = 0; i < mainPiles.length; i++) {
            mainPiles[i] = new CardStack(CardStack.FLIPTYPE_TOP, 0, 40);
            for (int j = 0; j < i + 1; j++) {
                int indexChoice = rand.nextInt(remainingCards.size());
                mainPiles[i].addNewCard(remainingCards.get(indexChoice));
                remainingCards.remove(indexChoice);
            }
        }

        hiddenStock = new CardStack(CardStack.FLIPTYPE_NONE);
        displayStock = new CardStack(CardStack.FLIPTYPE_ALL, 15, 0);
        while (remainingCards.size() > 0) {
            int indexChoice = rand.nextInt(remainingCards.size());
            hiddenStock.addNewCard(remainingCards.get(indexChoice));
            remainingCards.remove(indexChoice);
        }


    }

    public CardStack getSelectedCardstack(int clickedX, int clickedY){
        //Check through all main piles, use an individual card version for the display stock pile
        CardStack highlightedStack = null;

        for(int i=0;i<mainPiles.length;i++){
            if(mainPiles[i].inBounds(clickedX, clickedY)){
                int minCardID = mainPiles[i].getClickedCardID(clickedX, clickedY);
                highlightedStack = mainPiles[i].getWholeCardStack(minCardID, mainPiles[i].getCardCount()-1);
                lastHighlightedStackID = i;
            }
        }

        if(highlightedStack == null){
            if(displayStock.inBounds(clickedX, clickedY)){
                if(displayStock.getCardCount() > 2){
                    if(displayStock.getCard(2).inBounds(clickedX, clickedY)){
                        highlightedStack = displayStock.getWholeCardStack(2, 2);
                        lastHighlightedStackID = 99;
                    }
                }
            }
        }


        return highlightedStack;
    }

    private int getStackID(int clickedX, int clickedY){
        int id = -1;

        for(int i=0;i<mainPiles.length;i++){
            if(mainPiles[i].inBounds(clickedX, clickedY)){
                id = i;
            }
        }

        if(id == -1){
            if(displayStock.inBounds(clickedX, clickedY)){
                id = 99;
            }
        }

        return id;
    }

    public void onClick(int x, int y, JPanel gd){
        if(highlightedStack == null){
            highlightedStack = getSelectedCardstack(x, y);
        }else{
            CardStack refStack = null;
            int refStackID = getStackID(x, y);
            if(refStackID == 99){
                internalStackMove(displayStock);
            }else{
                if(refStackID != -1){
                    internalStackMove(mainPiles[refStackID]);
                }
            }

        }

        gd.repaint();
    }

    private void internalStackMove(CardStack refStack){
        if(highlightedStack.getCardCount() > 0 && refStack.getCardCount() > 0){
            if(canMoveStack(highlightedStack, refStack)){
                refStack.appendStack(highlightedStack);

                if(lastHighlightedStackID == 99){
                    displayStock.deletePartOfStack(highlightedStack);
                }else{
                    mainPiles[lastHighlightedStackID].deletePartOfStack(highlightedStack);
                }

                highlightedStack = null;
            }else{
                highlightedStack = null;
            }
        }
    }

    public boolean canMoveStack(CardStack movedStack, CardStack placedStack){
        boolean b = false;

        Card destCard = placedStack.getCard(placedStack.getCardCount()-1);
        Card attemptCard = movedStack.getCard(0);

        if(destCard.getVal() == (attemptCard.getVal()+1)){
            int suitCombo = destCard.getSuit() + attemptCard.getSuit();
            /*if(suitCombo != 3 && suitCombo != 7  && (destCard.getSuit() != attemptCard.getSuit())){
                b = true;
            }else{
                b = false;
            }*/

            b = true; //Disabling suit checking for testing purposes
        }

        return b;
    }

}
