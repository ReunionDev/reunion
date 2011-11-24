package com.googlecode.reunion.jreunion.game.skills.aidia;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
public class Teleport extends Skill {

	public Teleport(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 10;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 54+skillLevel;
	}

}
