package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.skills.GroupedSkill;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class Tier2 extends GroupedSkill{
	
	public Tier2(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int[] getSkillsInGroup() {
		return new int[]{5,10,13};
	}

	@Override
	public int getLevelRequirement(int level) {
		return 37+level;
	}
}
