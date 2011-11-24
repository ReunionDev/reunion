package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ItemType{
	
	private int price;

	private int sizeX; // number of cols

	private int sizeY; // number of rows
	
	private int type;

	private String name;
	
	private int maxExtraStats;
	
	private int maxDurability;

	public ItemType(int type) {
		super();
		setTypeId(type);
		loadFromReference(type);
	}	
	
	public String getName() {
		return name;
	}
	
	public int getPrice() {
		return price;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getTypeId() {
		return type;
	}
	
	private void setTypeId(int typeId){
		this.type = typeId;
	}
	
	/*
	public String getName(){		
		return Reference.getInstance().getItemReference()
		.getItemById(getTypeId()).getName();
	}
	*/

	public void loadFromReference(int type) {		
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(type);
		
		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setSizeX(1);
			setSizeY(1);
			setPrice(1);
			setMaxExtraStats(0);
			setMaxDurability(0);
			setName("Unknown");
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
			if (item.checkMembers(new String[] { "ExtraStats" })) {
				// use member from file
				setMaxExtraStats(Integer.parseInt(item.getMemberValue("ExtraStats")));
			} else {
				// use default
				setMaxExtraStats(0);
			}
			if (item.checkMembers(new String[] { "Durability" })) {
				// use member from file
				setMaxDurability(Integer.parseInt(item.getMemberValue("Durability")));
			} else {
				// use default
				setMaxDurability(0);
			}
			
			setName(item.getName());
			
		}
	}

	public void setName(String itemName) {
		this.name = itemName;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}
	
	public Item<?> create(){
		
		Item<?> item = new Item(this);
	
		return item;
	}
	
	public void setExtraStats(Item<?> item){
	}
	
	public void setGemNumber(Item<?> item){
	}

	public int getMaxDurability() {
		return maxDurability;
	}

	public void setMaxDurability(int maxDurability) {
		this.maxDurability = maxDurability;
	}

	public int getMaxExtraStats() {
		return maxExtraStats;
	}

	public void setMaxExtraStats(int maxExtraStats) {
		this.maxExtraStats = maxExtraStats;
	}

	
	
}