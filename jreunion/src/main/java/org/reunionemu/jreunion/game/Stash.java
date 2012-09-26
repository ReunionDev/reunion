package org.reunionemu.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Stash {

	private List<StashItem> itemList = new Vector<StashItem>();

	public Stash(Player player) {
		StashItem lime = new StashItem(new StashPosition(12), Server.getInstance().getWorld().getItemManager().
				getItemType(1014).create());
		addItem(lime);
	}

	public void addItem(StashItem item) {
		if (itemList.contains(item)) {
			return;
		}
		itemList.add(item);
	}

	public boolean checkPosEmpty(int pos) {
		Iterator<StashItem> listIter = itemListIterator();

		while (listIter.hasNext()) {
			StashItem item = listIter.next();

			if (item.getStashPosition().getSlot() == pos) {
				return false;
			}
		}
		return true;
	}

	public void clearStash() {
		itemList.clear();
	}

	public StashItem getItem(int pos) {
		Iterator<StashItem> listIter = itemListIterator();

		while (listIter.hasNext()) {
			StashItem item = listIter.next();

			if (item.getStashPosition().getSlot() == pos) {
				return item;
			}
		}
		return null;
	}

	public int getItemSlot(int stashTab, int itemTypeId) {
		int indexSlot = getTabFirstSlot(stashTab);
		int lastSlot = getTabLastSlot(stashTab);
		int freeSlot = -1;
		
		while(indexSlot < lastSlot){
			if(!checkPosEmpty(indexSlot)){
				StashItem stashItem = getItem(indexSlot);
				if(stashItem.getItem().getType().getTypeId() == itemTypeId)
					return stashItem.getStashPosition().getSlot();		//if exist same type item, return its slot
				
			} else if(freeSlot == -1){
				freeSlot = indexSlot; //store only the first free slot
			}
			indexSlot++;	
		}
		return freeSlot;
	}
	
	public int getTabFirstSlot(int stashTab){
		
		switch (stashTab) {
			case 0: { return 0; }
			case 1: { return 20; }
			case 2: { return 40; }
			default: { return 0; }
		}
	}
	
	public int getTabLastSlot(int stashTab){
		
		switch (stashTab) {
			case 0: { return 11; }
			case 1: { return 31; }
			case 2: { return 51; }
			default: { return 0; }
		}
	}
	
	public int getQuantity(int pos) {
		int count = 0;

		if(pos == 12)
			return 0;
		
		Iterator<StashItem> stashIter = itemListIterator();

		while (stashIter.hasNext()) {
			StashItem stashItem = stashIter.next();

			if (stashItem.getStashPosition().getSlot() == pos) {
				count++;
			}
		}
		return count;
	}

	public Iterator<StashItem> itemListIterator() {
		return itemList.iterator();
	}

	public int listSize() {
		return itemList.size();
	}

	public void removeItem(StashItem item) {
		if (!itemList.contains(item)) {
			return;
		}
		itemList.remove(item);
	}
}