package solitaire.game;

import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import java.awt.*;

public class Card implements IDrawable {
	public static int SUIT_SPADE = 1;
	public static int SUIT_CLUB = 2;
	public static int SUIT_HEART = 3;
	public static int SUIT_DIAMOND = 4;
	
    private boolean isFaceUp;
    private int id;
    private int val;
    private int suit;

	private int lastX;
	private int lastY;
	

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

}
