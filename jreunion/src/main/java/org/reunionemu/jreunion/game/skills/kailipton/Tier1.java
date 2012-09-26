package org.reunionemu.jreunion.game.skills.kailipton;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.skills.GroupedSkill;
import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.SkillManager;

public class Tier1 extends GroupedSkill{
	
	public Tier1(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}
	
	@Override
	public int getMinLevel(){
		return 1;
	}

	@Override
	public int[] getSkillsInGroup() {
		return new int[]{3,4,12};
	}
	
	@Override
	public int getLevelRequirement(int level) {
		return 0+level;
	}
	
	@Override
	public int getAffectedTargets() {
		if(this instanceof FireBall){
			return 2;
		}
		else return 1;
	}
	
	@Override
	public List<LivingObject> getTargets(String[] arguments, LocalMap map){
		List<LivingObject> targets = new Vector<LivingObject>();
		targets.add(getSingleTarget(Integer.parseInt(arguments[3]), map));
		return targets;
	}
	
}
