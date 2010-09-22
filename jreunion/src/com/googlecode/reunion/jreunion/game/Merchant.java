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
	public void buyItem(Player player, int itemType, int tab,
			int quantity) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		// if(player.getInventory().freeSlots(tab) == false)
		// return;

		Item item = ItemFactory.create(itemType);
		
		int count = 0;

		if (player.getLime() - item.getPrice() < 0) {
			String packetData = "msg Not enough lime.\n";
					client.SendData(packetData);
			return;
		}

		for (int i = 0; i < quantity; i++) {
			if (player.getInventory().freeSlots(tab, item) == false) {
				break;
			}
			item = ItemFactory.create(itemType);
			
			if (item != null) {
			player.pickItem(item.getEntityId());
			count++;
			}
			
		}
		if (item != null) {
		player.updateStatus(10, item.getPrice() * this.getBuyRate() / 100 * -1 * count, 0);
		
		
		}

	}

	/****** Open Merchant Shop ******/
	public void openShop(Player player) {

		Client client = player.getClient();


		String packetData = "shop_rate " + this.getBuyRate() + " "
				+ this.getSellRate() + "\n";
				client.SendData(packetData);

		Iterator<VendorItem> itemListIter = this.itemsListIterator();

		while (itemListIter.hasNext()) {
			VendorItem item = itemListIter.next();

			packetData = "shop_item " + item.getType() + "\n";
					client.SendData(packetData);
		}
	}

	/****** Sell items to merchant shop ******/
	public void sellItem(Player player) {
		
		try {
			Item item = player.getInventory().getItemSelected().getItem();
		
			if (item != null) {
				player.updateStatus(10, (item.getPrice() * (this.getSellRate() / 100)), 0);
				player.getInventory().setItemSelected(null);
				DatabaseUtils.getInstance().deleteItem(item);
			}
			
		} catch (Exception e) {
			System.err.println("Item Sell bug");
		}
	}
}