package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Inventory {
	
	private List<InventoryItem> items = null;;
	
	private HandPosition holdingItem = null;

	public Inventory() {
		items = new Vector<InventoryItem>();
	}
	
	public void setHoldingItem(HandPosition holdingItem) {
		this.holdingItem = holdingItem;
	}
	
	public HandPosition getHoldingItem() {
		return holdingItem;
	}
	
	public Iterator<InventoryItem> getInventoryIterator() {
		return items.iterator();
	}
	
	public boolean posEmpty(int tab, int posX, int posY) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		
		while (iter.hasNext()) {
			InventoryItem item = iter.next();
			if(item.getPosition().getTab() == tab){
				for (int x = item.getPosition().getPosX(); x < item.getPosition().getPosX()
						+ item.getItem().getType().getSizeX(); x++) {
					for (int y = item.getPosition().getPosY(); y < item.getPosition().getPosY()
							+ item.getItem().getType().getSizeY(); y++) {
						if (x == posX && y == posY) {
							//Logger.getLogger(Inventory.class).debug("DETECETED ITEM COLISION: ["+x+"]["+y+"] "+item.getItem().getDescription());
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public boolean itemFit(int tab, int posX, int posY, int sizeX, int sizeY) {

		//checks if item size, from the position we are trying to place it,
		//don't stay outside the inventory size.
		if (posX + sizeX > 8 || posY + sizeY > 6) {
			return false;
		}

		//checks if every position occupied by the item, in the inventory, is free 
		for (int x = posX; x < posX + sizeX; x++) {
			for (int y = posY; y < posY + sizeY; y++) {
				//Logger.getLogger(Inventory.class).debug("CHECKING FIT POSITION ["+x+"]["+y+"]");
				if (posEmpty(tab, x, y) == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	//returns the stored item position (top left corner) of an inventory item.
	//this is used, for example, if we check the position [1;3] in the inventory,
	//and it correspond to an item that starts in the position [0;1].
	public int[] getDetectedItemPosition(int tab, int posX, int posY, int sizeX, int sizeY){
		
		int[] position = new int[3];
		
		//checks if every position occupied by the item, in the inventory, is free 
		for (int x = posX; x < posX + sizeX; x++) {
			for (int y = posY; y < posY + sizeY; y++) {
				if (posEmpty(tab, x, y) == false) {
					
					position[0] = tab;
					position[1] = x;
					position[2] = y;
					return position;
					
					//return getItem(tab, x, y);
				}
			}
		}
		return null;
	}
	
	//return the first free slot, of the item size
	public int[] getFreeSlots(Item<?> item, int neededTab){
		
		if(item == null)
			return null;
		
		int[] position = new int[3];
		int firstTab = 0;
		int lastTab = 2;
		
		if(neededTab > -1){
			firstTab = neededTab;
			lastTab= neededTab;
		}
		
		//checks what is the first free position, where the item fits and returns it.
		for (int tab = firstTab; tab <= lastTab; tab++) {
			for (int posX = 0; posX < 8; posX++) {
				for (int posY = 0; posY < 6; posY++) {
					if (itemFit(tab, posX, posY, item.getType().getSizeX(), item.getType().getSizeY())) {
						position[0] = tab;
						position[1] = posX;
						position[2] = posY;
						return position;
					}
				}
			}
		}
		return null;
	}
	
	public boolean freeSlots(int tab, Item<?> item) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 6; y++) {
				if (itemFit(tab, x, y, item.getType().getSizeX(), item.getType().getSizeY())) {
					return true;
				}
			}
		}
		return false;
	}

	//returns the inventory item on the given position
	public InventoryItem getItem(int tab, int posX, int posY) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		
		while (iter.hasNext()) {
			InventoryItem invItem = (InventoryItem) iter.next();
			
			for (int x = invItem.getPosition().getPosX(); x < invItem.getPosition().getPosX()
					+ invItem.getItem().getType().getSizeX(); x++) {
				for (int y = invItem.getPosition().getPosY(); y < invItem.getPosition().getPosY()
						+ invItem.getItem().getType().getSizeY(); y++) {
					if (x == posX && y == posY && invItem.getPosition().getTab() == tab) {
						return invItem;
					}
				}
			}
		}
		return null;
	}
	
	public InventoryItem getItem(int itemId) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		
		while (iter.hasNext()) {
			InventoryItem invItem = iter.next();

			if (invItem.getItem().getEntityId() == itemId) {
				return invItem;
			}
		}
		return null;
	}
	
	//stores an inventory item in the first free position.
	//in this case, there is no item selected.
	public InventoryItem storeItem(Item<?> item, int neededTab){
		if(item != null){
			
			int[] position = getFreeSlots(item, neededTab);
			if(position == null)
				return null;
			InventoryItem inventoryItem = new InventoryItem(item,
					new InventoryPosition(position[1],position[2],position[0]));
			
			addInventoryItem(inventoryItem);
			return inventoryItem;
		}
		return null;
	}
	
	//stores an inventory item on the given position.
	public boolean storeInventoryItem(int tab, int posX, int posY){
		
		HandPosition handPosition = getHoldingItem();
		
		if(handPosition != null){
			InventoryItem inventoryItem = new InventoryItem(handPosition.getItem(),new InventoryPosition(posX,posY,tab)); 
			addInventoryItem(inventoryItem);
			
			Item<?> item = inventoryItem.getItem();
			Logger.getLogger(Inventory.class).info("Item "+item+" stored in player "+getPlayer()+
					" inventory at position {tab:"+tab+", x:"+posX+", y:"+posY+"}");
			
			setHoldingItem(null);
			return true;
		}
		return false;
	}
	
	//removes an inventory item from the given position.
	public boolean removeInventoryItem(int tab, int posX, int posY){
		
		HandPosition handPosition = getHoldingItem();
		int [] itemPosition = null;
		InventoryItem inventoryItem = null;
		
		if(handPosition != null){
			Item<?> handItem = handPosition.getItem();
			itemPosition = getDetectedItemPosition(tab, posX, posY, handItem.getType().getSizeX(),
					handItem.getType().getSizeY());
			inventoryItem = getItem(itemPosition[0], itemPosition[1], itemPosition[2]);
		}else {
			inventoryItem = getItem(tab,posX,posY);	
		}
		
		Item<?> item = inventoryItem.getItem();
		Logger.getLogger(Inventory.class).info("Item "+item+" removed from player "+getPlayer()+" inventory.");
		
		deleteInventoryItem(inventoryItem);
		
		if(handPosition != null){
			storeInventoryItem(tab,posX,posY);
		}
		setHoldingItem(new HandPosition(inventoryItem.getItem()));
		
		//when removing an item from the inventory, there must be an holding item.
		if(getHoldingItem() == null)
			return false;
		else
			return true;
	}
	
	public void addInventoryItem(InventoryItem inventoryItem){
		if (inventoryItem != null) {
				items.add(inventoryItem);
		}
	}
	
	public void deleteInventoryItem(InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			while (items.contains(inventoryItem)) {
				items.remove(inventoryItem);
			}
		}
	}
	
	/****** Manages the Items on the Inventory ******/
	public void handleInventory(int tab, int posX, int posY) {

		HandPosition handPosition = getHoldingItem();

		if(handPosition != null){
			//Logger.getLogger(Inventory.class).debug("ITEM STORED: "+holdingItem.getItem().getDescription());
			if(itemFit(tab,posX,posY,handPosition.getItem().getType().getSizeX(),
					handPosition.getItem().getType().getSizeY())){
				storeInventoryItem(tab,posX,posY);				
			}else {
				removeInventoryItem(tab, posX, posY);
				//Logger.getLogger(Inventory.class).debug("ITEM REMOVED: "+getHoldingItem().getItem().getDescription());
			}
		}else {
			if(!posEmpty(tab, posX, posY)){
				removeInventoryItem(tab, posX, posY);
				//Logger.getLogger(Inventory.class).debug("ITEM REMOVED: "+getHoldingItem().getItem().getDescription());
			}
			
		}
	}

	public Player getPlayer(){
		Iterator<Player> playerIter = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
		
		while(playerIter.hasNext()){
			Player player = (Player) playerIter.next();
			
			if(player.getInventory() == this)
				return player;
		}
		return null;
	}

	@Deprecated
	public void PrintInventoryMap(int tab) { // Debug Only
		boolean[][] newInvMap = new boolean[8][6];

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 6; y++) {
				newInvMap[x][y] = false;
			}
		}

		Logger.getLogger(Inventory.class).debug("Tab " + tab + ": \n");
		Iterator<InventoryItem> iter = getInventoryIterator();
		while (iter.hasNext()) {
			InventoryItem item = iter.next();

			for (int x = item.getPosition().getPosX(); x < item.getPosition().getPosX()
					+ item.getItem().getType().getSizeX(); x++) {
				for (int y = item.getPosition().getPosY(); y < item.getPosition().getPosY()
						+ item.getItem().getType().getSizeY(); y++) {
					if (item.getPosition().getTab() == tab) {
						newInvMap[x][y] = true;
					}
				}
			}
		}

		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 8; x++) {
				if (newInvMap[x][y] == false) {
					Logger.getLogger(Inventory.class).debug("0");
				}
				if (newInvMap[x][y] == true) {
					Logger.getLogger(Inventory.class).debug("1");
				}
			}
		}
	}
	
	@Deprecated
	public boolean addItem(Item<?> item) {
		
		for (int tab = 0; tab < 3; tab++) {
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 6; y++) {
					if(addItem(x, y, item, tab))
						return true;
				}
			}
		}
		return false;
	}

	@Deprecated
	public boolean addItem(int posX, int posY, Item<?> item, int tab) {

		InventoryItem inventoryItem = new InventoryItem(item, new InventoryPosition(posX, posY, tab));

		if (itemFit(tab, posX, posY, item.getType().getSizeX(), item.getType().getSizeY()) == true) {
			items.add(inventoryItem);
			return true;
			// Logger.getLogger(Inventory.class).info("Item Inserted\n");
			// PrintInventoryMap(0);
			// PrintInventoryMap(1);
			// PrintInventoryMap(2);
		}
		return false;
	}
	
	@Deprecated
	public int getSize() {
		return items.size();
	}
	
	@Deprecated
	public InventoryItem getItem(Item<?> item) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		
		while (iter.hasNext()) {
			InventoryItem invItem = iter.next();

			if (invItem.getItem() == item) {
				return invItem;
			}
		}
		return null;
	}
}