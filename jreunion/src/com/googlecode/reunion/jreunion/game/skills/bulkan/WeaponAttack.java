package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.SkillManager;

public abstract class WeaponAttack extends Skill {
	
	
	public WeaponAttack(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public abstract Class<?> getWeaponType();

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 44+skillLevel;
	}
}
