package org.reunionemu.jreunion.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.SkillManager;
import org.slf4j.LoggerFactory;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Skill {
	private int id;

	private int type; //0 - Passive; 1 - Activation; 2 - Attack 
	
	private String name;
	
	private SkillManager skillManager;

	public Skill(SkillManager skillManager, int id) {
		this.setSkillManager(skillManager);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public abstract int  getMaxLevel();
	
	public abstract int  getAffectedTargets();
	
	public int getMinLevel(){
		return 0;
	}

	public int getType() {
		return type;
	}
	
	public void reset(Player player){
		
		int skillLevel = player.getSkillLevel(this);
		
		//if skill current level is zero, there is no need to reset it.
		if(skillLevel == 0)
			return;
		
		int min = getMinLevel();
		player.setSkillLevel(this, min);
		player.getClient().sendPacket(Type.SKILLLEVEL, this, min);
		player.setStatusPoints(player.getStatusPoints() + skillLevel - min);
	}	
	
	public void handle(Player player, String [] arguments){
		
		Client client = player.getClient();
		LivingObject target = null;
		int entityId=-1;
		int castStep = 1;
		
		boolean againstNpc = arguments[2].equals("npc") || arguments[2].equals("n");
		boolean againstChar = arguments[2].equals("char") || arguments[2].equals("c");
		
		boolean selfSkill = !againstNpc && !againstChar; 
		
		if(!selfSkill) { //if length=3 then player is using skill on himself
			
			entityId = Integer.parseInt(arguments[3]);
			
			if(againstChar) {
				client.sendPacket(Type.SAY, "No PVP implemented yet!");
			}
		 
			//SELF: use_skill 36 1
			//attack char 44 0
			//Other: use_skill 74 char 44 64
			
			target = (LivingObject) player.getPosition().getLocalMap().getEntity(entityId);
		}
		else {
			target = player;
			
		}
		
		//if is a stepable skill, get the current step
		if(arguments.length == 5 && this.getAffectedTargets() == 1 ){
			castStep = Integer.parseInt(arguments[4]);
		}
		List<LivingObject> victims = new ArrayList<LivingObject>(Arrays.asList(new LivingObject[]{target}));
		
		if(arguments.length > 4 && this.getAffectedTargets() > 1){ //multiple targets
			for(int messageIndex=4; messageIndex < arguments.length; messageIndex++){
				entityId = Integer.parseInt(arguments[messageIndex]);
				target = (LivingObject) player.getPosition().getLocalMap().getEntity(entityId);
				victims.add(target);
			}
		}
		
		//cast attacks/skills and send effects to other clients.
		if(Castable.class.isInstance(this)){
			if(((Castable)this).cast(player, victims, castStep)){
				if(Effectable.class.isInstance(this))
					for(LivingObject victim : victims){
						this.effect(player, victim, castStep);
					}
			}
		} else {
			client.sendPacket(Type.SAY, this.getName()+" skill not implemented yet!");
			LoggerFactory.getLogger(Skill.class).error(this.getName()+" skill is not Castable!");
		}
		
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
	
	public void effect(LivingObject source, LivingObject target, int castStep){
		if(target == source){ //self usable skill
			//TODO: figure out why this is not working
			//source.getInterested().sendPacket(Type.SKILL, source, this);
			((Player)source).getClient().sendPacket(Type.SKILL, source, this);
		}
		else {
			source.getInterested().sendPacket(Type.EFFECT, source, target, this, 0, 0, 0);
			target.getInterested().sendPacket(Type.EFFECT, source, target, this, 0, 0, 0);
		}
	}
	
	public abstract int getLevelRequirement(int skillLevel);	

	public void setType(int type) {
		this.type = type;
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}

	private void setSkillManager(SkillManager skillManager) {
		this.skillManager = skillManager;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String skillName){
		this.name = skillName;
	}
}