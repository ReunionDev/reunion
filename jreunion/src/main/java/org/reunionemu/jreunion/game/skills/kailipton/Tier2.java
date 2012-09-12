package org.reunionemu.jreunion.game.skills.kailipton;

import org.reunionemu.jreunion.game.skills.GroupedSkill;
import org.reunionemu.jreunion.server.SkillManager;

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
	
	@Override
	public int getAffectedTargets() {
		if(this instanceof FirePillar){
			return 4;
		}
		else return 1;
	}
}
