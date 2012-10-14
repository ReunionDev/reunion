package org.reunionemu.jreunion.game.skills.human;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;

public class GemCutting extends Skill {

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
	
	@Override
	public int getAffectedTargets() {
		return 1;
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
}
