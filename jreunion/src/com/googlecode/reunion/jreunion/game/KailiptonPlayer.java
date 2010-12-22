package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Tools;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class KailiptonPlayer extends Player {

	public KailiptonPlayer(Client client) {
		super(client);
	}

	public void activationSkill(Skill skill) {

	}

	public void attackSkill(LivingObject livingObject, Skill skill) {
		if (livingObject instanceof Mob) {
			skillAttackMob((Mob) livingObject, skill);
		} else if (livingObject instanceof Player) {
			skillAttackPlayer((Player) livingObject, skill);
		}
	}

	public int getBaseDmg(Player player) {
		int baseDmg, randDmg;

		randDmg = player.getMinDmg()
				+ (int) (Math.random() * (player.getMaxDmg() - player
						.getMinDmg()));

		baseDmg = (randDmg + getLevel() / 5 + getWisdom() / 2);

		return baseDmg;
	}

	@Override
	public void meleeAttack(LivingObject livingObject) {
		if (livingObject instanceof Mob) {
			meleeAttackMob((Mob) livingObject);
		} else if (livingObject instanceof Player) {
			meleeAttackPlayer((Player) livingObject);
		}
	}

	private void meleeAttackMob(Mob mob) {
		int newHp;

		newHp = mob.getHp() - getBaseDmg(this);

		if (newHp <= 0) {

			mob.kill(this);

			if (mob.getType() == 324) {
				Item item = ItemFactory.create(1054);

				item.setExtraStats((int) (Math.random() * 10000));
				
				getInventory().addItem(item);
				//pickupItem(item);
				getQuest().questEnd(this, 669);
				getQuest().questEff(this);
			}
		} else {
			mob.setHp(newHp);
		}
	}

	private void meleeAttackPlayer(Player player) {

	}

	public void permanentSkill(Skill skill) {

	}

	public void skillAttackMob(Mob mob, Skill skill) {
		/*
		Client client = this.getClient();

		if (client == null) {
			return;
		}

		int newHp = mob.getHp() - (int) skill.getCurrFirstRange();

		updateStatus(skill.getStatusUsed(),
				getMana() - (int) skill.getCurrConsumn(), getMaxMana());

		if (newHp <= 0) {

			mob.kill(this);

			updateStatus(12, getLvlUpExp() - mob.getExp(), 0);
			updateStatus(11, mob.getExp(), 0);
			updateStatus(10, mob.getLime(), 0);
		} else {
			mob.setHp(newHp);
		}
		
		String packetData = "attack_vital npc " + mob.getId() + " "
				+ mob.getPercentageHp() + " 0 0\n";

		// S> attack_vital npc [NpcID] [RemainHP%] 0 0
		client.sendData( packetData);
		
		getInterested().sendPacket(Type.EFFECT, this, mob, skill);
*/
	}
	
	public void skillAttackPlayer(Player player, Skill skill) {

	}

	@Override
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
	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 80) +(getLeadership() / 2);
	}
	
	@Override
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 80) + Tools.statCalc(getConstitution(), 40)+ (getLeadership() / 2);		
	}
	
	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 30) + (getLeadership() / 2);		
	}
	
	public int getMaxStamina(){
		return getStrength() + (getLeadership() / 2);
	}

	@Override
	public int getBaseDamage() {
		return (getLevel()/ 5) + (getWisdom() / 2);
	}
}