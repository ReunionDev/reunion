package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.KailiptonPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;


public class ManaShield extends Skill implements Castable {

	public ManaShield(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 14 + skillLevel;
	}
	
	//TODO
	public float getSuccessRateModifier(){
		/* level 1 = 
		 * level 2 = 
		 * ...
		 * level 25 = 
		 */
		
		return 0f;
		
	}
	
	//TODO
	public float getSuccessRateModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0f+((level-1)*getSuccessRateModifier()));			
		}	
		
		return modifier;
	}
	
	public float getManaModifier(){
		/* mana spent:
		 * level 1 = 15
		 * level 2 = 15
		 * level 3 = 15
		 * level 4 = 16
		 * ...
		 * level 25 = 25
		 */
		return 10f/(getMaxLevel()-1);
	}
	
	float getManaModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (15 + ((level-1) * getManaModifier()));			
			}	
		
		return modifier;
	}
	
	@Override
	//TODO:
	public boolean cast(LivingObject caster, LivingObject... targets) {
		if(caster instanceof KailiptonPlayer){
			return true;
		}
			
		return false;
	}
}