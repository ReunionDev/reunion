package com.googlecode.reunion.jreunion.game;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_ExchangeItem {
	
	private G_Item item;
	
	private int posX;
	
	private int posY;
	
	public G_ExchangeItem(G_Item item, int posX, int posY) {
		setItem(item);
		setPosX(posX);
		setPosY(posY);
	}
	
	public void setItem(G_Item item){
		this.item = item;
	}
	public G_Item getItem(){
		return this.item;
	}
	
	public void setPosX(int posX){
		this.posX = posX;
	}
	public int getPosX(){
		return this.posX;
	}
	
	public void setPosY(int posY){
		this.posY = posY;
	}
	public int getPosY(){
		return this.posY;
	}
}