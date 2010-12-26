package com.googlecode.reunion.jreunion.game.skills.aidia;
import com.googlecode.reunion.jreunion.game.Skill;
public class BugSummonSkill extends Skill {

	public BugSummonSkill(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return skillLevel;
	}
}
