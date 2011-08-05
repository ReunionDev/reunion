package com.googlecode.reunion.jreunion.game.skills.kailipton;

import java.util.List;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.KailiptonPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.Tools;


public class ReturnToVillage extends Skill implements Castable {

	public ReturnToVillage(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 34 + skillLevel;
	}
	
	public float getSuccessRateModifier(){
		/* level 1 = 20%
		 * level 2 = 40%
		 * ...
		 * level 5 = 100%
		 */
		
		return 0.2f;
		
	}
	
	public float getSuccessRateModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0.2f+((level-1)*getSuccessRateModifier()));			
		}	
		
		return modifier;
	}
	
	//Teleports player to town.
	//If there isn't enough mana then the client wont allow to use the skill.
	@Override
	public boolean cast(LivingObject caster, List<LivingObject> victims) {
		if(caster instanceof KailiptonPlayer){
			Player player = (Player)caster;
			int manaSpent = 10; //mana spent is the same in every skill level
			
			player.setMana(player.getMana() - manaSpent);
			
			// calculate Success Rate of skill
			if(!Tools.successRateCalc(getSuccessRateModifier((Player)caster))){
				player.getClient().sendPacket(Type.SAY, "Failed to Return to Village.");
				return false;
			}
			
			player.spawn();
			return true;
		}
		return false;
	}
}