package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class RecoveryBoost extends Skill {

	public RecoveryBoost(SkillManager skillManager, int id) {
		super(skillManager, id);
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 4+skillLevel;
	}

	
	public float getRecoveryBoostModifier(Player player){
		return (1 + (player.getSkillLevel(this) * getRecoveryBoostModifier()));
	}
	
	public float getRecoveryBoostModifier(){
		/* level 1 = 5%
		 * level 2 = 10%
		 * ...
		 * level 5 = 25%
		 */
		return 0.05f;
	}
	
}
