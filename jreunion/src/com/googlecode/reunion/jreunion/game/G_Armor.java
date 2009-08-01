package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.S_Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Armor extends G_PlayerItem {
	private int def;

	private int reqStr;

	private int reqInt;

	private int reqDex;

	private int level;

	private int race; // -1 - Common; 0 - Bulkan; 1 - Kailipton; 2 - Aidia;

	// 3 - Human

	private int position; // 0 - head; 1 - body; 2 - legs; 3 - Shoulder Mount;
	
	//4 - Feet; 5 - Shield;

	private int boltMagicDef;
	
	private int coldMagicDef;
	
	private int volcanicMagicDef;
	
	private int stunMagicDef;
	
	private int magicDef; // value in %

	private int manaUsed; // value in %
	
	public G_Armor(int id) {
		super(id);
		loadFromReference(id);
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getDef() {
		return this.def;
	}

	public void setReqStr(int reqStr) {
		this.reqStr = reqStr;
	}

	public int getReqStr() {
		return this.reqStr;
	}

	public void setReqInt(int reqInt) {
		this.reqInt = reqInt;
	}

	public int getReqInt() {
		return this.reqInt;
	}

	public void setReqDex(int reqDex) {
		this.reqDex = reqDex;
	}

	public int getReqDex() {
		return this.reqDex;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	public void setRace(int race) {
		this.race = race;
	}

	public int getRace() {
		return this.race;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}
	public void setBoltMagicDef(int boltMagicDef) {
		this.boltMagicDef = boltMagicDef;
	}

	public int setBoltMagicDef() {
		return this.boltMagicDef;
	}
	public int getBoltMagicDef() {
		return this.boltMagicDef;
	}
	public void setColdMagicDef(int coldMagicDef) {
		this.coldMagicDef = coldMagicDef;
	}

	public int getColdMagicDef() {
		return this.coldMagicDef;
	}
	public void setVolcanicMagicDef(int volcanicMagicDef) {
		this.volcanicMagicDef = volcanicMagicDef;
	}

	public int getVolcanicMagicDef() {
		return this.volcanicMagicDef;
	}
	public void setStunMagicDef(int stunMagicDef) {
		this.stunMagicDef = stunMagicDef;
	}

	public int getStunMagicDef() {
		return this.stunMagicDef;
	}
	public void setMagicDef(int magicDef) {
		this.magicDef = magicDef;
	}

	public int getMagicDef() {
		return this.magicDef;
	}

	public void setManaUsed(int manaUsed) {
		this.manaUsed = manaUsed;
	}

	public int getManaUsed() {
		return this.manaUsed;
	}
	
	public void loadFromReference(int id) 
	{
	  super.loadFromReference(id);
		
	  S_ParsedItem item = S_Reference.getInstance().getItemReference().getItemById(id);
		
	  if (item==null)
	  {
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
	  }
	  else {
		
		if(item.checkMembers(new String[]{"Def"}))
		{
			// use member from file
			setDef(Integer.parseInt(item.getMemberValue("Def")));
		}
		else
		{
			// use default
			setDef(0);
		}
		if(item.checkMembers(new String[]{"ReqStr"}))
		{
			// use member from file
			setReqStr(Integer.parseInt(item.getMemberValue("ReqStr")));
		}
		else
		{
			// use default
			setReqStr(0);
		}
		if(item.checkMembers(new String[]{"ReqInt"}))
		{
			// use member from file
			setReqInt(Integer.parseInt(item.getMemberValue("ReqInt")));
		}
		else
		{
			// use default
			setReqInt(0);
		}
		if(item.checkMembers(new String[]{"ReqDex"}))
		{
			// use member from file
			setReqDex(Integer.parseInt(item.getMemberValue("ReqDex")));
		}
		else
		{
			// use default
			setReqDex(0);
		}
		if(item.checkMembers(new String[]{"Level"}))
		{
			// use member from file
			setLevel(Integer.parseInt(item.getMemberValue("Level")));
		}
		else
		{
			// use default
			setLevel(0);
		}
		if(item.checkMembers(new String[]{"Race"}))
		{
			// use member from file
			setRace(Integer.parseInt(item.getMemberValue("Race")));
		}
		else
		{
			// use default
			setRace(-1);
		}
		if(item.checkMembers(new String[]{"Position"}))
		{
			// use member from file
			setPosition(Integer.parseInt(item.getMemberValue("Position")));
		}
		else
		{
			// use default
			setPosition(-1);
		}
		if(item.checkMembers(new String[]{"BoltMagicDef"}))
		{
			// use member from file
			setBoltMagicDef(Integer.parseInt(item.getMemberValue("BoltMagicDef")));
		}
		else
		{
			// use default
			setBoltMagicDef(0);
		}
		if(item.checkMembers(new String[]{"VolcanicMagicDef"}))
		{
			// use member from file
			setVolcanicMagicDef(Integer.parseInt(item.getMemberValue("VolcanicMagicDef")));
		}
		else
		{
			// use default
			setVolcanicMagicDef(0);
		}
		if(item.checkMembers(new String[]{"ColdMagicDef"}))
		{
			// use member from file
			setColdMagicDef(Integer.parseInt(item.getMemberValue("ColdMagicDef")));
		}
		else
		{
			// use default
			setColdMagicDef(0);
		}
		if(item.checkMembers(new String[]{"StunMagicDef"}))
		{
			// use member from file
			setStunMagicDef(Integer.parseInt(item.getMemberValue("StunMagicDef")));
		}
		else
		{
			// use default
			setStunMagicDef(0);
		}
		if(item.checkMembers(new String[]{"MagicDef"}))
		{
			// use member from file
			setMagicDef(Integer.parseInt(item.getMemberValue("MagicDef")));
		}
		else
		{
			// use default
			setMagicDef(0);
		}
		if(item.checkMembers(new String[]{"ManaUsed"}))
		{
			// use member from file
			setManaUsed(Integer.parseInt(item.getMemberValue("ManaUsed")));
		}
		else
		{
			// use default
			setManaUsed(0);
		}
	  }
	}
}