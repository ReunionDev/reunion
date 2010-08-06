package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_DatabaseUtils;
import com.googlecode.reunion.jreunion.server.S_ItemFactory;
import com.googlecode.reunion.jreunion.server.S_Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Warehouse extends G_Npc {

	public G_Warehouse(int id) {
		super(id);
	}

	/****** Open stash ******/
	public void openStash(G_Player player) {

		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		Iterator<G_StashItem> stashIter = player.getStash().itemListIterator();
		while (stashIter.hasNext()) {
			G_StashItem stashItem = stashIter.next();

			String packetData = "stash " + stashItem.getPos() + " "
					+ stashItem.getItem().getType() + " "
					+ stashItem.getItem().getGemNumber() + " "
					+ stashItem.getItem().getExtraStats() + " "
					+ player.getStash().getQuantity(stashItem.getPos()) + "\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
		}
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, "stash_end");
	}

	/****** Add/Remove items from stash ******/
	public void stashClick(G_Player player, int pos, int type, int gems,
			int special) {

		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_Stash stash = player.getStash();
		G_StashItem stashItem;
		String packetData = null;

		if (pos == 12) {
			if (gems > player.getLime()) {
				packetData = "msg WARNING: Lime cheating detected!\n";
				S_Server.getInstance().getNetworkModule()
						.SendPacket(client.networkId, packetData);
				return;
			}
			stashItem = stash.getItem(pos);
			stashItem.getItem().setGemNumber(
					stashItem.getItem().getGemNumber() + gems / 100);
			player.updateStatus(10, gems * -1, 0);
			S_DatabaseUtils.getInstance().updateItemInfo(stashItem.getItem());
			if (gems >= 0) {
				packetData = "stash_to " + stashItem.getPos() + " "
						+ stashItem.getItem().getType() + " "
						+ stashItem.getItem().getGemNumber() + " "
						+ stashItem.getItem().getExtraStats() + "\n";
			} else {
				packetData = "stash_from " + stashItem.getPos() + " "
						+ stashItem.getItem().getType() + " "
						+ stashItem.getItem().getGemNumber() + " "
						+ stashItem.getItem().getExtraStats() + "\n";
			}
		} else {
			if (player.getInventory().getItemSelected() == null) {
				stashItem = stash.getItem(pos);
				G_Item item = S_ItemFactory.loadItem(stashItem.getItem()
						.getEntityId());
				S_DatabaseUtils.getInstance().loadItemInfo(item);
				stash.removeItem(stashItem);
				player.getInventory().setItemSelected(
						new G_InventoryItem(item, 0, 0, 0));

				packetData = "stash_from " + stashItem.getPos() + " "
						+ item.getEntityId() + " "
						+ stashItem.getItem().getType() + " "
						+ stashItem.getItem().getGemNumber() + " "
						+ stashItem.getItem().getExtraStats() + " "
						+ player.getStash().getQuantity(pos) + "\n";
			} else {
				if (stash.checkPosEmpty(pos)) {
					G_Item item = player.getInventory().getItemSelected()
							.getItem();
					stashItem = new G_StashItem(pos, item);
					stash.addItem(stashItem);
					player.getInventory().setItemSelected(null);

					packetData = "stash_to " + stashItem.getPos() + " "
							+ stashItem.getItem().getType() + " "
							+ stashItem.getItem().getGemNumber() + " "
							+ stashItem.getItem().getExtraStats() + " "
							+ player.getStash().getQuantity(pos) + " 0\n";
				} else {
					stashItem = stash.getItem(pos);

					if (stashItem.getItem().getType() == type
							&& stashItem.getItem().getGemNumber() == player
									.getInventory().getItemSelected().getItem()
									.getGemNumber()
							&& stashItem.getItem().getExtraStats() == special) {
						G_StashItem newStashItem = new G_StashItem(pos, player
								.getInventory().getItemSelected().getItem());
						stash.addItem(newStashItem);
						player.getInventory().setItemSelected(null);

						packetData = "stash_to " + stashItem.getPos() + " "
								+ stashItem.getItem().getType() + " "
								+ stashItem.getItem().getGemNumber() + " "
								+ stashItem.getItem().getExtraStats() + " "
								+ stash.getQuantity(pos) + " 0\n";
					}
				}
			}
		}

		// S_DatabaseUtils.getInstance().saveStash(client);
		if (packetData != null) {
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
		}
	}
}