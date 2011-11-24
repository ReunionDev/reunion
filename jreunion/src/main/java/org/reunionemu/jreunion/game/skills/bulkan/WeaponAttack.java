package org.reunionemu.jreunion.game.skills.bulkan;

import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;

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
