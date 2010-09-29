package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.skills.GroupedSkill;

public class Tier1 extends GroupedSkill{
	
	public Tier1(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}


	@Override
	protected int[] getSkillsInGroup() {
		return new int[]{3,4,12};
	}
	
	

	@Override
	public int getLevelRequirement(int level) {
		return level;
	}
}
