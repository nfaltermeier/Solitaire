package solitaire.game;

import solitaire.graphics.IDrawable;
import solitaire.graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Card implements IDrawable {
	public static int SUIT_SPADE = 1;
	public static int SUIT_CLUB = 2;
	public static int SUIT_HEART = 3;
	public static int SUIT_DIAMOND = 4;
	
    private boolean isFaceUp;
    private BufferedImage faceImage;
    private int id;
    private int val;
    private int suit;
	

	public Card(int id){
		this.id = id;
		this.val = id%13;
		this.suit = id/13 + 1;

        this.faceImage = ImageLoader.cardTextures[id];
        this.isFaceUp = false;
	}
	
    @Override
    public void draw(Graphics g, int x, int y) {
	    if(isFaceUp){
	        g.drawImage(this.faceImage, x, y, null);
        }else{
	        g.drawImage(ImageLoader.backTexture, x, y, null);
        }
    }
	
	
	
	public int getVal(){
		return this.val;
	}
	
	public int getSuit(){
		return this.suit;
	}

	public void setFaceDir(boolean isUp){
	    this.isFaceUp = isUp;
    }
	
}
