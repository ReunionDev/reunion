package org.reunionemu.jreunion.game;

import java.util.*;

import org.reunionemu.jreunion.model.jpa.InventoryItemImpl;
import org.reunionemu.jreunion.server.*;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
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
	public void MovingItem(int quickSlotBarPosition, int unknown, int itemEntityId) {

		InventoryItem invItem = player.getInventory().getItem(itemEntityId);
		QuickSlotItem qsItem = new QuickSlotItem(invItem.getItem(), new QuickSlotPosition(this, quickSlotBarPosition));
		
		player.getInventory().deleteInventoryItem(invItem);
		addItem(qsItem);
		player.getClient().sendPacket(Type.MT_ITEM, 1, quickSlotBarPosition, itemEntityId, 0);
	}
	
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
			
			InventoryItem invItem = new InventoryItemImpl(qsItem.getItem(), new InventoryPosition(-1,-1, -1), player);
			
			player.getInventory().setHoldingItem(new HandPosition(invItem.getItem()));
			
			
		} else {
			InventoryItem invItem = new InventoryItemImpl(player.getInventory().getHoldingItem().getItem(),
					new InventoryPosition(0,0,0), player);
			QuickSlotItem newQuickSlotItem = new QuickSlotItem(
					invItem.getItem(), new QuickSlotPosition(this, slot));
			QuickSlotItem oldQuickSlotItem = getItem(slot);
			if (oldQuickSlotItem == null) {
				addItem(newQuickSlotItem);
				player.getInventory().setHoldingItem(null);
			} else {
				removeItem(oldQuickSlotItem);
				addItem(newQuickSlotItem);
				invItem = new InventoryItemImpl(oldQuickSlotItem.getItem(), new InventoryPosition(0, 0,	0), player);
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
	public void useQuickSlot(Player player, int quickSlotBarPosition, int unknown, int itemEntityId) {

		int n;
		QuickSlotItem qsItem = getItem(quickSlotBarPosition);
		
		Item<?> item = qsItem.getItem();
		
		player.getPosition().getLocalMap().getWorld().getCommand().useItem(player, item, quickSlotBarPosition, unknown);
		
		LoggerFactory.getLogger(QuickSlotBar.class).info(player.getName()+" used item: " +item.getType().getName());
		
		removeItem(qsItem);
		Database.getInstance().deleteQuickSlotItem(item);
		item.delete();
		
	}
	
	public void useQuickSlot(Player player, int slot) {

		QuickSlotItem qsItem = getItem(slot);
		
		if(qsItem==null)
			return;
		
		Item<?> item = qsItem.getItem();
		
		player.getPosition().getLocalMap().getWorld().getCommand().useItem(player, item, slot);
		
		LoggerFactory.getLogger(QuickSlotBar.class).info(player.getName()+" used item: " +item.getType().getName());
		
		// Dont remove Rough Gems - we take care of them in the cutting routine.
		
		Integer itemtype = item.getType().getTypeId();
		
		if ( !(itemtype >= 215 && itemtype <= 221) && !(itemtype == 1067)) {
			removeItem(qsItem);
			Database.getInstance().deleteQuickSlotItem(item);
			item.delete();
		} 		
		
	}
}