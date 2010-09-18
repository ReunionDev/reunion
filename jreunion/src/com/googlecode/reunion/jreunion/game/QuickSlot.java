package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class QuickSlot {
	private List<QuickSlotItem> itemList = new Vector<QuickSlotItem>();

	public QuickSlot() {

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

			if (item.getSlot() == slot) {
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

		if (client == null) {
			return;
		}

		InventoryItem invItem = player.getInventory().getItem(itemId);
		QuickSlotItem qsItem = new QuickSlotItem(invItem.getItem(), slot);

		player.getInventory().removeItem(invItem);
		addItem(qsItem);
	}

	/****** Add/Remove Quick Slot Items ******/
	public void quickSlot(Player player, int slot) {

		if (player.getInventory().getItemSelected() == null) {
			QuickSlotItem qsItem = getItem(slot);
			removeItem(qsItem);
			
			InventoryItem invItem = new InventoryItem(qsItem.getItem(), -1,
					-1, -1);
			
			player.getInventory().setItemSelected(invItem);
			
			
		} else {
			InventoryItem invItem = player.getInventory().getItemSelected();
			QuickSlotItem newQuickSlotItem = new QuickSlotItem(
					invItem.getItem(), slot);
			QuickSlotItem oldQuickSlotItem = getItem(slot);
			if (oldQuickSlotItem == null) {
				addItem(newQuickSlotItem);
				player.getInventory().setItemSelected(null);
			} else {
				removeItem(oldQuickSlotItem);
				addItem(newQuickSlotItem);
				invItem = new InventoryItem(oldQuickSlotItem.getItem(), 0, 0,
						0);
				player.getInventory().setItemSelected(invItem);
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
		// int newRate = hpRec;
		// for(int i=4; i>0; i--){
		// newHp = newHp + newRate/i;
		// newRate = newRate - newRate/i;
		// }
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		QuickSlotItem qsItem = getItem(slot);

		if (qsItem.getItem().getType() == 175
				|| qsItem.getItem().getType() == 1053) {
			switch (qsItem.getItem().getType()) {
			case 175: {
				Server.getInstance().getWorldModule().getWorldCommand()
						.GoToPos(player, 6655, 5224);
				break;
			}
			case 1053: {
				player.getQuest().spawnOfRuin(player, slot);
				Mob mob = new Mob(324);
				mob.getPosition().setX(client.getPlayer().getPosition().getX() + 20);
				mob.getPosition().setY(client.getPlayer().getPosition().getY() + 20);
				mob.getPosition().setZ(client.getPlayer().getPosition().getZ());
				mob.setRunning(true);
				Server.getInstance().getWorldModule().getMobManager()
						.addMob(mob);
				break;
			}
			default: {
			}
			}
		} else {
			Potion potion = (Potion) qsItem.getItem();

			int newRate = 0;
			int newHp = player.getCurrHp();
			int newMana = player.getCurrMana();
			int newStm = player.getCurrStm();
			int newElect = player.getCurrElect();

			if (potion.getHpRec() > 0) {
				newRate = potion.getHpRec();
				for (int i = 4; i > 0; i--) {
					newHp = newHp + newRate / i;
					player.setCurrHp(newHp);
					player.updateStatus(0, newHp, player.getMaxHp());
					newRate = newRate - newRate / i;
				}
			} else if (potion.getManaRec() > 0) {
				newRate = potion.getManaRec();
				for (int i = 4; i > 0; i--) {
					newMana = newMana + newRate / i;
					player.setCurrMana(newMana);
					player.updateStatus(1, newMana, player.getMaxMana());
					newRate = newRate - newRate / i;
				}
			} else if (potion.getStmRec() > 0) {
				newRate = potion.getStmRec();
				for (int i = 4; i > 0; i--) {
					newStm = newStm + newRate / i;
					player.setCurrStm(newStm);
					player.updateStatus(2, newStm, player.getMaxStm());
					newRate = newRate - newRate / i;
				}
			} else if (potion.getElectRec() > 0) {
				newRate = potion.getElectRec();
				for (int i = 4; i > 0; i--) {
					newElect = newElect + newRate / i;
					player.setCurrElect(newElect);
					player.updateStatus(3, newElect, player.getMaxElect());
					newRate = newRate - newRate / i;
				}
			}
		}
		removeItem(qsItem);
		DatabaseUtils.getInstance().deleteItem(qsItem.getItem());
	}
}