package org.reunionemu.jreunion.game.skills.human;

import java.util.List;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.HumanPlayer;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.Tools;
import org.reunionemu.jreunion.server.PacketFactory.Type;

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
	//when casted, this skill only consume the item "Gem Cutting Kit".
	//TODO: Consume the "Gem Cutting Kit" item, and exchange brute stone with perfect stone.
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
