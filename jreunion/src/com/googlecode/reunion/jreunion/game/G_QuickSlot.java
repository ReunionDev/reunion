package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_DatabaseUtils;
import com.googlecode.reunion.jreunion.server.S_Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_QuickSlot {
	private List<G_QuickSlotItem> itemList = new Vector<G_QuickSlotItem>();

	public G_QuickSlot() {

	}

	public void addItem(G_QuickSlotItem qsItem) {
		if (itemList.contains(qsItem)) {
			return;
		}
		itemList.add(qsItem);
	}

	public G_QuickSlotItem getItem(int slot) {
		Iterator<G_QuickSlotItem> iter = getQuickSlotIterator();
		while (iter.hasNext()) {
			G_QuickSlotItem item = iter.next();

			if (item.getSlot() == slot) {
				return item;
			}
		}
		return null;
	}

	public Iterator<G_QuickSlotItem> getQuickSlotIterator() {
		return itemList.iterator();
	}

	/******* Place a item in the quick slot *********/
	public void MoveToQuick(G_Player player, int tab, int itemId, int slot) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_InventoryItem invItem = player.getInventory().getItem(itemId);
		G_QuickSlotItem qsItem = new G_QuickSlotItem(invItem.getItem(), slot);

		player.getInventory().removeItem(invItem);
		addItem(qsItem);
	}

	/****** Add/Remove Quick Slot Items ******/
	public void quickSlot(G_Player player, int slot) {

		if (player.getInventory().getItemSelected() == null) {
			G_QuickSlotItem qsItem = getItem(slot);
			removeItem(qsItem);
			
			G_InventoryItem invItem = new G_InventoryItem(qsItem.getItem(), -1,
					-1, -1);
			
			player.getInventory().setItemSelected(invItem);
			
			
		} else {
			G_InventoryItem invItem = player.getInventory().getItemSelected();
			G_QuickSlotItem newQuickSlotItem = new G_QuickSlotItem(
					invItem.getItem(), slot);
			G_QuickSlotItem oldQuickSlotItem = getItem(slot);
			if (oldQuickSlotItem == null) {
				addItem(newQuickSlotItem);
				player.getInventory().setItemSelected(null);
			} else {
				removeItem(oldQuickSlotItem);
				addItem(newQuickSlotItem);
				invItem = new G_InventoryItem(oldQuickSlotItem.getItem(), 0, 0,
						0);
				player.getInventory().setItemSelected(invItem);
			}
		}
	}

	public void removeItem(G_QuickSlotItem qsItem) {
		while (itemList.contains(qsItem)) {
			itemList.remove(qsItem);
		}
	}

	/****** Use Quick Slot Items ******/
	public void useQuickSlot(G_Player player, int slot) {
		// int newRate = hpRec;
		// for(int i=4; i>0; i--){
		// newHp = newHp + newRate/i;
		// newRate = newRate - newRate/i;
		// }
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_QuickSlotItem qsItem = getItem(slot);

		if (qsItem.getItem().getType() == 175
				|| qsItem.getItem().getType() == 1053) {
			switch (qsItem.getItem().getType()) {
			case 175: {
				S_Server.getInstance().getWorldModule().getWorldCommand()
						.GoToPos(player, 6655, 5224);
				break;
			}
			case 1053: {
				player.getQuest().spawnOfRuin(player, slot);
				G_Mob mob = new G_Mob(324);
				mob.setPosX(client.playerObject.getPosX() + 20);
				mob.setPosY(client.playerObject.getPosY() + 20);
				mob.setPosZ(client.playerObject.getPosZ());
				mob.setRunning(true);
				S_Server.getInstance().getWorldModule().getMobManager()
						.addMob(mob);
				break;
			}
			default: {
			}
			}
		} else {
			G_Potion potion = (G_Potion) qsItem.getItem();

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
		S_DatabaseUtils.getInstance().deleteItem(qsItem.getItem());
	}
}