package com.googlecode.reunion.jreunion.game.skills;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.Server;

public abstract class BasicAttack extends Skill {
	
	
	public BasicAttack(int id) {
		super(id);
	}
	
	public boolean attack(LivingObject attacker, LivingObject victim) {
		
		float damage = 0;
		if (attacker instanceof Player){
			Player player = (Player)attacker; 
			Weapon weapon = player.getEquipment().getMainHand();
			//damage = player.getBaseDamage();
			if(weapon!=null){
				int min = weapon.getMinDamage();
				int max = weapon.getMaxDamage();
				
				damage += min + (Server.getRand().nextFloat()*(max-min));
				if(!weapon.use(player)){
					return false;
				}
			}
		}
		synchronized(victim){
			
			victim.setHp((int)(victim.getHp()-damage));
		}		
		
		return true;
	}
}
