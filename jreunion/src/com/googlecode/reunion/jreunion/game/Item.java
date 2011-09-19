package com.googlecode.reunion.jreunion.game;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
import com.googlecode.reunion.jreunion.game.items.equipment.Armor;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Item implements Entity {
	
	private int price;

	private int entityId = -1;
	
	private int itemId = -1; //for database;

	private int sizeX; // number of cols

	private int sizeY; // number of rows

	private int gemNumber;

	private int extraStats;

	private int type;

	private int rotation;

	private String description;

	public Item(int type) {
		super();
		this.type = type;
		loadFromReference(type);
	}
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int id) {
		this.entityId = id;
	}

	public int getExtraStats() {
		return extraStats;
	}

	public int getGemNumber() {
		return gemNumber;
	}

	public int getPrice() {
		return price;
	}

	public int getRotation() {
		return rotation;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getType() {
		return type;
	}
	
	public String getName(){		
		return Reference.getInstance().getItemReference()
		.getItemById(getType()).getName();
	}

	public void loadFromReference(int type) {		
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(type);
		
		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setSizeX(1);
			setSizeY(1);
			setPrice(1);
			setDescription("Unknown");
		} else {

			if (item.checkMembers(new String[] { "SizeX" })) {
				// use member from file
				setSizeX(Integer.parseInt(item.getMemberValue("SizeX")));
			} else {
				// use default
				setSizeX(1);
			}
			if (item.checkMembers(new String[] { "SizeY" })) {
				// use member from file
				setSizeY(Integer.parseInt(item.getMemberValue("SizeY")));
			} else {
				// use default
				setSizeY(1);
			}
			if (item.checkMembers(new String[] { "Price" })) {
				// use member from file
				setPrice(Integer.parseInt(item.getMemberValue("Price")));
			} else {
				// use default
				setPrice(1);
			}
			if (item.checkMembers(new String[] { "Description" })) {
				// use member from file
				setDescription(item.getMemberValue("Description"));
			} else {
				// use default
				setDescription(item.getName());
			}
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getGradeLevel(){
		
		int gemNumber = getGemNumber();
		
		if(((PlayerItem)this).getLevel() < 181) {
			return (gemNumber/1>0?1:0)+(gemNumber/3>0?1:0)+(gemNumber/6>0?1:0)+(gemNumber/10>0?1:0)+(gemNumber/15>0?1:0);
		}
		else{
			return gemNumber;
		}
	}
	
	public void upgrade(Player player, Slot slot)
	{	
		setGemNumber(getGemNumber()+1);
		DatabaseUtils.getDinamicInstance().saveItem(this);
		DatabaseUtils.getDinamicInstance().deleteItem(player.getInventory().getHoldingItem().getItem());
		player.getInventory().setHoldingItem(null);
		player.setDefense();
		
		player.getClient().sendPacket(Type.UPGRADE, this, slot,1);
	}

	public void setExtraStats(int extraStats) {
		this.extraStats = extraStats;
	}

	public void setGemNumber(int gemNumber) {
		this.gemNumber = gemNumber;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

}