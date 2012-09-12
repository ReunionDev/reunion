package org.reunionemu.jreunion.game.skills.human;

import org.reunionemu.jreunion.server.SkillManager;

public class Marksmanship extends GunMastery {

	public Marksmanship(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 4+skillLevel;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}
	
}
