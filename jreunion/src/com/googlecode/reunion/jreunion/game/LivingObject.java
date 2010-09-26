package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Session;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class LivingObject extends WorldObject {

	private int team;

	private LivingObject target;
	
	
	private Position targetPosition;

	public Position getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Position targetPosition) {
		this.targetPosition = targetPosition;
	}

	private int hp;

	private int maxHp;

	private int mana;

	private int maxMana;

	private int elect;

	private int maxElect;

	private int stm;

	private int maxStm;

	private int level;

	public LivingObject() {
		super();
	}

	public int getElect() {
		return elect;
	}

	public int getHp() {
		return hp;
	}

	public int getStm() {
		return stm;
	}

	public int getLevel() {
		return level;
	}

	public int getMaxElect() {
		return maxElect;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public int getMana() {
		return mana;
	}

	public int getMaxStm() {
		return maxStm;
	}

	public LivingObject getTarget() {
		return target;
	}

	public void loadFromReference(int id) {

	}

	public void setCurrElect(int currElect) {
		this.elect = currElect;
	}

	public void setHp(int currHp) {
		this.hp = currHp;
		
		
	}

	public void setCurrMana(int currMana) {
		this.mana = currMana;
	}

	public void setCurrStm(int currStm) {
		this.stm = currStm;

	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxElect(int maxElect) {
		this.maxElect = maxElect;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public void setMaxStm(int maxStm) {
		this.maxStm = maxStm;
	}

	public void setTarget(LivingObject target) {
		this.target = target;
	}

}