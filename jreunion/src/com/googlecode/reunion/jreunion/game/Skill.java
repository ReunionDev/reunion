package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Skill {
	private int id;

	private int level;

	private int race;

	private int maxLevel;

	private int minFirstRange;

	private int maxFirstRange;

	private int minSecondRange;

	private int maxSecondRange;

	private float currSecondRange = 0;

	private int type;

	private int minConsumn;

	private int maxConsumn;

	private float currConsumn = 0;

	private int statusUsed;

	public Skill(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public int getMaxConsumn() {
		return maxConsumn;
	}

	public int getMaxFirstRange() {
		return maxFirstRange;
	}

	public abstract int  getMaxLevel();
	
	public int getMaxSecondRange() {
		return maxSecondRange;
	}

	public int getMinConsumn() {
		return minConsumn;
	}

	public int getMinFirstRange() {
		return minFirstRange;
	}

	public int getMinSecondRange() {
		return minSecondRange;
	}

	public int getRace() {
		return race;
	}

	public int getStatusUsed() {
		return statusUsed;
	}

	public int getType() {
		return type;
	}

	public void setCurrConsumn(float currConsumn) {
		this.currConsumn = currConsumn;
	}
	
	public boolean levelUp(Player player) {
	
		synchronized(player){			
			
			java.util.Map<Skill,Integer> skills = player.getSkills();
			
			if(!skills.containsKey(this))
				return false; //cheater?
		
			int currentSkillLevel = skills.get(this);
			
			if(currentSkillLevel < this.getMaxLevel() && this.getLevelRequirement(currentSkillLevel+1) <= player.getLevel()){
				
				skills.put(this, ++currentSkillLevel);
				
				player.getClient().sendPacket(Type.SKILLLEVEL, this, currentSkillLevel);
				return true;
			}
			return false;
		}
	}
	
	
	public void effect(LivingObject source, LivingObject target){
		source.getInterested().sendPacket(Type.EFFECT, source,target,this);
		target.getInterested().sendPacket(Type.EFFECT, source,target,this);
	}

	public void setCurrSecondRange(float currSecondRange) {
		this.currSecondRange = currSecondRange;
	}
	
	public abstract int getLevelRequirement(int level);

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxConsumn(int maxConsumn) {
		this.maxConsumn = maxConsumn;
	}

	public void setMaxFirstRange(int maxFirstRange) {
		this.maxFirstRange = maxFirstRange;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public void setMaxSecondRange(int maxSecondRange) {
		this.maxSecondRange = maxSecondRange;
	}

	public void setMinConsumn(int minConsumn) {
		this.minConsumn = minConsumn;
	}

	public void setMinFirstRange(int minFirstRange) {
		this.minFirstRange = minFirstRange;
	}

	public void setMinSecondRange(int minSecondRange) {
		this.minSecondRange = minSecondRange;
	}

	public void setRace(int race) {
		this.race = race;
	}

	public void setStatusUsed(int statusUsed) {
		this.statusUsed = statusUsed;
	}

	public void setType(int type) {
		this.type = type;
	}
}