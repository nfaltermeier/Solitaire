package solitaire.game;

import org.jetbrains.annotations.Nullable;
import solitaire.graphics.IDrawable;

import java.awt.*;
import java.util.*;
import java.io.File;

public class Game implements IDrawable {
    // Represents a complete game with all of its CardStacks and data

    private CardStack[] foundationStacks;
    private CardStack[] mainPiles;
    private CardStack hiddenStock;
    private CardStack displayStock;

    private int mainPileYOffset; //Set this with graphics stuff
    private int displayStockXOffset; //Set this with graphics stuff


    @Nullable
    private File loadedFrom;


    public Game(){
        //Add more here later, for now it always initializes a new game
        initNewGame();
    }


    @Override
    public void draw(Graphics g, int x, int y) {
		
    }

    public void initNewGame(){
        foundationStacks = new CardStack[4];
        mainPiles = new CardStack[7];

        //Creates a list of cards to be distributed into the different piles
        ArrayList<Integer> remainingCards = new ArrayList<>();
        for(int i=1;i<=52;i++){
            remainingCards.add(i);
        }


        for(int i=0;i<foundationStacks.length;i++){
            foundationStacks[i] = new CardStack(CardStack.FLIPTYPE_ALL);
        }

        Random rand = new Random();

        for(int i=0;i<mainPiles.length;i++){
            mainPiles[i] = new CardStack(CardStack.FLIPTYPE_TOP, 0, 15);
            for(int j=0;j<i+1;j++){
                int indexChoice = rand.nextInt(remainingCards.size());
                mainPiles[i].addNewCard(indexChoice);
                remainingCards.remove(indexChoice);
            }
        }

        hiddenStock = new CardStack(CardStack.FLIPTYPE_NONE);
        displayStock = new CardStack(CardStack.FLIPTYPE_ALL, 15, 0);
        while(remainingCards.size() > 0){
            int indexChoice = rand.nextInt(remainingCards.size());
            hiddenStock.addNewCard(indexChoice);
            remainingCards.remove(indexChoice);
        }


    }

}
