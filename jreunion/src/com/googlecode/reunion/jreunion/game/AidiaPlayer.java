package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Tools;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class AidiaPlayer extends Player {

	public AidiaPlayer(Client client) {
		super(client);
	}

	public int getBaseDmg(Player player) {
		int baseDmg, randDmg;

		randDmg = player.getMinDmg()
				+ (int) (Math.random() * (player.getMaxDmg() - player
						.getMinDmg()));

		baseDmg = (randDmg + getLevel() / 5 + getWisdom() / 3 + getLeadership());

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

		if (getEquipment().getMainHand() != null) {
			getEquipment().getMainHand().consumn(this);
		}

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

	@Override
	public void useSkill(LivingObject livingObject, int skillId) {

	}
	
	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 80) +(getLeadership() / 2);
	}
	
	@Override
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 80) + Tools.statCalc(getConstitution(), 40)+((getLeadership() / 2) * 5);		
	}
	
	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 30) + ((getLeadership() / 2) * 5);		
	}
	
	public int getMaxStamina(){
		return getStrength() + (getLeadership() / 2);
	}

	@Override
	int getBaseDamage() {
		
		return (getLevel() / 5) + (getWisdom() / 3) + getLeadership();
	}
}