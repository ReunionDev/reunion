package com.googlecode.reunion.jreunion.game.items;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.PlayerItem;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class SpecialWeapon extends PlayerItem {
	
	private int speed; // 0 - Very Slow; 1 - Slow; 2 - Normal; 3 - Fast; 4 - Very Fast

	private int minDamge;

	private int maxDamage;

	private int position; // 0 - head; 1 - body; 2 - legs; 3 - Feet;

	// 4 - Shield; 5 - Cape/Wings/Special Weapon; 6 - weapon

	private int race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia; 3 - Human

	private int stmUsed;

	private int manaUsed;

	private int eeUsed;

	private int handed;

	private int slot;

	public SpecialWeapon(int id) {
		super(id);
	}

	public int getEeUsed() {
		return eeUsed;
	}

	public int getHanded() {
		return handed;
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

	/**
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	public int getRace() {
		return race;
	}

	public int getSlot() {
		return slot;
	}

	public int getSpeed() {
		return speed;
	}

	public int getStmUsed() {
		return stmUsed;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setLevel(1);
			setHanded(0);
			setSpeed(4);
			setMinDamage(1);
			setMaxDamage(1);
			setReqStr(0);
			setReqInt(0);
			setReqDex(0);
			setPosition(5);
			setRace(-1);
			setStmUsed(0);
			setEeUsed(0);
			setManaUsed(0);
		} else {
			if (item.checkMembers(new String[] { "Level" })) {
				// use member from file
				setLevel(Integer.parseInt(item
						.getMemberValue("Level")));
			} else {
				// use default
				setLevel(1);
			}
			if (item.checkMembers(new String[] { "Handed" })) {
				// use member from file
				setHanded(Integer.parseInt(item
						.getMemberValue("Handed")));
			} else {
				// use default
				setHanded(0);
			}
			if (item.checkMembers(new String[] { "Speed" })) {
				// use member from file
				setSpeed(Integer.parseInt(item
						.getMemberValue("Speed")));
			} else {
				// use default
				setSpeed(4);
			}
			if (item.checkMembers(new String[] { "MinDmg" })) {
				// use member from file
				setMinDamage(Integer.parseInt(item
						.getMemberValue("MinDmg")));
			} else {
				// use default
				setMinDamage(1);
			}
			if (item.checkMembers(new String[] { "MaxDmg" })) {
				// use member from file
				setMaxDamage(Integer.parseInt(item
						.getMemberValue("MaxDmg")));
			} else {
				// use default
				setMaxDamage(1);
			}
			if (item.checkMembers(new String[] { "ReqStr" })) {
				// use member from file
				setReqStr(Integer.parseInt(item
						.getMemberValue("ReqStr")));
			} else {
				// use default
				setReqStr(0);
			}
			if (item.checkMembers(new String[] { "ReqInt" })) {
				// use member from file
				setReqInt(Integer.parseInt(item
						.getMemberValue("ReqInt")));
			} else {
				// use default
				setReqInt(0);
			}
			if (item.checkMembers(new String[] { "ReqDex" })) {
				// use member from file
				setReqDex(Integer.parseInt(item
						.getMemberValue("ReqDex")));
			} else {
				// use default
				setReqDex(0);
			}
			if (item.checkMembers(new String[] { "Position" })) {
				// use member from file
				setPosition(Integer.parseInt(item
						.getMemberValue("Position")));
			} else {
				// use default
				setPosition(5);
			}
			if (item.checkMembers(new String[] { "Race" })) {
				// use member from file
				setRace(Integer.parseInt(item
						.getMemberValue("Race")));
			} else {
				// use default
				setRace(-1);
			}
			if (item.checkMembers(new String[] { "StmUsed" })) {
				// use member from file
				setStmUsed(Integer.parseInt(item
						.getMemberValue("StmUsed")));
			} else {
				// use default
				setStmUsed(0);
			}
			if (item.checkMembers(new String[] { "EeUsed" })) {
				// use member from file
				setEeUsed(Integer.parseInt(item
						.getMemberValue("EeUsed")));
			} else {
				// use default
				setEeUsed(0);
			}
			if (item.checkMembers(new String[] { "ManaUsed" })) {
				// use member from file
				setManaUsed(Integer.parseInt(item
						.getMemberValue("ManaUsed")));
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
		this.handed = handed;
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

	/**
	 * @param race
	 */
	public void setRace(int race) {
		this.race = race;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setStmUsed(int stmUsed) {
		this.stmUsed = stmUsed;
	}
	
	public long getDamage(){
		return (long)(getMinDamage() + ((getMaxDamage()-getMinDamage())*Math.random()));
	}

}