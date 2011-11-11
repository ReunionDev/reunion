package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Warehouse extends Npc {
	
	public Warehouse(int id) {
		super(id);
		loadFromReference(id);
	}

	/****** Open stash ******/
	public void openStash(Player player) {

		Client client = player.getClient();

		Iterator<StashItem> stashIter = player.getStash().itemListIterator();
		while (stashIter.hasNext()) {
			StashItem stashItem = stashIter.next();	
			client.sendPacket(Type.STASH, player, stashItem);
		
		}
		client.sendPacket(Type.STASH_END);
	}

	/****** Add/Remove items from stash ******/
	public void stashClick(Player player, int pos, int type, int gems, int special) {
		
		Client client = player.getClient();
		
		if(pos == 12){
			client.sendPacket(Type.MSG, "Lime deposit/withdraw not implemented yet.");
			return;
		}
		/*
			if(gems >= 0)
				storeLime(player,pos,gems);
			else
				removeLime(player,pos,gems);
		}*/
		
		HandPosition handPosition = player.getInventory().getHoldingItem();
		StashItem stashItem = null;

		// Withdraw item from stash
		if(handPosition == null){
			stashItem = player.getStash().getItem(pos);
			removeItem(player, stashItem);
		} else { //depositing item stash
			stashItem = new StashItem(new StashPosition(pos), handPosition.getItem());
			storeItem(player,stashItem);
			
		}
		
		
		
		
//		if (pos == 12) {
//			if (gems > player.getLime()) {
//				//client.sendPacket(Type.MSG, "WARNING: Lime cheating detected!");
//				return;
//			}
//			stashItem = stash.getItem(pos);
//			stashItem.getItem().setGemNumber(
//					stashItem.getItem().getGemNumber() + gems / 100);
//			synchronized(player) {
//				player.setLime(player.getLime()-gems);			
//			}
//			
//			DatabaseUtils.getDinamicInstance().saveItem(stashItem.getItem());
//			if (gems >= 0) {
//				client.sendPacket(Type.STASH_TO, player, stashItem);
//			} else {
//				client.sendPacket(Type.STASH_FROM, stashItem);
//			}
//		} else {
//			if (player.getInventory().getHoldingItem() == null) {
//				stashItem = stash.getItem(pos);
//				Item<?> item = Item.load(stashItem.getItem().getItemId());
//				stash.removeItem(stashItem);
//				player.getInventory().setHoldingItem(new HandPosition(item));
//
//				client.sendPacket(Type.STASH_FROM, stashItem);
//			} else {
//				if (stash.checkPosEmpty(pos)) {
//					Item<?> item = player.getInventory().getHoldingItem()
//							.getItem();
//					stashItem = new StashItem(new StashPosition(pos), item);
//					stash.addItem(stashItem);
//					player.getInventory().setHoldingItem(null);
//
//					client.sendPacket(Type.STASH_TO, player, stashItem);
//				} else {
//					stashItem = stash.getItem(pos);
//
//					if (stashItem.getItem().getType().getTypeId() == type
//							&& stashItem.getItem().getGemNumber() == player
//									.getInventory().getHoldingItem().getItem()
//									.getGemNumber()
//							&& stashItem.getItem().getExtraStats() == special) {
//						StashItem newStashItem = new StashItem(new StashPosition(pos), player
//								.getInventory().getHoldingItem().getItem());
//						stash.addItem(newStashItem);
//						player.getInventory().setHoldingItem(null);
//
//						client.sendPacket(Type.STASH_TO, player, stashItem);
//					}
//				}
//			}
//		}

		// S_DatabaseUtils.getInstance().saveStash(client);
	}
	
	public void storeItem(Player player, StashItem stashItem){
		player.getInventory().setHoldingItem(null);
		player.getStash().addItem(stashItem);
		player.getClient().sendPacket(Type.STASH_TO, player, stashItem);
	}
	
	public void removeItem(Player player, StashItem stashItem){
		Item<?> item = stashItem.getItem();
		
		if(item.getEntityId() == -1)
			player.getPosition().getLocalMap().createEntityId(item);
		
		player.getInventory().setHoldingItem(new HandPosition(item));
		player.getStash().removeItem(stashItem);
		player.getClient().sendPacket(Type.STASH_FROM, stashItem);
	}
	
	public boolean storeLime(Player player, int pos, int limeAmmount){
		StashItem stashItem = player.getStash().getItem(pos);
		Item<?> limeItem = null;
		
		if(stashItem == null){
			ItemType limeItemType = new ItemType(1014);
			limeItem = limeItemType.create();
		} else {
			limeItem = stashItem.getItem();
		}
		
		synchronized(player) {
			if((player.getLime()-limeAmmount) >= 0)
				player.setLime(player.getLime()-limeAmmount);
			else {
				Logger.getLogger(Warehouse.class).error("Player "+player+" is trying to remove "+limeAmmount+" lime. " +
						"Lime available "+player.getLime());
				return false;
			}
		}
		
		limeItem.setGemNumber((limeItem.getGemNumber()*100) + limeAmmount);		
		DatabaseUtils.getDinamicInstance().saveItem(limeItem);
		player.getClient().sendPacket(Type.STASH_TO, player, stashItem, (limeItem.getGemNumber()/100));

		return true;
	}
	
	public void removeLime(Player player, int pos, int lime){
		
	}
}