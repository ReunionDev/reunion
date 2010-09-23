package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Server;

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

		baseDmg = (randDmg + getLevel() / 5 + getWis() / 2);

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

		newHp = mob.getCurrHp() - getBaseDmg(this);

		if (newHp <= 0) {

			mob.kill(this);

			updateStatus(12, getLvlUpExp() - mob.getExp(), 0);
			updateStatus(11, mob.getExp(), 0);
			updateStatus(10, mob.getLime(), 0);

			if (mob.getType() == 324) {
				Item item = com.googlecode.reunion.jreunion.server.ItemFactory
						.create(1054);

				item.setExtraStats((int) (Math.random() * 10000));

				pickupItem(item);
				getQuest().questEnd(this, 669);
				getQuest().questEff(this);
			}
		} else {
			mob.setCurrHp(newHp);
		}
	}

	private void meleeAttackPlayer(Player player) {

	}

	public void permanentSkill(Skill skill) {

	}

	public void skillAttackMob(Mob mob, Skill skill) {
		Client client = this.getClient();

		if (client == null) {
			return;
		}

		int newHp = mob.getCurrHp() - (int) skill.getCurrFirstRange();

		updateStatus(skill.getStatusUsed(),
				getCurrMana() - (int) skill.getCurrConsumn(), getMaxMana());

		if (newHp <= 0) {

			mob.kill(this);

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

		String packetData = "attack_vital npc " + mob.getId() + " "
				+ percentageHp + " 0 0\n";

		// S> attack_vital npc [NpcID] [RemainHP%] 0 0
				client.SendData( packetData);

			Iterator<WorldObject> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				Player pl =(Player) playerIter.next();

				client = pl.getClient();

				if (client == null) {
					continue;
				}

				packetData = "effect " + skill.getId() + " char "
						+ getId() + " npc " + mob.getId() + " "
						+ percentageHp + " 0 0\n";

				// S> effect [SkillID] char [charID] npc [npcID] [RemainNpcHP%]
				// 0 0
						client.SendData( packetData);
			}
		
	}

	public void skillAttackPlayer(Player player, Skill skill) {

	}

	@Override
	public void useSkill(LivingObject livingObject, int skillId) {

		Skill skill = getCharSkill().getSkill(skillId);

		if (skill.getType() == 0) {
			permanentSkill(skill);
		} else if (skill.getType() == 1) {
			activationSkill(skill);
		} else if (skill.getType() == 2) {
			attackSkill(livingObject, skill);
		}
	}
}