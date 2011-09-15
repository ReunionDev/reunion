package com.googlecode.reunion.jreunion.game.items.equipment;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.PlayerItem;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Armor extends PlayerItem {
	private int def;
	
	private int defunmodified;

	private int reqStr;

	private int reqInt;

	private int reqDex;

	private int level;

	private int race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia;

	// 3 - Human

	private int position; // 0 - head; 1 - body; 2 - legs; 3 - Shoulder Mount;

	// 4 - Feet; 5 - Shield;

	private float boltMagicDef; // value in %

	private float coldMagicDef; // value in %

	private float volcanicMagicDef; // value in %

	private float stunMagicDef; // value in %

	private float magicDef; // value in %

	private float manaUsed; // value in %

	public Armor(int id) {
		super(id);
		loadFromReference(id);
	}

	public float getBoltMagicDef() {
		return boltMagicDef;
	}

	public float getColdMagicDef() {
		return coldMagicDef;
	}

	public int getDef() {
		return def;
	}

	public int getUnmodifiedDef() {
		return defunmodified;
	}

	public int getLevel() {
		return level;
	}

	public float getMagicDef() {
		return magicDef;
	}

	public float getManaUsed() {
		return manaUsed;
	}

	public int getPosition() {
		return position;
	}

	public int getRace() {
		return race;
	}

	public int getReqDex() {
		return reqDex;
	}

	public int getReqInt() {
		return reqInt;
	}

	public int getReqStr() {
		return reqStr;
	}

	public float getStunMagicDef() {
		return stunMagicDef;
	}

	public float getVolcanicMagicDef() {
		return volcanicMagicDef;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setDef(0);
			setReqStr(0);
			setReqInt(0);
			setReqDex(0);
			setLevel(0);
			setRace(-1);
			setPosition(-1);
			setBoltMagicDef(0);
			setVolcanicMagicDef(0);
			setColdMagicDef(0);
			setStunMagicDef(0);
			setMagicDef(0);
			setManaUsed(0);
		} else {

			if (item.checkMembers(new String[] { "Def" })) {
				// use member from file
				setDef(Integer.parseInt(item.getMemberValue("Def")));
				setUnmodifiedDef(Integer.parseInt(item.getMemberValue("Def")));
			} else {
				// use default
				setDef(0);
				setUnmodifiedDef(0);
			}
			if (item.checkMembers(new String[] { "ReqStr" })) {
				// use member from file
				setReqStr(Integer.parseInt(item.getMemberValue("ReqStr")));
			} else {
				// use default
				setReqStr(0);
			}
			if (item.checkMembers(new String[] { "ReqInt" })) {
				// use member from file
				setReqInt(Integer.parseInt(item.getMemberValue("ReqInt")));
			} else {
				// use default
				setReqInt(0);
			}
			if (item.checkMembers(new String[] { "ReqDex" })) {
				// use member from file
				setReqDex(Integer.parseInt(item.getMemberValue("ReqDex")));
			} else {
				// use default
				setReqDex(0);
			}
			if (item.checkMembers(new String[] { "Level" })) {
				// use member from file
				setLevel(Integer.parseInt(item.getMemberValue("Level")));
			} else {
				// use default
				setLevel(0);
			}
			if (item.checkMembers(new String[] { "Race" })) {
				// use member from file
				setRace(Integer.parseInt(item.getMemberValue("Race")));
			} else {
				// use default
				setRace(-1);
			}
			if (item.checkMembers(new String[] { "Position" })) {
				// use member from file
				setPosition(Integer.parseInt(item.getMemberValue("Position")));
			} else {
				// use default
				setPosition(-1);
			}
			if (item.checkMembers(new String[] { "BoltMagicDef" })) {
				// use member from file
				setBoltMagicDef(Float.parseFloat(item
						.getMemberValue("BoltMagicDef")));
			} else {
				// use default
				setBoltMagicDef(0);
			}
			if (item.checkMembers(new String[] { "VolcanicMagicDef" })) {
				// use member from file
				setVolcanicMagicDef(Float.parseFloat(item
						.getMemberValue("VolcanicMagicDef")));
			} else {
				// use default
				setVolcanicMagicDef(0);
			}
			if (item.checkMembers(new String[] { "ColdMagicDef" })) {
				// use member from file
				setColdMagicDef(Float.parseFloat(item
						.getMemberValue("ColdMagicDef")));
			} else {
				// use default
				setColdMagicDef(0);
			}
			if (item.checkMembers(new String[] { "StunMagicDef" })) {
				// use member from file
				setStunMagicDef(Float.parseFloat(item
						.getMemberValue("StunMagicDef")));
			} else {
				// use default
				setStunMagicDef(0);
			}
			if (item.checkMembers(new String[] { "MagicDef" })) {
				// use member from file
				setMagicDef(Float.parseFloat(item.getMemberValue("MagicDef")));
			} else {
				// use default
				setMagicDef(0);
			}
			if (item.checkMembers(new String[] { "ManaUsed" })) {
				// use member from file
				setManaUsed(Float.parseFloat(item.getMemberValue("ManaUsed")));
			} else {
				// use default
				setManaUsed(0);
			}
		}
	}

	public void setBoltMagicDef(float boltMagicDef) {
		this.boltMagicDef = boltMagicDef;
	}

	public void setColdMagicDef(float coldMagicDef) {
		this.coldMagicDef = coldMagicDef;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public void setUnmodifiedDef(int def) {
		this.defunmodified = def;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void setMagicDef(float magicDef) {
		this.magicDef = magicDef;
	}

	public void setManaUsed(float manaUsed) {
		this.manaUsed = manaUsed;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setRace(int race) {
		this.race = race;
	}

	public void setReqDex(int reqDex) {
		this.reqDex = reqDex;
	}

	public void setReqInt(int reqInt) {
		this.reqInt = reqInt;
	}

	public void setReqStr(int reqStr) {
		this.reqStr = reqStr;
	}

	public void setStunMagicDef(float stunMagicDef) {
		this.stunMagicDef = stunMagicDef;
	}

	public void setVolcanicMagicDef(float volcanicMagicDef) {
		this.volcanicMagicDef = volcanicMagicDef;
	}
}