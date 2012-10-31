package org.reunionemu.jreunion.game;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 * Item wrapper for Inventory
 */
public abstract class InventoryItem {
	
	public InventoryItem(){
		
	}
	
	public InventoryItem(Item<?> item, InventoryPosition position, Player player) {
		setItem(item);
		setPosition(position);
		setPlayer(player);
	}

	public abstract Item<?> getItem();
	
	public abstract void setItem(Item<?> item);

	public abstract InventoryPosition getPosition();
	
	public abstract void setPosition(InventoryPosition position);

	public int getSizeX() {
		return getItem().getType().getSizeX();
	}

	public int getSizeY() {
		return getItem().getType().getSizeY();
	}

	public abstract void setPlayer(Player player);

	public abstract Player getPlayer();
}
