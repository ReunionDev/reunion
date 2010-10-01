package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Session;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

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
	
	public int getPercentageHp(){
		
		double percentageHp = this.getHp() * 100 / this.getMaxHp();
		if (percentageHp > 0 && percentageHp < 1) {
			percentageHp = 1;
		}
		return (int) percentageHp;		
	}
	
	public void walk(Position position, boolean running) {

		setIsRunning(running);
		synchronized(this) {
			setPosition(position);
			setTargetPosition(position.clone());			
		}
		getInterested().sendPacket(Type.WALK, this, position);
				
	}
	
	private int dmgType;
	
	public int getDmgType() {
		return dmgType;
	}

	public void setDmgType(int dmgType) {
		this.dmgType = dmgType;
	}
	
	private boolean running; // 0 - Off; 1 - On

	public void setIsRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
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

	public void setElect(int currElect) {
		this.elect = currElect;
	}

	public void setHp(int currHp) {
		this.hp = currHp;		
	}

	public void setMana(int currMana) {
		this.mana = currMana;
	}

	public void setStm(int currStm) {
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