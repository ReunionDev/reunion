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

	private int targetPosX;

	private int targetPosY;

	private int targetPosZ;

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
		return stm;
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

	public int getTargetPosX() {
		return targetPosX;
	}

	public int getTargetPosY() {
		return targetPosY;
	}

	public int getTargetPosZ() {
		return targetPosZ;
	}

	public void loadFromReference(int id) {

		ParsedItem mob = Reference.getInstance().getMobReference()
				.getItemById(id);

		if (mob == null) {
			// cant find Item in the reference continue to load defaults:
			setHp(1);
			setMaxHp(1);
			setLevel(1);
		} else {

			if (mob.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setHp(Integer.parseInt(mob.getMemberValue("Hp")));
			} else {
				// use default
				setHp(1);
			}
			if (mob.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setMaxHp(Integer.parseInt(mob.getMemberValue("Hp")));
			} else {
				// use default
				setMaxHp(1);
			}
			if (mob.checkMembers(new String[] { "Level" })) {
				// use member from file
				setLevel(Integer.parseInt(mob.getMemberValue("Level")));
			} else {
				// use default
				setLevel(1);
			}
		}
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

		// Client client =
		// Server.getInstance().networkModule.getClient(this.getPlayerSession().getPlayer(this.getEntityId()));
		// if(client.clientState == 10)
		// Server.getInstance().networkModule.SendPacket(client.networkId,"status
		// 2 "+this.getPlayerCurrStm()+" "+this.getPlayerMaxStm());
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

	public void setTargetPosX(int targetPosX) {
		this.targetPosX = targetPosX;
	}

	public void setTargetPosY(int targetPosY) {
		this.targetPosY = targetPosY;
	}

	public void setTargetPosZ(int targetPosZ) {
		this.targetPosZ = targetPosZ;
	}


	
}