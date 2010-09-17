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
public class Merchant extends Npc {

	public Merchant(int type) {
		super(type);
	}

	/****** Buy items from merchant shop ******/
	public void buyItem(Player player, int npcUniqueid, int itemType,
			int tab, int qnt) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		// if(player.getInventory().freeSlots(tab) == false)
		// return;

		Npc npc = Server.getInstance().getWorldModule().getNpcManager()
				.getNpc(npcUniqueid);
		Item item = new Item(itemType);
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
	public void openShop(Player player, int uniqueid) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		Npc npc = Server.getInstance().getWorldModule().getNpcManager()
				.getNpc(uniqueid);

		String packetData = "shop_rate " + npc.getBuyRate() + " "
				+ npc.getSellRate() + "\n";
				client.SendData(packetData);

		Iterator<Item> itemListIter = npc.itemsListIterator();

		while (itemListIter.hasNext()) {
			Item item = itemListIter.next();

			packetData = "shop_item " + item.getType() + "\n";
					client.SendData(packetData);
		}
	}

	/****** Sell items to merchant shop ******/
	public void sellItem(Player player, int npcUniqueid) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}
		try {
			Item item = player.getInventory().getItemSelected().getItem();
		
			if (item != null) {
			Npc npc = Server.getInstance().getWorldModule().getNpcManager()
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