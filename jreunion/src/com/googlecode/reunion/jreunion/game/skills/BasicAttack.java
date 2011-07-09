package com.googlecode.reunion.jreunion.game.skills;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;

public abstract class BasicAttack extends Skill {
	
	
	public BasicAttack(int id) {
		super(id);
	}
	
	public boolean attack(LivingObject attacker, LivingObject victim) {
		
		
		
		return false;
	}
}
