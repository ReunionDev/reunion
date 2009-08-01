package com.googlecode.reunion.jreunion.game;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_QuickSlotItem {
	
	private int slot;
	
	private G_Item item;
	
	public G_QuickSlotItem(G_Item item, int slot) {
		this.item = item;
		this.slot = slot;
	}
	public void setItem(G_Item item){
		this.item = item;
	}
	public G_Item getItem(){
		return item;
	}
	public void setSlot(int slot){
		this.slot = slot;
	}
	public int getSlot(){
		return slot;
	}
}
	