package org.reunionemu.jreunion.game.skills.bulkan;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.SkillManager;

public abstract class WeaponAttack extends Skill {
	
	
	public WeaponAttack(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public abstract Class<?> getWeaponType();

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 44+skillLevel;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}
	
	@Override
	public List<LivingObject> getTargets(String[] arguments, LocalMap map){
		List<LivingObject> targets = new Vector<LivingObject>();
		targets.add(getSingleTarget(Integer.parseInt(arguments[3]), map));
		return targets;
	}
}
