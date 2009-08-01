package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_StashItem {
	
	private int pos;
	
	private G_Item item;
	
	public G_StashItem(int pos, G_Item item) {
		setPos(pos);
		setItem(item);
	}
	
	public void setPos(int pos){
		this.pos = pos;
	}
	public int getPos(){
		return this.pos;
	}
	
	public void setItem(G_Item item){
		this.item = item;
	}
	public G_Item getItem(){
		return this.item;
	}
}