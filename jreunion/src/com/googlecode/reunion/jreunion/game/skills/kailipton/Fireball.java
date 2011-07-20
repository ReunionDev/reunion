package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class Fireball extends Tier1 implements Castable,Effectable {

	public Fireball(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public boolean cast(LivingObject caster, LivingObject target) {
		if(caster instanceof Player){
			
			Player player = (Player)caster;
			int level = player.getSkillLevel(this);
			
			synchronized(target){
				
				return true;
			}
			
		}		
		return false;
	}

	public int getLevelRequirement(int level) {
		return 0+level;
	}
	
	public double getDamageModifier(){
		
		return 3.333333;
		
	}
	
	
}