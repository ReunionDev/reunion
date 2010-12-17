package com.googlecode.reunion.jreunion.game.skills.bulkan;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Axe;
public class OverheadBlow extends WeaponAttack {

	public OverheadBlow(int id) {
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
		return Axe.class;
	}

}
