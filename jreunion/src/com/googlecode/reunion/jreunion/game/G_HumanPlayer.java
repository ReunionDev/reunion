package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_HumanPlayer extends G_Player {

	public G_HumanPlayer() {
		super();
	}

	public int getBaseDmg(G_Player player) {
		int randDmg, baseDmg = 0;

		randDmg = player.getMinDmg()
				+ (int) (Math.random() * (player.getMaxDmg() - player
						.getMinDmg()));

		baseDmg = (randDmg + getLevel() / 6 + getStr() + getDex() / 4);

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
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
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

		if (getEquipment().getFirstHand() != null) {
			getEquipment().getFirstHand().consumn(this);
		}

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

	@Override
	public void useSkill(G_LivingObject livingObject, int skillId) {

	}
}