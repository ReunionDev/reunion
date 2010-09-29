package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;

public class Fireball extends Tier1 implements Castable,Effectable {

	public Fireball(int id) {
		super(id);
	}

	@Override
	public void cast(LivingObject caster, LivingObject target) {
		if(caster instanceof Player){
			
			Player player = (Player)caster;
			int level = player.getSkillLevel(this);
			
			synchronized(target){
				
				
			}
			
		}		
	}

	
	
	
}