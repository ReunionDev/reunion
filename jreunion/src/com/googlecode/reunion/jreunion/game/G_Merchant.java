package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Merchant extends G_Npc {

	public G_Merchant(int type) {
		super(type);
	}

	/****** Buy items from merchant shop ******/
	public void buyItem(G_Player player, int npcUniqueid, int itemType,
			int tab, int qnt) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		// if(player.getInventory().freeSlots(tab) == false)
		// return;

		G_Npc npc = Server.getInstance().getWorldModule().getNpcManager()
				.getNpc(npcUniqueid);
		G_Item item = new G_Item(itemType);
		int count = 0;

		if (player.getLime() - item.getPrice() < 0) {
			String packetData = "msg Not enough lime.\n";
					client.SendData(packetData);
			return;
		}

		for (int i = 0; i < qnt; i++) {
			if (player.getInventory().freeSlots(tab, item) == false) {
				break;
			}
			item = ItemFactory.createItem(itemType);
			
			if (item != null) {
			player.pickItem(item.getEntityId());
			count++;
			}
			
		}
		if (item != null) {
		player.updateStatus(10, item.getPrice() * npc.getBuyRate() / 100 * -1
				* count, 0);
		}

	}

	/****** Open Merchant Shop ******/
	public void openShop(G_Player player, int uniqueid) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_Npc npc = Server.getInstance().getWorldModule().getNpcManager()
				.getNpc(uniqueid);

		String packetData = "shop_rate " + npc.getBuyRate() + " "
				+ npc.getSellRate() + "\n";
				client.SendData(packetData);

		Iterator<G_Item> itemListIter = npc.itemsListIterator();

		while (itemListIter.hasNext()) {
			G_Item item = itemListIter.next();

			packetData = "shop_item " + item.getType() + "\n";
					client.SendData(packetData);
		}
	}

	/****** Sell items to merchant shop ******/
	public void sellItem(G_Player player, int npcUniqueid) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}
		try {
			G_Item item = player.getInventory().getItemSelected().getItem();
		
			if (item != null) {
			G_Npc npc = Server.getInstance().getWorldModule().getNpcManager()
					.getNpc(npcUniqueid);
				player.updateStatus(10, (item.getPrice() * npc.getSellRate() / 100), 0);
				player.getInventory().setItemSelected(null);
				DatabaseUtils.getInstance().deleteItem(item);
			}
		} catch (Exception e) {
			System.err.println("Item Sell bug");
		}
	}

}