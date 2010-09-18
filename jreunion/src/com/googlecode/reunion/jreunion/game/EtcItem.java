package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class EtcItem extends PlayerItem {
	private int race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia;

	// 3 - Human; 4 - Pet

	private int reqStr;

	private int reqInt;

	private int reqDex;

	private int skillLevel;

	private int hpRec;

	private int manaRec;

	private int stmRec;

	private int electRec;

	public EtcItem(int id) {
		super(id);
	}

	public int getElectRec() {
		return electRec;
	}

	public int getHpRec() {
		return hpRec;
	}

	public int getManaRec() {
		return manaRec;
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

	public int getSkillLevel() {
		return skillLevel;
	}

	public int getStmRec() {
		return stmRec;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setHpRec(0);
			setManaRec(0);
			setStmRec(0);
			setElectRec(0);
			setSkillLevel(0);
			setReqStr(0);
			setReqInt(0);
			setReqDex(0);
			setRace(-1);
		} else {
			if (item.checkMembers(new String[] { "HpRec" })) {
				// use member from file
				setHpRec(Integer.parseInt(item.getMemberValue("HpRec")));
			} else {
				// use default
				setHpRec(0);
			}
			if (item.checkMembers(new String[] { "ManaRec" })) {
				// use member from file
				setManaRec(Integer.parseInt(item.getMemberValue("ManaRec")));
			} else {
				// use default
				setManaRec(0);
			}
			if (item.checkMembers(new String[] { "StmRec" })) {
				// use member from file
				setStmRec(Integer.parseInt(item.getMemberValue("StmRec")));
			} else {
				// use default
				setStmRec(0);
			}
			if (item.checkMembers(new String[] { "ElectRec" })) {
				// use member from file
				setElectRec(Integer.parseInt(item.getMemberValue("ElectRec")));
			} else {
				// use default
				setElectRec(0);
			}
			if (item.checkMembers(new String[] { "SkillLevel" })) {
				// use member from file
				setSkillLevel(Integer.parseInt(item
						.getMemberValue("SkillLevel")));
			} else {
				// use default
				setSkillLevel(0);
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
			if (item.checkMembers(new String[] { "Race" })) {
				// use member from file
				setRace(Integer.parseInt(item.getMemberValue("Race")));
			} else {
				// use default
				setRace(-1);
			}
		}
	}

	public void setElectRec(int electRec) {
		this.electRec = electRec;
	}

	public void setHpRec(int hpRec) {
		this.hpRec = hpRec;
	}

	public void setManaRec(int manaRec) {
		this.manaRec = manaRec;
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

	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}

	public void setStmRec(int stmRec) {
		this.stmRec = stmRec;
	}
}