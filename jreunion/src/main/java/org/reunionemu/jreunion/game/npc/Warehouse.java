package org.reunionemu.jreunion.game.npc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.reunionemu.jreunion.game.HandPosition;
import org.reunionemu.jreunion.game.InventoryItem;
import org.reunionemu.jreunion.game.InventoryPosition;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.game.NpcType;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.StashItem;
import org.reunionemu.jreunion.game.StashPosition;
import org.reunionemu.jreunion.game.items.etc.Lime;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Warehouse extends NpcType {
	
	public Warehouse(int id) {
		super(id);
		//loadFromReference(id);
	}

	/****** Open stash ******/
	public void openStash(Player player) {

		Client client = player.getClient();
		int firstSlot = player.getStash().getTabFirstSlot(0);
		int lastSlot = player.getStash().getTabLastSlot(2);
		for(int pos=firstSlot; pos<=lastSlot; pos++){
			if(!player.getStash().checkPosEmpty(pos)){
				StashItem stashItem = player.getStash().getItem(pos);	
				client.sendPacket(Type.STASH, stashItem, player.getStash().getQuantity(pos));
			}
		}
		
		/*
		Iterator<StashItem> stashIter = player.getStash().itemListIterator();
		while (stashIter.hasNext()) {
			StashItem stashItem = stashIter.next();	
			int slot = stashItem.getStashPosition().getSlot();
			client.sendPacket(Type.STASH, stashItem, player.getStash().getQuantity(slot));
		
		}
		*/
		client.sendPacket(Type.STASH_END);
	}

	/****** Add/Remove single items to/from stash ******/
	public void stashClick(Player player, int pos, int type, int gems, int special) {
		
		
		if(pos == 12){
			if(gems >= 0)
				storeLime(player, pos, gems);
			else
				removeLime(player, pos, gems);
			return;
		}
		
		HandPosition handPosition = player.getInventory().getHoldingItem();
		StashItem stashItem = null;

		// Withdraw item from stash
		if(handPosition == null){
			stashItem = player.getStash().getItem(pos);
			player.getInventory().setHoldingItem(new HandPosition(removeItem(player, stashItem)));
			LoggerFactory.getLogger(Warehouse.class).info("Player "+player+" removed item "+stashItem.getItem()+" from the warehouse");
			player.getClient().sendPacket(Type.STASH_FROM, stashItem, player.getStash().getQuantity(pos));
		} else { //store item on stash
			stashItem = new StashItem(new StashPosition(pos), handPosition.getItem());
			storeItem(player,stashItem);	
			LoggerFactory.getLogger(Warehouse.class).info("Player "+player+" stored item "+stashItem.getItem()+" in the warehouse");
		}
	}
	
	/****** Add multiple items to stash ******/
	public void stashPut(Player player, int[] packetData) {
		int index = 0;
		int itemTypeId = packetData[index++];
		int invTab = packetData[index++];
		int stashTab = packetData[index++];
		int position = player.getStash().getItemSlot(stashTab, itemTypeId);
		
		if(position == -1){
			player.getClient().sendPacket(Type.SAY, "No free slots available on this Tab.");
			return;
		}
		
		StashPosition stashPosition = new StashPosition(position);
		StashItem stashItem = null;
		
		while(index < packetData.length -1){
			int posX = packetData[index++];
			int posY = packetData[index++];
			
			Item<?> item = player.getInventory().getItem(invTab, posX, posY).getItem();
			stashItem = new StashItem(stashPosition, item);
			player.getStash().addItem(stashItem);
			player.getInventory().deleteInventoryItem(player.getInventory().getItem(invTab, posX, posY));
		}
		LoggerFactory.getLogger(Warehouse.class).info(
				"Player " + player + " stored " + (packetData.length - 3) / 2
						+ " item(s) " + stashItem.getItem()	+ " in the warehouse slot " + (stashPosition.getSlot()+1));
		player.getClient().sendPacket(Type.STASH_PUT, itemTypeId, invTab,
				stashTab, stashPosition.getSlot(), (packetData.length - 3) / 2, packetData);
	}
	
	/****** Remove multiple items from stash ******/
	public void stashGet(Player player, int type, int inventoryTab, int unknown1, int pos) {
		List<int[]> itemList = new Vector<int[]>();
		int itemQuantity = player.getStash().getQuantity(pos);
		StashItem stashItem = null;
				
		itemQuantity = itemQuantity <= 10 ? itemQuantity : 10;
		
		while(itemQuantity-- > 0){
			stashItem = player.getStash().getItem(pos);
			Item<?> item = removeItem(player,stashItem);
			int[] itemData = new int[3]; 
			
			itemData = player.getInventory().getFreeSlots(item, inventoryTab); //get item inventory position
			player.getInventory().addInventoryItem(
					new InventoryItem(item, 
							new InventoryPosition(itemData[1], itemData[2], itemData[0])));
			itemData[0] = item.getEntityId(); //store item entity Id
			itemList.add(itemData);
		}
		LoggerFactory.getLogger(Warehouse.class).info("Player " + player + " removed " + itemList.size()
						+ " item(s) " + stashItem.getItem()	+ " from the warehouse slot " + (pos+1));
		player.getClient().sendPacket(Type.STASH_GET, itemList, type, inventoryTab, unknown1, pos, itemList.size());
		
	}
	
	public void storeItem(Player player, StashItem stashItem){
		int slot = stashItem.getStashPosition().getSlot();
		player.getInventory().setHoldingItem(null);
		player.getStash().addItem(stashItem);
		player.getClient().sendPacket(Type.STASH_TO, stashItem, player.getStash().getQuantity(slot));
		//LoggerFactory.getLogger(Warehouse.class).info("Player "+player+" stored item "+stashItem.getItem()+" in the warehouse");
	}
	
	public Item<?> removeItem(Player player, StashItem stashItem){
		if(stashItem == null) {
			return null;
		}
		Item<?> item = stashItem.getItem();
		int slot = stashItem.getStashPosition().getSlot();
		
		if(item.getEntityId() == -1 && slot != 12)
			player.getPosition().getLocalMap().createEntityId(item);
		
		//player.getInventory().setHoldingItem(new HandPosition(item));
		player.getStash().removeItem(stashItem);
		//LoggerFactory.getLogger(Warehouse.class).info("Player "+player+" removed item "+item+" from the warehouse");
		return item;
	}
	
	//storing lime on the warehouse
	public boolean storeLime(Player player, int pos, long limeAmount){
		StashItem stashItem = player.getStash().getItem(pos);
		Item<?> limeItem = null;
		
		if(stashItem == null){
			limeItem = player.getClient().getWorld().getItemManager().create(1014);
			stashItem = new StashItem(new StashPosition(12), limeItem);
			player.getStash().addItem(stashItem);
		} else {
			limeItem = stashItem.getItem();
		} 
		
		long limeLimit = Server.getInstance().getWorld().getServerSetings().getWarehouseLimeLimit();
		
		//check if warehouse lime limit is reached.
		if((limeItem.getGemNumber() + limeAmount) > limeLimit){
			long limeOverLimit = limeItem.getGemNumber() + limeAmount - limeLimit;
			limeAmount = limeAmount - limeOverLimit;
			player.getClient().sendPacket(Type.MSG, "Inventory maximum lime limit reached.");
		}
		
		//synchronized(player) {
		if ((player.getLime() - limeAmount) >= 0)
			player.setLime(player.getLime() - limeAmount);
		else {
			LoggerFactory.getLogger(Warehouse.class).warn(
					"Player " + player + " is trying to remove " + limeAmount
							+ " lime from character. " + "Lime available " + player.getLime());
			return false;
		}
		//}
		
		limeItem.setGemNumber((limeItem.getGemNumber()) + limeAmount);		
		DatabaseUtils.getDinamicInstance().saveItem(limeItem);
		player.getClient().sendPacket(Type.STASH_TO, stashItem, player.getStash().getQuantity(pos));

		return true;
	}
	
	public boolean removeLime(Player player, int pos, int limeAmmount){
		StashItem stashItem = player.getStash().getItem(pos);
		Item<?> limeItem = null;
		
		if(stashItem == null){
			LoggerFactory.getLogger(Warehouse.class).warn(
					"Player " + player + " is trying to remove " + limeAmmount
							+ " lime from Warehouse. " + "But there is no lim available.");
			return false;
		} else {
			limeItem = stashItem.getItem();
		}
		
		//synchronized(player) {
		if ((limeItem.getGemNumber() + limeAmmount) >= 0)
			limeItem.setGemNumber(limeItem.getGemNumber() + limeAmmount);
		else {
			LoggerFactory.getLogger(Warehouse.class).warn(
					"Player " + player + " is trying to remove " + limeAmmount
							+ " lime from Warehouse. " + "Lime available " + player.getLime());
			return false;
		}
		//}
				
		player.setLime(player.getLime() - limeAmmount);
		DatabaseUtils.getDinamicInstance().saveItem(limeItem);
		player.getClient().sendPacket(Type.STASH_FROM, stashItem, player.getStash().getQuantity(pos));

		return true;
	}
}