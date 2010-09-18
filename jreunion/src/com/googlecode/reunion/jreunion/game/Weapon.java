package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Weapon extends PlayerItem {
	private int speed; // 0 - Very Fast; 1 - Fast; 2 - Normal; 3 - Slow;

	// 4 - Very Slow

	private int minDamge;

	private int maxDamage;

	private int reqStr;

	private int reqDex;

	private int reqInt;

	private int level;

	private int position; // -1 - Empty; 3 - Shoulder Mount; 9 - Weapon

	private int race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia;

	// 3 - Human; 4 - Hybrider

	private int handed; // 1 - One handed; 2 - Two handed

	private int manaUsed;

	private int eeUsed;

	public Weapon(int id) {
		super(id);
		loadFromReference(id);
	}

	/****** Handle the consumn of the weapon, if exists. ******/
	public void consumn(Player player) {
		if (getEeUsed() != 0) {
			player.updateStatus(3, player.getCurrElect()
					- player.getEquipment().getMainHand().getEeUsed(),
					player.getMaxElect());
		} else if (getManaUsed() != 0) {
			player.updateStatus(1, player.getCurrMana()
					- player.getEquipment().getMainHand().getManaUsed(),
					player.getMaxMana());
		}
	}

	public int getEeUsed() {
		return eeUsed;
	}

	public int getHanded() {
		return handed;
	}

	public int getLevel() {
		return level;
	}

	public int getManaUsed() {
		return manaUsed;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public int getMinDamage() {
		return minDamge;
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
			setEeUsed(0);
			setManaUsed(0);
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
				setMinDamage(Integer.parseInt(item.getMemberValue("MinDmg")));
			} else {
				// use default
				setMinDamage(1);
			}
			if (item.checkMembers(new String[] { "MaxDmg" })) {
				// use member from file
				setMaxDamage(Integer.parseInt(item.getMemberValue("MaxDmg")));
			} else {
				// use default
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
			if (item.checkMembers(new String[] { "EeUsed" })) {
				// use member from file
				setEeUsed(Integer.parseInt(item.getMemberValue("EeUsed")));
			} else {
				// use default
				setEeUsed(0);
			}
			if (item.checkMembers(new String[] { "ManaUsed" })) {
				// use member from file
				setManaUsed(Integer.parseInt(item.getMemberValue("ManaUsed")));
			} else {
				// use default
				setManaUsed(0);
			}
		}
	}

	public void setEeUsed(int eeUsed) {
		this.eeUsed = eeUsed;
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

	public void setManaUsed(int manaUsed) {
		this.manaUsed = manaUsed;
	}

	public void setMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}

	public void setMinDamage(int minDamge) {
		this.minDamge = minDamge;
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

}