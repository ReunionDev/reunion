package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PlayerItem extends Item {
	
	private int level;
	
	private int reqStr;

	private int reqDex;

	private int reqInt;
	
	public PlayerItem(int id) {
		super(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
	
	public int getLevel() {
		return level;
	}	
	
	public void setLevel(int level) {
		this.level = level;
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
	
	public void setReqDex(int reqDex) {
		this.reqDex = reqDex;
	}

	public void setReqInt(int reqInt) {
		this.reqInt = reqInt;
	}

	public void setReqStr(int reqStr) {
		this.reqStr = reqStr;
	}
}