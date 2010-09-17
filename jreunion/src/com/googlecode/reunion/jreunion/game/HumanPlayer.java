package com.googlecode.reunion.jreunion.game;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class HumanPlayer extends Player {

	public HumanPlayer() {
		super();
	}

	public int getBaseDmg(Player player) {
		int randDmg, baseDmg = 0;

		randDmg = player.getMinDmg()
				+ (int) (Math.random() * (player.getMaxDmg() - player
						.getMinDmg()));

		baseDmg = (randDmg + getLevel() / 6 + getStr() + getDexterity() / 4);

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

		if (getEquipment().getMainHand() != null) {
			getEquipment().getMainHand().consumn(this);
		}

		if (newHp <= 0) {

			mob.setDead(this);

			updateStatus(12, getLvlUpExp() - mob.getExp(), 0);
			updateStatus(11, mob.getExp(), 0);
			updateStatus(10, mob.getLime(), 0);

			if (mob.getType() == 324) {
				Item item = com.googlecode.reunion.jreunion.server.ItemFactory
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

	private void meleeAttackPlayer(Player player) {

	}

	@Override
	public void useSkill(LivingObject livingObject, int skillId) {

	}
}