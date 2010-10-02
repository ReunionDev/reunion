package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class EtcItem extends PlayerItem {
	private Race race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia;

	// 3 - Human; 4 - Pet

	private int reqStr;

	private int reqInt;

	private int reqDex;

	private int skillLevel;

	public EtcItem(int id) {
		super(id);
	}

	public Race getRace() {
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

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:		
			setSkillLevel(0);
			setReqStr(0);
			setReqInt(0);
			setReqDex(0);
			setRace(Race.UNDEFINED);
		} else {
			
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
				setRace(Race.values()[Integer.parseInt(item.getMemberValue("Race"))]);
			} else {
				// use default
				setRace(Race.UNDEFINED);
			}
		}
	}

	public void setRace(Race race) {
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
}