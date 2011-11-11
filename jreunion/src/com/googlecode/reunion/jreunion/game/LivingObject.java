package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.game.quests.ExperienceQuest;
import com.googlecode.reunion.jreunion.game.quests.QuestState;
import com.googlecode.reunion.jreunion.game.quests.objective.Objective;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Tools;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class LivingObject extends WorldObject {

	private LivingObject target;
	
	private Position targetPosition;
	
	private String name;

	public Position getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Position targetPosition) {
		this.targetPosition = targetPosition;
	}
	
	public int getPercentageHp() {
		
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
	
	private boolean running;

	public void setIsRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}

	private int hp;

	private int maxHp;

	private int level;

	public LivingObject() {
		super();
	}

	public int getHp() {
		return hp;
	}

	public int getLevel() {
		return level;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public LivingObject getTarget() {
		return target;
	}

	public void loadFromReference(int id) {

	}

	public void setHp(int hp) {
		this.hp = Tools.between(hp, 0, getMaxHp());		
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public void setTarget(LivingObject target) {
		this.target = target;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String livingObjectName){
		this.name = livingObjectName;
	}
	
	public void getsAttacked(Player player, int damage){
		
		//Cursed quest Boss packet
		if(this instanceof Mob){
			Mob mob = (Mob)this;
			QuestState questState = player.getQuestState();
		
			if(questState != null){
				Quest quest = questState.getQuest();
				if(quest instanceof ExperienceQuest){
					Objective objective = quest.getObjective(mob.getType());
					if(objective != null){
							if(questState.getProgression(objective.getId()) == (objective.getAmmount()-1)){
								if(!mob.getIsBoss()){
									player.getClient().sendPacket(Type.QT, "king "+mob.getEntityId()+" 1");
									mob.setBoss();
								}
							}
					}
				}
			}
		}
		
		int newHp = getHp() - damage;				
		
		if (newHp <= 0) {
			if(this instanceof Mob)
				((Mob)this).kill(player);
		} else {
			setHp(newHp);
			this.getInterested().sendPacket(Type.ATTACK_VITAL, this);
		}
	}
}