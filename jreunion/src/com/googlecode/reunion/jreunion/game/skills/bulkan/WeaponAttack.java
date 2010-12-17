package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;

public abstract class WeaponAttack extends Skill {
	
	
	public WeaponAttack(int id) {
		super(id);
	}
	
	public abstract Class<?> getWeaponType();

}
