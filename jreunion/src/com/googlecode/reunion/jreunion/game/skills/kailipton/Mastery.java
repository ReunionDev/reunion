package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.skills.GroupedSkill;

public class Mastery extends GroupedSkill{
	
	public Mastery(int id) {
		super(id);
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
