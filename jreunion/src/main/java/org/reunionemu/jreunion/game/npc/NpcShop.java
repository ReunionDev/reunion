package org.reunionemu.jreunion.game.npc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.NpcType;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.VendorItem;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.ItemManager;
import org.reunionemu.jreunion.server.Reference;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NpcShop {

	private String shop;

	private Parser shopReference;

	private List<VendorItem> itemsList = new Vector<VendorItem>();
	
	private int sellRate;

	private int buyRate;
	
	public NpcShop(Npc<?> npc) {
		shopReference = new Parser();
		shop = null;
		loadShop(npc);
	}
	
	public void addItem(VendorItem item) {
		itemsList.add(item);
	}

	public Iterator<VendorItem> itemsListIterator() {
		return itemsList.iterator();
	}
	
	public List<VendorItem> getItemsList(){
		return itemsList;
	}

	public void setBuyRate(int buyRate) {
		this.buyRate = buyRate;
	}

	public void setSellRate(int sellRate) {
		this.sellRate = sellRate;
	}
	

	public int getBuyRate() {
		return buyRate;
	}
	public int getSellRate() {
		return sellRate;
	}
	

	public String getShop() {
		return shop;
	}
	
	public void setShop(String shop) {
		this.shop = shop;
	}
	
	
	public void loadShop(Npc<?> npc) {
		
		ParsedItem parsedNpc = npc.getPosition().getLocalMap().getNpcSpawnReference().getItemById(npc.getSpawn().getId());
				
		setSellRate(Integer.parseInt(parsedNpc.getMemberValue("SellRate")));
		setBuyRate(Integer.parseInt(parsedNpc.getMemberValue("BuyRate")));
		setShop(parsedNpc.getMemberValue("Shop"));
		
		
		shopReference.Parse(Reference.getDataResource(getShop()));
		
		loadItemList(npc);
	}

	public void loadItemList(Npc<?> npc) {

		
		if (shopReference != null) {
	
			Iterator<ParsedItem> iter = shopReference.getItemListIterator();
	
			while (iter.hasNext()) {
	
				ParsedItem i = iter.next();
	
				if (!i.checkMembers(new String[] { "Type" })) {
					LoggerFactory.getLogger(Merchant.class).warn("Failed to load a Npc Shop Item on map: "
							+ npc.getPosition().getLocalMap());
					continue;
				}
				
				VendorItem item = new VendorItem(Integer.parseInt(i.getMemberValue("Type")));
				addItem(item);
			}
		}
	}

	/*
	public void load(){
		itemsList.clear();
		shopReference = new Parser();
	}*/
	

	/****** Buy items from merchant shop ******/
	public boolean buyItem(Player player, int itemTypeId, int tab, int quantity) {

		Client client = player.getClient();
		ItemManager itemManager = player.getClient().getWorld().getItemManager();

		ItemType itemType = itemManager.getItemType(itemTypeId);
		
		if(itemType == null){
			player.getClient().sendPacket(Type.SAY, "Item not implemented!");
			LoggerFactory.getLogger(this.getClass()).error("Item Type "+itemType+" not found.");
			return false;
		}
		
		for (int i = 0; i < quantity; i++) {
			
			Item<?> item = itemManager.create(itemTypeId);
			
			if (player.getInventory().freeSlots(tab, item) == false) {
				DatabaseUtils.getDinamicInstance().deleteItem(item.getItemId());
				return false;
			}
			
			int cost = item.getType().getPrice() * this.getBuyRate() / 100;
				
			if (player.getLime() - cost < 0) {
				client.sendPacket(Type.MSG, "Not enough lime.");
				return false;
			} else {
				synchronized(player) {
					player.setLime(player.getLime() - cost);	
				}
			}

			player.getPosition().getLocalMap().createEntityId(item);
			player.pickItem(item, tab);
		}
		
		return true;
	}

	/****** Open Merchant Shop ******/
	public void openShop(Player player) {

		Client client = player.getClient();

		client.sendPacket(Type.SHOP_RATE, this);
		
		Iterator<VendorItem> itemListIter = this.itemsListIterator();

		while (itemListIter.hasNext()) {
			VendorItem vendorItem = itemListIter.next();
			
			client.sendPacket(Type.SHOP_ITEM, vendorItem);

		}
	}

	/****** Sell items to merchant shop ******/
	public void sellItem(Player player) {
		
		Item<?> item = player.getInventory().getHoldingItem().getItem();
	
		if (item != null) {
			int price = (int) (item.getType().getPrice() * ((double)this.getSellRate() / 100));
			LoggerFactory.getLogger(Merchant.class).info("Player "+player+" sold item "+item+" for "+price+" Lime");
			synchronized(player){
				player.setLime(player.getLime()+price);
			}

			player.getInventory().setHoldingItem(null);
			DatabaseUtils.getDinamicInstance().deleteItem(item.getItemId());
		}
		else{
			LoggerFactory.getLogger(Merchant.class).error("Sell failed, no item selected");			
		}		
	}
}