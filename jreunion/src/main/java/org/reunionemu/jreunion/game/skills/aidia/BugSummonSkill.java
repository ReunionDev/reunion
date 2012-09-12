package org.reunionemu.jreunion.game.skills.aidia;

import org.reunionemu.jreunion.server.SkillManager;

public class BugSummonSkill extends RingWeaponMastery {

	public BugSummonSkill(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return skillLevel;
	}
}
