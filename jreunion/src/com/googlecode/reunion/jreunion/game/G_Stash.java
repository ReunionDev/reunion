package com.googlecode.reunion.jreunion.game;

import java.util.*;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Stash {
	
	private List<G_StashItem> itemList;
		
	public G_Stash() {
		itemList = new Vector<G_StashItem>();
	}
	
	public void addItem(G_StashItem item){
		if(itemList.contains(item))
			return;
		itemList.add(item);
	}
	
	public void removeItem(G_StashItem item){
		if(!itemList.contains(item))
			return;
		itemList.remove(item);
	}
	
	public boolean checkPosEmpty(int pos){
		Iterator<G_StashItem> listIter = itemListIterator();
		
		while(listIter.hasNext()){
			G_StashItem item = listIter.next();
			
			if(item.getPos() == pos)
				return false;
		}
		return true;
	}
	
	public G_StashItem getItem(int pos){
		Iterator<G_StashItem> listIter = itemListIterator();
		
		while(listIter.hasNext()){
			G_StashItem item = listIter.next();
			
			if(item.getPos() == pos)
				return item;
		}
		return null;
	}
	
	public void clearStash(){
		itemList.clear();
	}
	
	public int listSize(){
		return itemList.size();
	}
	
	public int getQuantity(int pos){
		int count = 0;
		
		Iterator<G_StashItem> stashIter = itemListIterator();
		
		while(stashIter.hasNext()){
			G_StashItem stashItem = stashIter.next();
			
			if(stashItem.getPos() == pos)
				count++;
		}
		return count;
	}
	
	public Iterator<G_StashItem> itemListIterator(){
		return itemList.iterator();
	}
}