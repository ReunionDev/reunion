package com.googlecode.reunion.jreunion.game.skills.human;

import java.util.List;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.HumanPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.Tools;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class GemCutting extends Skill implements Castable{

	public GemCutting(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 19+skillLevel;
	}

	public float getSuccessRateModifier(){
		/* level 1 = 5%
		 * level 2 = 40%
		 * ...
		 * level 25 = 95%
		 */
		
		return 0.9f / (getMaxLevel() - 1);
		
	}
	
	public float getSuccessRateModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0.05f+((level-1)*getSuccessRateModifier()));			
		}	
		
		return modifier;
	}
	
	//Cuts brut stones existing in the player exchange window.
	//when casted, this skill don't consume any of the player status.
	//TODO: Consume the "Gem Cutting Kit" item, and exchange brute stone with usable stone.
	@Override
	public boolean cast(LivingObject caster, List<LivingObject> victims) {
		if(caster instanceof HumanPlayer){
			Player player = (Player)caster;
			
			// calculate Success Rate of skill
			if(!Tools.successRateCalc(getSuccessRateModifier((Player)caster))){
				//TODO: Consume the "Gem Cutting Kit" item
				player.getClient().sendPacket(Type.SAY, "Failed to cut the stone.");
				return false;
			}
			//TODO: Consume the "Gem Cutting Kit" item, and exchange brute stone with usable stone. 
			
		}
		return false;
	}
}
