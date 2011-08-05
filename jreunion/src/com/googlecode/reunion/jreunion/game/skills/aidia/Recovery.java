package com.googlecode.reunion.jreunion.game.skills.aidia;


import java.util.Iterator;
import java.util.List;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
public class Recovery extends Skill implements Castable{

	public Recovery(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 4+skillLevel;
	}
	
	public float getManaModifier(){
		/* mana spent:
		 * level 1 = 10
		 * level 2 = 10
		 * level 3 = 11
		 * ...
		 * level 25 = 30
		 */
		return 20f/(getMaxLevel()-1);
	}
	
	float getManaModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (10 + ((level-1) * getManaModifier()));			
		}	
		
		return modifier;
	}
	
	public float getHpModifier(){
		/* mana spent:
		 * level 1 = 40
		 * level 2 = 42
		 * level 3 = 45
		 * ...
		 * level 25 = 100
		 */
		return 60f/(getMaxLevel()-1);
	}
	
	float getHpModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (40 + ((level-1) * getManaModifier()));			
		}	
		
		return modifier;
	}

	//when casted, this skill will only send to the client
	//the new hp and mana values.
	@Override
	public boolean cast(LivingObject caster, List<LivingObject> victims) {

		Iterator<LivingObject> victimsIterator = victims.iterator();
		
		while(victimsIterator.hasNext()){
			LivingObject victim = (LivingObject)victimsIterator.next(); 
			if(victim == null && caster instanceof Player) {
				Player player = (Player)caster;
				int playerHp = player.getHp();
				if(playerHp < player.getMaxHp()) {
					int mana = (int) getManaModifier(player); //mana usage
					int hp = (int) getHpModifier(player); //hp recovery
					int playerMana = player.getMana();
					if(playerMana >= mana) {
						player.setMana(playerMana - mana);
						player.setHp(playerHp + hp);
						return true;
					}
				}
				return true;
			}
		}
		return false;
	}
}
