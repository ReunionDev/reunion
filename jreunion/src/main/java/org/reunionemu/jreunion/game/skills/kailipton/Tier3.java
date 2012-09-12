package org.reunionemu.jreunion.game.skills.kailipton;

import org.reunionemu.jreunion.game.skills.GroupedSkill;
import org.reunionemu.jreunion.server.SkillManager;

public class Tier3 extends GroupedSkill{
	
	public Tier3(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int[] getSkillsInGroup() {
		return new int[]{26,27,28};
	}

	@Override
	public int getLevelRequirement(int level) {
		return 49+level;
	}
	
	@Override
	public int getAffectedTargets() {
		if(this instanceof StarFlare){
			return 5;
		}
		else return 1;
	}
}
