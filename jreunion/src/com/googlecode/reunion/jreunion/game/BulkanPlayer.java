package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Tools;



/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class BulkanPlayer extends Player {

	public BulkanPlayer(Client client) {
		super(client);
	}

	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 50) +(getLeadership() / 2);
	}
	
	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 80) + (getLeadership() / 2);		
	}
	
	@Override
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 50) + Tools.statCalc(getConstitution(), 20)+ (getLeadership() / 2);		
	}
	
	public int getMaxStamina(){
		return Tools.statCalc(getStrength(), 60) + (getLeadership() / 2);		
		
	}
		
	
	@Deprecated()
	public void meleeAttack(LivingObject livingObject) {
		if (livingObject instanceof Mob) {
			meleeAttackMob((Mob) livingObject);
		} else if (livingObject instanceof Player) {
			meleeAttackPlayer((Player) livingObject);
		}
	}
	
	@Deprecated()
	private void meleeAttackMob(Mob mob) {

	}
	
	@Deprecated()
	private void meleeAttackPlayer(Player player) {

	}

	@Deprecated()
	public void attackSkill(LivingObject livingObject, Skill skill) {

	}
	
	@Deprecated()
	public void permanentSkill(Skill skill) {

	}

	@Deprecated()
	public void activationSkill(Skill skill) {

	}
	
	@Deprecated()
	public void skillAttackPlayer(Player player, Skill skill) {

	}

	@Deprecated()
	public void useSkill(LivingObject livingObject, int skillId) {

		Skill skill = getPosition().getLocalMap().getWorld().getSkillManager().getSkill(skillId);

		if (skill.getType() == 0) {
			permanentSkill(skill);
		} else if (skill.getType() == 1) {
			activationSkill(skill);
		} else if (skill.getType() == 2) {
			attackSkill(livingObject, skill);
		}
	}

	
	@Override
	public float getBaseDamage() {
		return (getLevel()/6) + (getStrength()/4) + (getDexterity()/4) + (getConstitution()/8);
	}
}