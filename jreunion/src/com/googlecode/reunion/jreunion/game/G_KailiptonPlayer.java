package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_KailiptonPlayer extends G_Player {

	public G_KailiptonPlayer() {
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

	public int getBaseDmg(G_Player player) {
		int baseDmg, randDmg;

		randDmg = player.getMinDmg()
				+ (int) (Math.random() * (player.getMaxDmg() - player
						.getMinDmg()));

		baseDmg = (randDmg + getLevel() / 5 + getWis() / 2);

		return baseDmg;
	}

	@Override
	public void levelUpSkill(G_Skill skill) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		String packetData = new String();

		getCharSkill().incSkill(this, skill);
		packetData = "skilllevel " + skill.getId() + " " + skill.getCurrLevel()
				+ "\n";
				client.SendData( packetData);
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
		int newHp;

		newHp = mob.getCurrHp() - getBaseDmg(this);

		if (newHp <= 0) {

			mob.setDead();

			updateStatus(12, getLvlUpExp() - mob.getExp(), 0);
			updateStatus(11, mob.getExp(), 0);
			updateStatus(10, mob.getLime(), 0);

			if (mob.getType() == 324) {
				G_Item item = com.googlecode.reunion.jreunion.server.S_ItemFactory
						.createItem(1054);

				item.setExtraStats((int) (Math.random() * 10000));

				pickupItem(item.getEntityId());
				getQuest().questEnd(this, 669);
				getQuest().questEff(this);
			}
		} else {
			mob.setCurrHp(newHp);
		}
	}

	private void meleeAttackPlayer(G_Player player) {

	}

	public void permanentSkill(G_Skill skill) {

	}

	public void skillAttackMob(G_Mob mob, G_Skill skill) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		int newHp = mob.getCurrHp() - (int) skill.getCurrFirstRange();

		updateStatus(skill.getStatusUsed(),
				getCurrMana() - (int) skill.getCurrConsumn(), getMaxMana());

		if (newHp <= 0) {

			mob.setDead();

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