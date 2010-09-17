package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ExchangeItem {

	private Item item;

	private int posX;

	private int posY;

	public ExchangeItem(Item item, int posX, int posY) {
		setItem(item);
		setPosX(posX);
		setPosY(posY);
	}

	public Item getItem() {
		return item;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
}