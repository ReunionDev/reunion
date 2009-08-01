package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.*;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Skill {
	private int id;

	private int level;

	private int race;

	private int maxLevel;
			
	private int currLevel=0;
	
	private int minFirstRange;
	
	private int maxFirstRange;
	
	private float currFirstRange=0;
	
	private int minSecondRange;
	
	private int maxSecondRange;
	
	private float currSecondRange=0;
			
	private int type;
	
	private int minConsumn;
	
	private int maxConsumn;
	
	private float currConsumn=0;
	
	private int statusUsed;
		
	public G_Skill(int id) {
		this.id = id;
		loadFromReference(id);
	}

	public int getId() {
		return this.id;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevel() {
		return this.level;
	}

	public void setMinFirstRange(int minFirstRange) {
		this.minFirstRange = minFirstRange;
	}
	public int getMinFirstRange() {
		return this.minFirstRange;
	}

	public void setMaxFirstRange(int maxFirstRange) {
		this.maxFirstRange = maxFirstRange;
	}
	public int getMaxFirstRange() {
		return this.maxFirstRange;
	}
	
	public void setCurrFirstRange(float currFirstRange) {
		this.currFirstRange = currFirstRange;
	}
	public float getCurrFirstRange() {
		return this.currFirstRange;
	}
	
	public void setMinSecondRange(int minSecondRange) {
		this.minSecondRange = minSecondRange;
	}
	public int getMinSecondRange() {
		return this.minSecondRange;
	}

	public void setMaxSecondRange(int maxSecondRange) {
		this.maxSecondRange = maxSecondRange;
	}
	public int getMaxSecondRange() {
		return this.maxSecondRange;
	}
	
	public void setCurrSecondRange(float currSecondRange) {
		this.currSecondRange = currSecondRange;
	}
	public float getCurrSecondRange() {
		return this.currSecondRange;
	}

	public void setRace(int race) {
		this.race = race;
	}

	public int getRace() {
		return this.race;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	public int getMaxLevel() {
		return this.maxLevel;
	}
	
	public int getCurrLevel() {
		return this.currLevel;
	}
	public void setCurrLevel(int currLevel) {
		this.currLevel = currLevel;
	}

	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return this.type;
	}
	
	public void setMinConsumn(int minConsumn) {
		this.minConsumn = minConsumn;
	}
	public int getMinConsumn() {
		return this.minConsumn;
	}
	
	public void setMaxConsumn(int maxConsumn) {
		this.maxConsumn = maxConsumn;
	}
	public int getMaxConsumn() {
		return this.maxConsumn;
	}
		
	public void setCurrConsumn(float currConsumn) {
		this.currConsumn = currConsumn;
	}
	public float getCurrConsumn() {
		return this.currConsumn;
	}
	
	//public void incCurrLevel(int amount) {
	//	this.currLevel = this.currLevel + amount;
	//}

	public void setStatusUsed(int statusUsed) {
		this.statusUsed = statusUsed;
	}
	public int getStatusUsed() {
		return this.statusUsed;
	}
	
	public void loadFromReference(int id)
	{	
	  S_ParsedItem skill = S_Reference.getInstance().getSkillReference().getItemById(id);
		
	  if (skill==null)
	  {
		// cant find Item in the reference continue to load defaults:
		  setLevel(0);
		  setMaxLevel(0);
		  setMinFirstRange(0);
		  setMaxFirstRange(0);
		  setMinSecondRange(0);
		  setMaxSecondRange(0);
		  setMinConsumn(0);
		  setMaxConsumn(0);
		  setType(0);
		  setStatusUsed(-1);
	  }
	  else {
		
		if(skill.checkMembers(new String[]{"Level"}))
		{
			// use member from file
			setLevel(Integer.parseInt(skill.getMemberValue("Level")));
		}
		else
		{
			// use default
			setLevel(0);
		}
		if(skill.checkMembers(new String[]{"MaxLevel"}))
		{
			// use member from file
			setMaxLevel(Integer.parseInt(skill.getMemberValue("MaxLevel")));
		}
		else
		{
			// use default
			setMaxLevel(0);
		}
		if(skill.checkMembers(new String[]{"MinFirstRange"}))
		{
			// use member from file
			setMinFirstRange(Integer.parseInt(skill.getMemberValue("MinFirstRange")));
		}
		else
		{
			// use default
			setMinFirstRange(0);
		}
		if(skill.checkMembers(new String[]{"MaxFirstRange"}))
		{
			// use member from file
			setMaxFirstRange(Integer.parseInt(skill.getMemberValue("MaxFirstRange")));
		}
		else
		{
			// use default
			setMaxFirstRange(0);
		}
		if(skill.checkMembers(new String[]{"MinSecondRange"}))
		{
			// use member from file
			setMinSecondRange(Integer.parseInt(skill.getMemberValue("MinSecondRange")));
		}
		else
		{
			// use default
			setMinSecondRange(0);
		}
		if(skill.checkMembers(new String[]{"MaxSecondRange"}))
		{
			// use member from file
			setMaxSecondRange(Integer.parseInt(skill.getMemberValue("MaxSecondRange")));
		}
		else
		{
			// use default
			setMaxSecondRange(0);
		}
		if(skill.checkMembers(new String[]{"MinConsumn"}))
		{
			// use member from file
			setMinConsumn(Integer.parseInt(skill.getMemberValue("MinConsumn")));
		}
		else
		{
			// use default
			setMinConsumn(0);
		}
		if(skill.checkMembers(new String[]{"MaxConsumn"}))
		{
			// use member from file
			setMaxConsumn(Integer.parseInt(skill.getMemberValue("MaxConsumn")));
		}
		else
		{
			// use default
			setMaxConsumn(0);
		}
		if(skill.checkMembers(new String[]{"Type"}))
		{
			// use member from file
			setType(Integer.parseInt(skill.getMemberValue("Type")));
		}
		else
		{
			// use default
			setType(0);
		}
		if(skill.checkMembers(new String[]{"StatusUsed"}))
		{
			// use member from file
			setStatusUsed(Integer.parseInt(skill.getMemberValue("StatusUsed")));
		}
		else
		{
			// use default
			setStatusUsed(-1);
		}
	  }
	}
}