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

	public G_Item getItem() {
		return item;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setItem(G_Item item) {
		this.item = item;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
}