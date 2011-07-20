package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.skills.GroupedSkill;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class Mastery extends GroupedSkill{
	
	public Mastery(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int level) {
		return 24+level;
	}

	@Override
	protected int[] getSkillsInGroup() {
		return new int[]{8,11,14};
	}
}
