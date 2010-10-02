package com.googlecode.reunion.jreunion.game.items;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.PlayerItem;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class SpecialWeapon extends PlayerItem {
	private int speed; // 0 - Very Slow; 1 - Slow; 2 - Normal; 3 - Fast; 4 -

	// Very Fast

	private int minDamge;

	// Very Fast

	private int maxDamage;

	private int reqStr;

	private int reqDex;

	private int reqInt;

	private int level;

	private int position; // 0 - head; 1 - body; 2 - legs; 3 - Feet;

	// 4 - Shield; 5 - Cape/Wings/Special Weapon; 6 - weapon

	private int race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia;

	// 3 - Human

	private int stmUsed;

	private int manaUsed;

	private int eeUsed;

	private int handed;

	private int slot;

	public SpecialWeapon(int id) {
		super(id);
	}

	public int getSpecialWeaponEeUsed() {
		return eeUsed;
	}

	public int getSpecialWeaponHanded() {
		return handed;
	}

	public int getSpecialWeaponLevel() {
		return level;
	}

	public int getSpecialWeaponManaUsed() {
		return manaUsed;
	}

	public int getSpecialWeaponMaxDamage() {
		return maxDamage;
	}

	public int getSpecialWeaponMinDamage() {
		return minDamge;
	}

	/**
	 * @return
	 */
	public int getSpecialWeaponPosition() {
		return position;
	}

	public int getSpecialWeaponRace() {
		return race;
	}

	public int getSpecialWeaponReqDex() {
		return reqDex;
	}

	public int getSpecialWeaponReqInt() {
		return reqInt;
	}

	public int getSpecialWeaponReqStr() {
		return reqStr;
	}

	public int getSpecialWeaponSlot() {
		return slot;
	}

	public int getSpecialWeaponSpeed() {
		return speed;
	}

	public int getSpecialWeaponStmUsed() {
		return stmUsed;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setSpecialWeaponLevel(1);
			setSpecialWeaponHanded(0);
			setSpecialWeaponSpeed(4);
			setSpecialWeaponMinDamage(1);
			setSpecialWeaponMaxDamage(1);
			setSpecialWeaponReqStr(0);
			setSpecialWeaponReqInt(0);
			setSpecialWeaponReqDex(0);
			setSpecialWeaponPosition(5);
			setSpecialWeaponRace(-1);
			setSpecialWeaponStmUsed(0);
			setSpecialWeaponEeUsed(0);
			setSpecialWeaponManaUsed(0);
		} else {
			if (item.checkMembers(new String[] { "Level" })) {
				// use member from file
				setSpecialWeaponLevel(Integer.parseInt(item
						.getMemberValue("Level")));
			} else {
				// use default
				setSpecialWeaponLevel(1);
			}
			if (item.checkMembers(new String[] { "Handed" })) {
				// use member from file
				setSpecialWeaponHanded(Integer.parseInt(item
						.getMemberValue("Handed")));
			} else {
				// use default
				setSpecialWeaponHanded(0);
			}
			if (item.checkMembers(new String[] { "Speed" })) {
				// use member from file
				setSpecialWeaponSpeed(Integer.parseInt(item
						.getMemberValue("Speed")));
			} else {
				// use default
				setSpecialWeaponSpeed(4);
			}
			if (item.checkMembers(new String[] { "MinDmg" })) {
				// use member from file
				setSpecialWeaponMinDamage(Integer.parseInt(item
						.getMemberValue("MinDmg")));
			} else {
				// use default
				setSpecialWeaponMinDamage(1);
			}
			if (item.checkMembers(new String[] { "MaxDmg" })) {
				// use member from file
				setSpecialWeaponMaxDamage(Integer.parseInt(item
						.getMemberValue("MaxDmg")));
			} else {
				// use default
				setSpecialWeaponMaxDamage(1);
			}
			if (item.checkMembers(new String[] { "ReqStr" })) {
				// use member from file
				setSpecialWeaponReqStr(Integer.parseInt(item
						.getMemberValue("ReqStr")));
			} else {
				// use default
				setSpecialWeaponReqStr(0);
			}
			if (item.checkMembers(new String[] { "ReqInt" })) {
				// use member from file
				setSpecialWeaponReqInt(Integer.parseInt(item
						.getMemberValue("ReqInt")));
			} else {
				// use default
				setSpecialWeaponReqInt(0);
			}
			if (item.checkMembers(new String[] { "ReqDex" })) {
				// use member from file
				setSpecialWeaponReqDex(Integer.parseInt(item
						.getMemberValue("ReqDex")));
			} else {
				// use default
				setSpecialWeaponReqDex(0);
			}
			if (item.checkMembers(new String[] { "Position" })) {
				// use member from file
				setSpecialWeaponPosition(Integer.parseInt(item
						.getMemberValue("Position")));
			} else {
				// use default
				setSpecialWeaponPosition(5);
			}
			if (item.checkMembers(new String[] { "Race" })) {
				// use member from file
				setSpecialWeaponRace(Integer.parseInt(item
						.getMemberValue("Race")));
			} else {
				// use default
				setSpecialWeaponRace(-1);
			}
			if (item.checkMembers(new String[] { "StmUsed" })) {
				// use member from file
				setSpecialWeaponStmUsed(Integer.parseInt(item
						.getMemberValue("StmUsed")));
			} else {
				// use default
				setSpecialWeaponStmUsed(0);
			}
			if (item.checkMembers(new String[] { "EeUsed" })) {
				// use member from file
				setSpecialWeaponEeUsed(Integer.parseInt(item
						.getMemberValue("EeUsed")));
			} else {
				// use default
				setSpecialWeaponEeUsed(0);
			}
			if (item.checkMembers(new String[] { "ManaUsed" })) {
				// use member from file
				setSpecialWeaponManaUsed(Integer.parseInt(item
						.getMemberValue("ManaUsed")));
			} else {
				// use default
				setSpecialWeaponManaUsed(0);
			}
		}
	}

	public void setSpecialWeaponEeUsed(int eeUsed) {
		this.eeUsed = eeUsed;
	}

	public void setSpecialWeaponHanded(int handed) {
		this.handed = handed;
	}

	public void setSpecialWeaponLevel(int level) {
		this.level = level;
	}

	public void setSpecialWeaponManaUsed(int manaUsed) {
		this.manaUsed = manaUsed;
	}

	public void setSpecialWeaponMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}

	public void setSpecialWeaponMinDamage(int minDamge) {
		this.minDamge = minDamge;
	}

	public void setSpecialWeaponPosition(int position) {
		this.position = position;
	}

	/**
	 * @param race
	 */
	public void setSpecialWeaponRace(int race) {
		this.race = race;
	}

	public void setSpecialWeaponReqDex(int reqDex) {
		this.reqDex = reqDex;
	}

	public void setSpecialWeaponReqInt(int reqInt) {
		this.reqInt = reqInt;
	}

	public void setSpecialWeaponReqStr(int reqStr) {
		this.reqStr = reqStr;
	}

	public void setSpecialWeaponSlot(int slot) {
		this.slot = slot;
	}

	public void setSpecialWeaponSpeed(int speed) {
		this.speed = speed;
	}

	public void setSpecialWeaponStmUsed(int stmUsed) {
		this.stmUsed = stmUsed;
	}
}