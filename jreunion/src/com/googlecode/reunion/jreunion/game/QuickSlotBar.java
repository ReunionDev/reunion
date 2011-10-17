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
	public void MoveToQuick(Player player, int tab, int itemId, int slot) {
		Client client = player.getClient();

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
			
			player.getInventory().setHoldingItem(invItem);
			
			
		} else {
			InventoryItem invItem = player.getInventory().getHoldingItem();
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
				player.getInventory().setHoldingItem(invItem);
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
		
		Logger.getLogger(QuickSlotBar.class).info("USING: " +item);
		
		player.getPosition().getLocalMap().getWorld().getCommand().useItem(player, item, slot);
/*
		if (qsItem.getItem().getType() == 175
				|| qsItem.getItem().getType() == 1053) {
			switch (qsItem.getItem().getType()) {
			case 175: {
				Position position = player.getPosition().clone();
				position.setX(6655);
				position.setX(5224);
				Server.getInstance().getWorld().getCommand()
						.GoToPos(player, position);
				break;
			}
			case 1053: {
				player.getQuest().spawnOfRuin(player, slot);
				Mob mob = new Mob(324);
				mob.getPosition().setX(client.getPlayer().getPosition().getX() + 20);
				mob.getPosition().setY(client.getPlayer().getPosition().getY() + 20);
				mob.getPosition().setZ(client.getPlayer().getPosition().getZ());
				mob.setIsRunning(true);
				Server.getInstance().getWorld().getMobManager()
						.addMob(mob);
				break;
			}
			default: {
			}
			}
		} else {
			Potion potion = (Potion) qsItem.getItem();

			int newRate = 0;
			int newHp = player.getHp();
			int newMana = player.getMana();
			int newStm = player.getStm();
			int newElect = player.getElect();

			if (potion.getHpRec() > 0) {
				newRate = potion.getHpRec();
				for (int i = 4; i > 0; i--) {
					newHp = newHp + newRate / i;
					player.setHp(newHp);
					player.updateStatus(0, newHp, player.getMaxHp());
					newRate = newRate - newRate / i;
				}
			} else if (potion.getManaRec() > 0) {
				newRate = potion.getManaRec();
				for (int i = 4; i > 0; i--) {
					newMana = newMana + newRate / i;
					player.setMana(newMana);
					player.updateStatus(1, newMana, player.getMaxMana());
					newRate = newRate - newRate / i;
				}
			} else if (potion.getStmRec() > 0) {
				newRate = potion.getStmRec();
				for (int i = 4; i > 0; i--) {
					newStm = newStm + newRate / i;
					player.setStm(newStm);
					player.updateStatus(2, newStm, player.getMaxStm());
					newRate = newRate - newRate / i;
				}
			} else if (potion.getElectRec() > 0) {
				newRate = potion.getElectRec();
				for (int i = 4; i > 0; i--) {
					newElect = newElect + newRate / i;
					player.setElect(newElect);
					player.updateStatus(3, newElect, player.getMaxElect());
					newRate = newRate - newRate / i;
				}
			}
		}
		*/
		
		removeItem(qsItem);
		DatabaseUtils.getDinamicInstance().deleteItem(qsItem.getItem());
	}
}