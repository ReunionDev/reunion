package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class QuickSlotBar {
	private List<QuickSlotItem> itemList = new Vector<QuickSlotItem>();

	private Player player;
	
	public QuickSlotBar(Player player) {
		setPlayer(player);
	}

	public Player getPlayer() {
		return player;
	}

	private void setPlayer(Player player) {
		this.player = player;
	}

	public void addItem(QuickSlotItem qsItem) {
		if (itemList.contains(qsItem)) {
			return;
		}
		itemList.add(qsItem);
	}

	public QuickSlotItem getItem(int slot) {
		Iterator<QuickSlotItem> iter = getQuickSlotIterator();
		while (iter.hasNext()) {
			QuickSlotItem item = iter.next();

			if (item.getPosition().getSlot() == slot) {
				return item;
			}
		}
		return null;
	}

	public Iterator<QuickSlotItem> getQuickSlotIterator() {
		return itemList.iterator();
	}

	/******* Place a item in the quick slot *********/
	public void MoveToQuick(int tab, int itemId, int slot) {

		InventoryItem invItem = player.getInventory().getItem(itemId);
		QuickSlotItem qsItem = new QuickSlotItem(invItem.getItem(), new QuickSlotPosition(this, slot));
		
		player.getInventory().deleteInventoryItem(invItem);
		addItem(qsItem);
	}

	/****** Add/Remove Quick Slot Items ******/
	public void quickSlot(Player player, int slot) {

		if (player.getInventory().getHoldingItem() == null) {
			QuickSlotItem qsItem = getItem(slot);
			removeItem(qsItem);
			
			InventoryItem invItem = new InventoryItem(qsItem.getItem(), new InventoryPosition(-1,-1, -1));
			
			player.getInventory().setHoldingItem(new HandPosition(invItem.getItem()));
			
			
		} else {
			InventoryItem invItem = new InventoryItem(player.getInventory().getHoldingItem().getItem(),
					new InventoryPosition(0,0,0));
			QuickSlotItem newQuickSlotItem = new QuickSlotItem(
					invItem.getItem(), new QuickSlotPosition(this, slot));
			QuickSlotItem oldQuickSlotItem = getItem(slot);
			if (oldQuickSlotItem == null) {
				addItem(newQuickSlotItem);
				player.getInventory().setHoldingItem(null);
			} else {
				removeItem(oldQuickSlotItem);
				addItem(newQuickSlotItem);
				invItem = new InventoryItem(oldQuickSlotItem.getItem(), new InventoryPosition(0, 0,	0));
				player.getInventory().setHoldingItem(new HandPosition(invItem.getItem()));
			}
		}
	}

	public void removeItem(QuickSlotItem qsItem) {
		while (itemList.contains(qsItem)) {
			itemList.remove(qsItem);
		}
	}

	/****** Use Quick Slot Items ******/
	public void useQuickSlot(Player player, int slot) {

		QuickSlotItem qsItem = getItem(slot);
		
		Item<?> item = qsItem.getItem();
		
		Logger.getLogger(QuickSlotBar.class).info(player.getName()+" is using: " +item.getType().getName());
		
		player.getPosition().getLocalMap().getWorld().getCommand().useItem(player, item, slot);
		
		removeItem(qsItem);
		DatabaseUtils.getDinamicInstance().deleteItem(qsItem.getItem().getItemId());
	}
}