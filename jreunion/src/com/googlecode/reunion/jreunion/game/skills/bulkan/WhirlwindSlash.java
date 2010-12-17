package com.googlecode.reunion.jreunion.game.skills.bulkan;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Sword;
public class WhirlwindSlash extends WeaponAttack {

	public WhirlwindSlash(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 44+skillLevel;
	}
	
	@Override
	public Class<?> getWeaponType() {
		return Sword.class;
	}


}
