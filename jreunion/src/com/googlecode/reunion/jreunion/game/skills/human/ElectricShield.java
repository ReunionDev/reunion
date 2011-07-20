package com.googlecode.reunion.jreunion.game.skills.human;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
public class ElectricShield extends Skill {

	public ElectricShield(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 19+skillLevel;
	}

}
