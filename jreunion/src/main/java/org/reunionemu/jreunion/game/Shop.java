package org.reunionemu.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.server.Session;
import org.reunionemu.jreunion.server.SessionList;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Shop {

	private Player Owner;
	
	private List<ShopItem> itemList = new Vector<ShopItem>();
	
	private long lime;
	
	private String description;
	
	private int tabs;
	
	//list of players buying from this shop
	private List<Shop> playersBuying = new Vector<Shop>();

	public Shop() {
		
	}
	
	public Shop(Player owner, int tabs) {
		setOwner(owner);
		setTabs(tabs);
	}

	public Player getOwner() {
		return Owner;
	}

	public void setOwner(Player owner) {
		Owner = owner;
	}
	
	public void addItem(ShopItem item){
		if (itemList.contains(item)) {
			return;
		}
		itemList.add(item);
	}
	
	public List<ShopItem> getItemList() {
		return itemList;
	}

	public int listSize() {
		return itemList.size();
	}

	public void removeItem(ShopItem item) {
		if (!itemList.contains(item)) {
			return;
		}
		itemList.remove(item);
	}
	
	public long getLime() {
		return lime;
	}

	public void setLime(long lime) {
		this.lime = lime;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getTabs() {
		return tabs;
	}

	public void setTabs(int tabs) {
		this.tabs = tabs;
	}

	public List<Shop> getPlayersBuying() {
		return playersBuying;
		//return new Vector<Shop>(playersBuying);
	}

	public void addPlayerBuying(Shop shop){
		if(!getPlayersBuying().contains(shop)){
			getPlayersBuying().add(shop);
		}
	}
	
	public void removePlayerBuying(Shop shop){
		if(getPlayersBuying().contains(shop)){
			getPlayersBuying().remove(shop);
		}
	}
	
	public int getPlayersBuyingSize(){
		return getPlayersBuying().size();
	}
	
	public int getItemAmount(int position){
		int count = 0;
		
		for(ShopItem item : itemList){
			if(item.getShopPosition().getSlot() == position)
				count++;
		}
		return count;
	}
	
	public ShopItem getItem(ShopPosition position){
		for(ShopItem item : itemList){
			if(item.getShopPosition().getSlot() == position.getSlot())
				return item;
		}
		return null;
	}
	
	public ShopItem getItem(int itemTypeId){
		for(ShopItem item : itemList){
			if(item.getItem().getType().getTypeId() == itemTypeId)
				return item;
		}
		return null;
	}
	
	public ShopItem getIdenticalItem(int itemTypeId, long price, int goldBars, int silverBars, int bronzeBars){
		for(ShopItem item : itemList){
			if(item.getItem().getType().getTypeId() == itemTypeId &&
					item.getPrice() == price &&
					item.getGoldBars() == goldBars &&
					item.getSilverBars() == silverBars &&
					item.getBronzeBars() == bronzeBars &&
					getItemAmount(item.getShopPosition().getSlot()) < 100) {
						return item;
			}
		}
		return null;
	}
	
	public int getFreePosition(){
		for(int pos=0; pos<36; pos++){
			if(getItem(new ShopPosition(pos)) == null)
				return pos;
		}
		return -1;
	}
	
	public void close(){
		
		for(Shop shop : getPlayersBuying()){
			shop.getOwner().getClient().sendPacket(Type.MSG, getOwner().getName()+" closed shop.");
			shop.close();
		}
		//getPlayersBuying().clear();
		
		if(listSize() > 0){
			unRegAll();
		}
		
		getOwner().setLime(getOwner().getLime() + getLime());
		getOwner().getPosition().getLocalMap().removeShop(this);
		getOwner().setShop(null);
		
		getOwner().getClient().sendPacket(Type.U_SHOP, "close");
		getOwner().getClient().sendPacket(Type.U_SHOP, "out", getOwner());
		
		SessionList<Session> sessionList = getOwner().getInterested().getSessions();
		sessionList.sendPacket(Type.U_SHOP, "out", getOwner());
		LoggerFactory.getLogger(this.getClass()).info(getOwner().getName()+" personal shop closed.");
	}
	
	public void regItem(int position, int invTab, int goldBars, int silverBars, int bronzeBars, long price, int invPosX, int invPosY){
		ShopItem shopItem = null;
		List<ShopItem> newShopItems = new Vector<ShopItem>();
		//ShopPosition shopPosition = new ShopPosition(position);
		int[] invItemsPosition; //[tab][x1][y1][x2][y2]...[x10][y10]
		
		if(invTab == -1){ //add single item to shop
			shopItem = getItem(new ShopPosition(position));
			//if the requested position already contains a item, and the new item
			//is different from the existing item, then return error message.
			if(shopItem != null && shopItem.getPrice() != price){
				getOwner().getClient().sendPacket(Type.U_SHOP, "reg", 0, -4, 1);
				return;
			}
			HandPosition holdingItem = getOwner().getInventory().getHoldingItem();
			if(holdingItem==null){
				return;
			} else {
				Item<?> item = holdingItem.getItem();
				shopItem = new ShopItem(new ShopPosition(position), goldBars, silverBars, bronzeBars, price, item);
				newShopItems.add(shopItem);
				getOwner().getInventory().setHoldingItem(null);
				invItemsPosition = new int[3];
				invItemsPosition[0] = -1;
				invItemsPosition[1] = -1;
				invItemsPosition[2] = -1;
			}
			
		} else { //add multiple items to shop
			InventoryItem invItem = getOwner().getInventory().getItem(invTab, invPosX, invPosY);
			shopItem = getIdenticalItem(invItem.getItem().getType().getTypeId(), price, goldBars, silverBars, bronzeBars);
			
			//if already exist a shop position with the same item requested, select it,
			//else get the first free position.
			ShopPosition shopPosition = (shopItem==null ?  new ShopPosition(getFreePosition()) : shopItem.getShopPosition());
			position = shopPosition.getSlot();			
			
			//if adding the max multiple amount (10) is above the position limit amount (100),
			//then calculate the available position amount, else set it to the max amount(10).
			int itemAmount = ((getItemAmount(shopPosition.getSlot())+10) > 100 ?
					100-getItemAmount(shopPosition.getSlot()) : 9);
			
			List<InventoryItem> invItemList = new Vector<InventoryItem>();	
			invItemList.add(invItem);
			int itemTypeId = invItem.getItem().getType().getTypeId();
			getOwner().getInventory().deleteInventoryItem(invItem);

			//get/remove all inventory items available, equal to the requested item type.
			do {
				invItem = getOwner().getInventory().getItem(invTab, itemTypeId);
				if(invItem != null) {
					invItemList.add(invItem);
					getOwner().getInventory().deleteInventoryItem(invItem);
				}
			} while(invItem != null && itemAmount-- > 1);
			
			invItemsPosition = new int[(invItemList.size()*2)+1];
			invItemsPosition[0] = invTab;
			int index = 1;
			
			//get/store inventory items positions and create/add shop items. 
			for(InventoryItem item : invItemList){
				shopItem = new ShopItem(new ShopPosition(position), goldBars, silverBars, bronzeBars, price, item.getItem());
				newShopItems.add(shopItem);
				invItemsPosition[index++] = item.getPosition().getPosX();
				invItemsPosition[index++] = item.getPosition().getPosY();
			}
		}
		
		for(ShopItem newShopItem : newShopItems){
			addItem(newShopItem);
			LoggerFactory.getLogger(this.getClass()).info("Player "+getOwner()+" added item "+newShopItem.getItem()+" to shop.");
		}
		
		getOwner().getClient().sendPacket(Type.U_SHOP, "reg", 1, position, newShopItems.size(), shopItem, invItemsPosition );
		
	}
	
	public void unRegItem(int position, int amountRequested){
		ShopItem shopItem = getItem(new ShopPosition(position));
		
		if(shopItem == null)
			return;
		
		if(amountRequested == 1){ //removing a single items from shop.
			removeItem(shopItem);
			getOwner().getInventory().setHoldingItem(new HandPosition(shopItem.getItem()));
			getOwner().getClient().sendPacket(Type.U_SHOP, "unreg", 1, position, amountRequested);
			getOwner().getClient().sendPacket(Type.PICK_EXTRA, shopItem.getItem());
			LoggerFactory.getLogger(this.getClass()).info("Player "+getOwner()+" removed item "+shopItem.getItem()+" from shop.");
		} else if(amountRequested > 1) { //removing multiple items from shop.
			int existingAmount = getItemAmount(position);
			getOwner().getClient().sendPacket(Type.U_SHOP, "unreg", 1, position, existingAmount);
			do {
				InventoryItem invItem = getOwner().getInventory().storeItem(shopItem.getItem(), -1);
				getOwner().getClient().sendPacket(Type.INVEN, invItem, getOwner().getClient().getVersion());
				removeItem(shopItem);
				shopItem = getItem(new ShopPosition(position));
			} while(existingAmount-- > 1);
		}
	}
	
	public void unRegAll(){
		
		//List<Integer> processedPositions = new Vector<Integer>();
		
		for(ShopItem shopItem : itemList){
			//TODO: process more then one item at the same time.
			/*
			int position = shopItem.getShopPosition().getSlot();
			if(processedPositions.contains(position))
				continue;
			processedPositions.add(position);
			int amount = getItemAmount(position);
			*/
			int amount = 1;
			getOwner().getClient().sendPacket(Type.U_SHOP, "unreg", 1, shopItem.getShopPosition().getSlot(), amount);
			
			//int[] freePosition = getOwner().getInventory().getFreeSlots(shopItem.getItem(), -1);
			//InventoryPosition inventoryPosition = new InventoryPosition(freePosition[1],freePosition[2],freePosition[0]);
			//InventoryItem inventoryItem = new InventoryItem(shopItem.getItem(),	inventoryPosition);
			//getOwner().getInventory().addInventoryItem(inventoryItem);
			InventoryItem inventoryItem = getOwner().getInventory().storeItem(shopItem.getItem(), -1);
			getOwner().getClient().sendPacket(Type.INVEN, inventoryItem, getOwner().getClient().getVersion());
		}
		itemList.clear();
	}
	
	public void start(String[] args){
		String shopDescription = "";
		
		for(int i=2; i<args.length; i++){
			shopDescription += args[i];
			if(i < args.length-1)
				shopDescription += " ";
		}
		
		setDescription(shopDescription);
		SessionList<Session> sessions = getOwner().getInterested().getSessions();
		sessions.sendPacket(Type.U_SHOP, "in", getOwner(), shopDescription);
		getOwner().getClient().sendPacket(Type.U_SHOP, "start");
	}
	
	public void modify(){
		getOwner().getClient().sendPacket(Type.U_SHOP, "out", getOwner());
		SessionList<Session> sessionList = getOwner().getInterested().getSessions();
		sessionList.sendPacket(Type.U_SHOP, "out", getOwner());
		
		getOwner().getClient().sendPacket(Type.U_SHOP, "modify");
	}
	
	public void open(Player buyer){
		buyer.setShop(new Shop(buyer, getTabs()));
		buyer.getClient().sendPacket(Type.U_SHOP, "open", getOwner(), getTabs()-1);
		buyer.getClient().sendPacket(Type.U_SHOP, "list_start");
		for(ShopItem shopItem : getItemList()){
			buyer.getClient().sendPacket(Type.U_SHOP, "list", shopItem, getItemAmount(shopItem.getShopPosition().getSlot()));
		}
		buyer.getClient().sendPacket(Type.U_SHOP, "list_end");
		addPlayerBuying(buyer.getShop());
	}

	public void buy(int position, int requestedAmount){
		Shop sellerShop = getOwner().getPosition().getLocalMap().getShopBuying(getOwner());
		ShopItem shopItem = sellerShop.getItem(new ShopPosition(position));
		
		int availableAmount = sellerShop.getItemAmount(position);
		
		if(getOwner().getLime() - (shopItem.getPrice()*availableAmount) < 0){
			getOwner().getClient().sendPacket(Type.MSG, "You don't have enough lime to buy this item.");
			return;
		}
		
		if(shopItem.getGoldBars()>0 || shopItem.getSilverBars()>0 || shopItem.getBronzeBars()>0){
			getOwner().getClient().sendPacket(Type.SAY, "The amount of bars (gold, silver, bronze) will be ignored.");
			sellerShop.getOwner().getClient().sendPacket(Type.SAY, "The amount of bars (gold, silver, bronze) will be ignored.");
		}
			
		getOwner().setLime(getOwner().getLime() - (shopItem.getPrice()*availableAmount));
		
		//buy command to the player who is buying.
		getOwner().getClient().sendPacket(Type.U_SHOP, "buy", 1, position, availableAmount);
		
		//buy command to other players that are viewing the shop.
		for(Shop shop : sellerShop.getPlayersBuying()){
			if(getOwner() != shop.getOwner()){
				shop.getOwner().getClient().sendPacket(Type.U_SHOP, "buy", 2, position, availableAmount);
			}
		}
		List<ShopItem> shopItems = new Vector<ShopItem>();
		
		while(availableAmount-- > 0){
			shopItem = sellerShop.getItem(new ShopPosition(position));
			//int[] freePosition = getOwner().getInventory().getFreeSlots(shopItem.getItem(), -1);
			//InventoryPosition inventoryPosition = new InventoryPosition(freePosition[1],freePosition[2],freePosition[0]);
			//InventoryItem inventoryItem = new InventoryItem(shopItem.getItem(),	inventoryPosition);
			//getOwner().getInventory().addInventoryItem(inventoryItem);
			InventoryItem inventoryItem = getOwner().getInventory().storeItem(shopItem.getItem(), -1);
			getOwner().getClient().sendPacket(Type.INVEN, inventoryItem, getOwner().getClient().getVersion());
			shopItems.add(shopItem);
			sellerShop.removeItem(shopItem);
			
		}
		getOwner().getClient().sendPacket(Type.SAY, "The item as been bought: "+shopItem.getItem().getType().getName()+
													" ("+shopItems.size()+")");
		LoggerFactory.getLogger(this.getClass()).info("Player "+getOwner()+" bought "+shopItems.size()+" item(s) "
														+shopItem.getItem()+" from player "+sellerShop.getOwner());
		sellerShop.sell(getOwner(), shopItems);
	}
	
	public void sell(Player buyer, List<ShopItem> shopItems){
		
		if(buyer==null || shopItems==null)
			return;
		
		int saleLime = 0;
		ShopItem sItem = null;
		
		for(ShopItem shopItem : shopItems){
			sItem = shopItem;
			saleLime += shopItem.getPrice();
			//removeItem(shopItem);
		}
		setLime(getLime() + saleLime);
		getOwner().getClient().sendPacket(Type.U_SHOP, "sell", 1, sItem.getShopPosition().getSlot(), shopItems.size(),
												sItem, getLime());
		getOwner().getClient().sendPacket(Type.SAY, buyer.getName()+" bought item: "+sItem.getItem().getType().getName()+
													" ("+shopItems.size()+")");
	}
}