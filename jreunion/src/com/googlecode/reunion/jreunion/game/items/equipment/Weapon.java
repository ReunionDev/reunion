package com.googlecode.reunion.jreunion.game.items.equipment;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.PlayerItem;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Weapon extends PlayerItem {
	private int speed; // 0 - Very Fast; 1 - Fast; 2 - Normal; 3 - Slow;

	// 4 - Very Slow

	private int minDamge;

	private int minUnmodifiedDamge;
	
	private int maxUnmodifiedDamge;
	
	private int maxDamage;

	private int reqStr;

	private int reqDex;

	private int reqInt;

	private int level;

	private int position; // -1 - Empty; 3 - Shoulder Mount; 9 - Weapon

	private int race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia;

	// 3 - Human; 4 - Hybrider

	private int handed; // 1 - One handed; 2 - Two handed

	private float magicDmg; // value in %
	
	private float magicUnmodifiedDmg; // value in %

	public Weapon(int id) {
		super(id);
		loadFromReference(id);
	}

	/****** Handle the consumn of the weapon, if exists. 
	 * @return TODO******/
	public abstract boolean use(Player player);
	
	

	public int getHanded() {
		return handed;
	}

	public int getLevel() {
		return level;
	}	

	public int getMaxDamage() {
		return maxDamage;
	}

	public int getMinDamage() {
		return minDamge;
	}


	public int getUnmodifiedMinDamage() {
		return minUnmodifiedDamge;
	}
	
	public int getUnmodifiedMaxDamage() {
		return maxUnmodifiedDamge;
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

	public int getSpeed() {
		return speed;
	}
	
	public float getMagicDmg() {
		return magicDmg;
	}
	
	public float getUnmodifiedMagicDmg() {
		return magicUnmodifiedDmg;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setLevel(1);
			setHanded(2);
			setSpeed(4);
			setMinDamage(1);
			setMaxDamage(1);
			setReqStr(0);
			setReqInt(0);
			setReqDex(0);
			setPosition(9);
			setRace(-1);
			setMagicDmg(0);
		} else {
			if (item.checkMembers(new String[] { "Level" })) {
				// use member from file
				setLevel(Integer.parseInt(item.getMemberValue("Level")));
			} else {
				// use default
				setLevel(1);
			}
			if (item.checkMembers(new String[] { "Handed" })) {
				// use member from file
				setHanded(Integer.parseInt(item.getMemberValue("Handed")));
			} else {
				// use default
				setHanded(2);
			}
			if (item.checkMembers(new String[] { "Speed" })) {
				// use member from file
				setSpeed(Integer.parseInt(item.getMemberValue("Speed")));
			} else {
				// use default
				setSpeed(4);
			}
			if (item.checkMembers(new String[] { "MinDmg" })) {
				// use member from file
				setUnmodifiedMinDamage(Integer.parseInt(item.getMemberValue("MinDmg")));
				setMinDamage(Integer.parseInt(item.getMemberValue("MinDmg")));
			} else {
				// use default
				setUnmodifiedMinDamage(1);
				setMinDamage(1);
			}
			if (item.checkMembers(new String[] { "MaxDmg" })) {
				// use member from file
				setUnmodifiedMaxDamage(Integer.parseInt(item.getMemberValue("MaxDmg")));
				setMaxDamage(Integer.parseInt(item.getMemberValue("MaxDmg")));
			} else {
				// use default
				setUnmodifiedMaxDamage(1);
				setMaxDamage(1);
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
			if (item.checkMembers(new String[] { "Position" })) {
				// use member from file
				setPosition(Integer.parseInt(item.getMemberValue("Position")));
			} else {
				// use default
				setPosition(9);
			}
			if (item.checkMembers(new String[] { "Race" })) {
				// use member from file
				setRace(Integer.parseInt(item.getMemberValue("Race")));
			} else {
				// use default
				setRace(-1);
			}	
			if (item.checkMembers(new String[] { "MagicDmg" })) {
				// use member from file
				setMagicDmg(Float.parseFloat(item.getMemberValue("MagicDmg")));
				setUnmodifiedMagicDmg(Float.parseFloat(item.getMemberValue("MagicDmg")));
			} else {
				// use default
				setMagicDmg(0);
				setUnmodifiedMagicDmg(0);
			}	
		}
	}
	
	public void setHanded(int handed) {
		if (handed > 1) {
			handed = 2;
		} else {
			handed = 1;
		}
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}

	public void setMinDamage(int minDamge) {
		this.minDamge = minDamge;
	}


	public void setUnmodifiedMaxDamage(int maxDamage) {
		this.maxUnmodifiedDamge = maxDamage;
	}

	public void setUnmodifiedMinDamage(int minDamge) {
		this.minUnmodifiedDamge = minDamge;
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

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void setMagicDmg(float magicDmg) {
		this.magicDmg = magicDmg;
	}
	
	public void setUnmodifiedMagicDmg(float magicDmg) {
		this.magicUnmodifiedDmg = magicDmg;
	}

}