package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Tools;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class HybriderPlayer extends Player {

	public HybriderPlayer(Client client) {
		super(client);
		
	}
	
	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 50) +(getLeadership() / 2);
	}
	
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 60) + Tools.statCalc(getConstitution(), 30)+ (getLeadership() / 2);		
	}

	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 55) + (getLeadership() / 2);		
	}
	
	public int getMaxStamina(){
		return Tools.statCalc(getStrength(), 90) + (getLeadership() / 2);		
		
	}
	
	public int getBaseDmg(Player player) {
		int randDmg, baseDmg = 0;

		randDmg = player.getMinDmg()
				+ (int) (Math.random() * (player.getMaxDmg() - player
						.getMinDmg()));

		baseDmg = (randDmg + getLevel() / 6 + getStrength() / 4 + getDexterity() / 4 + getConstitution() / 8);

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

		int newHp = mob.getHp() - getBaseDmg(this);

		if (newHp <= 0) {

			mob.kill(this);

			if (mob.getType() == 324) {
				Item item = com.googlecode.reunion.jreunion.server.ItemFactory
						.create(1054);

				item.setExtraStats((int) (Math.random() * 10000));

				//pickupItem(item);
				getInventory().addItem(item);
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

	@Override
	public float getBaseDamage() {
		return (getLevel() / 6) + (getStrength() / 5) + (getWisdom()/ 4) + (getDexterity() / 3);
	}
	
}