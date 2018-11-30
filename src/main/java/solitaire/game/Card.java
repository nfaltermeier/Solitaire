package solitaire.game;

import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import java.awt.*;
import java.util.Objects;

public class Card implements IDrawable {
	public static int SUIT_SPADE = 1;
	public static int SUIT_CLUB = 2;
	public static int SUIT_HEART = 3;
	public static int SUIT_DIAMOND = 4;
	
    private boolean isFaceUp;
    private int id;
    private int val;
    private int suit;

	public int lastX;
	public int lastY;
	

	public Card(int id){
		this.id = id;
		this.val = id%13;
		this.suit = id/13 + 1;

        this.isFaceUp = false;
	}
	
    @Override
    public void draw(Graphics g, int x, int y) {
	    if(isFaceUp){
	        /*
            Caching card images in the card causes problems because game is created before card images are guaranteed
            to be loaded and may, when the game is saved, cause all the images to be included in the game save
            */
            g.drawImage(ImageLoader.cardTextures[id], x, y, null);
        }else{
	        g.drawImage(ImageLoader.backTexture, x, y, null);
        }

        lastX = x;
	    lastY = y;
    }
	
	
	
	public int getVal(){
		return this.val;
	}
	
	public int getSuit(){
		return this.suit;
	}

	public int getIDNum(){return this.id; }

	public void setFaceDir(boolean isUp){
	    this.isFaceUp = isUp;
    }

	public boolean inBounds(int x, int y){
		Rectangle bounds = new Rectangle(lastX, lastY, ImageLoader.cardTexWidth, ImageLoader.cardTexHeight);
		return bounds.contains(x, y);
	}

    public boolean isSameColor(Card c){
	    boolean b = true;
        switch(this.getSuit()){
            case 1:
            case 2:
                b = (c.getSuit() == 1 || c.getSuit() == 2);
                break;
            case 3:
            case 4:
                b = (c.getSuit() == 3 || c.getSuit() == 4);
                break;
        }
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
