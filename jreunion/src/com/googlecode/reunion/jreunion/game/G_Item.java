package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Item extends G_Entity {
	private int uniqueId;

	private int price;

	private int sizeX; // number of cols

	private int sizeY; // number of rows

	private int gemNumber;

	private int extraStats;

	private int type;

	private int rotation;

	private int description;

	public G_Item(int type) {
		super();
		this.type = type;
		loadFromReference(type);
	}

	public int getDescription() {
		return description;
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

	public int getUniqueId() {
		return uniqueId;
	}

	public void loadFromReference(int id) {
		S_ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);
		
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