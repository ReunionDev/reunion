package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.skills.GroupedSkill;

public class Tier3 extends GroupedSkill{
	
	public Tier3(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	protected int[] getSkillsInGroup() {
		return new int[]{26,27,28};
	}

	@Override
	public int getLevelRequirement(int level) {
		return 49+level;
	}
}
