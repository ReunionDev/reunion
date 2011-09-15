package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.items.equipment.Armor;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.Reference;

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

	private int description;

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
	
	public int getDescription() {
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
			setDescription(-1);
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
				setDescription(Integer.parseInt(item
						.getMemberValue("Description")));
			} else {
				// use default
				setDescription(-1);
			}
		}
	}

	public void setDescription(int description) {
		this.description = description;
	}
	
	private void upgrade()
	{
		int itemlvl = 0;
		int upped = 0;
		int mindamage = 0;
		int maxdamage = 0;
		float magicdamage = 0;
		int defense = 0;
		
		int gemnumber = this.getGemNumber();
		
		if(this instanceof Weapon)
		{
			itemlvl = ((Weapon) this).getLevel();
		}
		else if(this instanceof Armor)
		{
			itemlvl = ((Armor) this).getLevel();
		}
		
		if(this instanceof Weapon)
		{
			mindamage = ((Weapon) this).getUnmodifiedMinDamage();
			maxdamage = ((Weapon) this).getUnmodifiedMaxDamage();
			magicdamage = ((Weapon) this).getUnmodifiedMagicDmg();
		}
		else if(this instanceof Armor)
		{
			defense = ((Armor) this).getUnmodifiedDef();
		}
		
		if(itemlvl < 181)
		{
			upped = (gemnumber/1>0?1:0)+(gemnumber/3>0?1:0)+(gemnumber/6>0?1:0)+(gemnumber/10>0?1:0)+(gemnumber/15>0?1:0);
			
		}
		else if(itemlvl > 181)
		{
			upped = gemnumber;
		}
		
		if(upped > 0)
		{
			//Lvl < 181 Start
			float[] steps_1to181 = new float[6];
			steps_1to181[0] = 0;
			steps_1to181[1] = .12f;
			steps_1to181[2] = .26f;
			steps_1to181[3] = .44f;
			steps_1to181[4] = .68f;
			steps_1to181[5] = 1;
			
			//Lvl < 181 End
			
			//Lvl 181 to 261 Start
			float step_181to261 = .10f;
			//Lvl 181 to 261 End
			
			if(this instanceof Weapon)
			{
				if(itemlvl < 181)
				{
					maxdamage = (int)(maxdamage*(steps_1to181[upped])+maxdamage );
					mindamage = (int)(mindamage*(steps_1to181[upped])+mindamage );
					magicdamage = magicdamage*(steps_1to181[upped])+magicdamage;
				}
				else if(itemlvl >= 181 && itemlvl < 261)
				{
					mindamage = (int)(mindamage*(step_181to261*upped)+mindamage);
					maxdamage = (int)(maxdamage*(step_181to261*upped)+maxdamage);
					magicdamage = magicdamage*(step_181to261*upped)+magicdamage;
				}
				((Weapon) this).setMinDamage(mindamage);
				((Weapon) this).setMaxDamage(maxdamage);
				((Weapon) this).setMagicDmg(magicdamage);
			}
			else if(this instanceof Armor)
			{
				if(itemlvl < 181)
				{
					defense = (int)(defense*(steps_1to181[upped])+defense );
				}
				else if(itemlvl >= 181 && itemlvl < 261)
				{
					defense = (int)(defense*(step_181to261*upped)+defense);
				}
				((Armor) this).setDef(defense);
			}
		}
	}

	public void setExtraStats(int extraStats) {
		this.extraStats = extraStats;
	}

	public void setGemNumber(int gemNumber) {
		this.gemNumber = gemNumber;
		upgrade();
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