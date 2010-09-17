package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Server;



/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_BulkanPlayer extends G_Player {

	public G_BulkanPlayer() {
		super();
	}

	public void activationSkill(G_Skill skill) {

	}

	public void attackSkill(G_LivingObject livingObject, G_Skill skill) {
		if (livingObject instanceof G_Mob) {
			skillAttackMob((G_Mob) livingObject, skill);
		} else if (livingObject instanceof G_Player) {
			skillAttackPlayer((G_Player) livingObject, skill);
		}
	}

	public int getBaseDmg() {
		int randDmg, baseDmg = 0;

		randDmg = getMinDmg()
				+ (int) (Math.random() * (getMaxDmg() - getMinDmg()));

		baseDmg = (randDmg + getLevel() / 6 + getStr() / 4 + getDexterity() / 4 + getConstitution() / 8);

		if (getEquipment().getMainHand() instanceof G_Sword) {
			baseDmg = (int) (baseDmg + baseDmg
					* (getCharSkill().getSkill(1).getCurrFirstRange() / 100));
		} else if (getEquipment().getMainHand() instanceof G_Axe) {
			baseDmg = (int) (baseDmg + baseDmg
					* (getCharSkill().getSkill(2).getCurrFirstRange() / 100));
		}
		;

		return baseDmg;
	}

	@Override
	public void meleeAttack(G_LivingObject livingObject) {
		if (livingObject instanceof G_Mob) {
			meleeAttackMob((G_Mob) livingObject);
		} else if (livingObject instanceof G_Player) {
			meleeAttackPlayer((G_Player) livingObject);
		}
	}

	private void meleeAttackMob(G_Mob mob) {

		int baseDmg = getBaseDmg();
		// S_Server.getInstance().getWorldModule().getWorldCommand().serverSay("BaseDmg:"+baseDmg);
		int newHp = mob.getCurrHp() - baseDmg;

		if (newHp <= 0) {

			mob.setDead(this);

			updateStatus(12, getLvlUpExp() - mob.getExp(), 0);
			updateStatus(11, mob.getExp(), 0);
			updateStatus(10, mob.getLime(), 0);

		/*	if (mob.getType() == 226) {
				
				G_Item item = com.googlecode.reunion.jreunion.server.S_ItemFactory
						.createItem(150);
				
				
				
				double random = Math.round(Math.random() * 10);	

				if (random == 0) {
					item.setGemNumber ((int) (Math.random() * 10000));
					item.setExtraStats((int) (Math.random() * 10000));
				}	
				
			//	dropItemMob(mob.getType(), item.getEntityId(),
			//			mob.getPosX(), mob.getPosY(), 0, 0, 0, 0, this);
				
			//	dropItem(item.getEntityId());
			  pickupItem(item.getEntityId());
			//	getQuest().questEnd(this, 669);
			//	getQuest().questEff(this);
			}*/
		} else {
			mob.setCurrHp(newHp);
		}
	}

	private void meleeAttackPlayer(G_Player player) {

	}

	public void permanentSkill(G_Skill skill) {

	}

	public void skillAttackMob(G_Mob mob, G_Skill skill) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		float baseDmg = getBaseDmg();
		float skillDmg = baseDmg;

		if (skill.getId() == 31) { // Whirlwind Slash Skill
			if (getEquipment().getMainHand() instanceof G_Sword) {
				skillDmg = baseDmg + baseDmg
						* (skill.getCurrFirstRange() / 100);
			}
		} else if (skill.getId() == 18) { // Overhead Blow Skill
			if (getEquipment().getMainHand() instanceof G_Axe) {
				skillDmg = baseDmg + baseDmg
						* (skill.getCurrFirstRange() / 100);
			}
		} else if (skill.getId() == 38) { // Exploding rage
			if (getEquipment().getMainHand() instanceof G_Axe) {
				skillDmg = baseDmg + baseDmg
						* (skill.getCurrFirstRange() / 100);
			}
		}

		// S_Server.getInstance().getWorldModule().getWorldCommand().serverSay("SkillDmg:"+skillDmg);
		int newHp = mob.getCurrHp() - (int) skillDmg;

		updateStatus(skill.getStatusUsed(),
				getCurrStm() - (int) skill.getCurrConsumn(), getMaxStm());

		if (newHp <= 0) {

			mob.setDead(this);

			updateStatus(12, getLvlUpExp() - mob.getExp(), 0);
			updateStatus(11, mob.getExp(), 0);
			updateStatus(10, mob.getLime(), 0);
		} else {
			mob.setCurrHp(newHp);
		}

		int percentageHp = mob.getCurrHp() * 100 / mob.getMaxHp();

		if (percentageHp == 0 && mob.getCurrHp() > 0) {
			percentageHp = 1;
		}

		String packetData = "attack_vital npc " + mob.getEntityId() + " "
				+ percentageHp + " 0 0\n";

		// S> attack_vital npc [NpcID] [RemainHP%] 0 0
				client.SendData( packetData);
		/*
		if (getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				G_Player pl = playerIter.next();

				client = S_Server.getInstance().getNetworkModule()
						.getClient(pl);

				if (client == null) {
					continue;
				}

				packetData = "effect " + skill.getId() + " char "
						+ getEntityId() + " npc " + mob.getEntityId() + " "
						+ percentageHp + " 0 0\n";

				// S> effect [SkillID] char [charID] npc [npcID] [RemainNpcHP%]
				// 0 0
						client.SendData( packetData);
			}
		}
		*/
	}

	public void skillAttackPlayer(G_Player player, G_Skill skill) {

	}

	@Override
	public void useSkill(G_LivingObject livingObject, int skillId) {

		G_Skill skill = getCharSkill().getSkill(skillId);

		if (skill.getType() == 0) {
			permanentSkill(skill);
		} else if (skill.getType() == 1) {
			activationSkill(skill);
		} else if (skill.getType() == 2) {
			attackSkill(livingObject, skill);
		}
	}
}