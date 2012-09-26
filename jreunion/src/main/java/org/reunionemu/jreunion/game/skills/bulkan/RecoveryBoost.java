package org.reunionemu.jreunion.game.skills.bulkan;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;

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

	@Override
	public int getAffectedTargets() {
		return 1;
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
