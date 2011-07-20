package com.googlecode.reunion.jreunion.game.skills.bulkan;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
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

		//ToDo
	public double getRecoveryBoostModifier(Player player){
		return 0;
	}
	
	public double getRecoveryBoostModifier(){
		return 0.05;
	}
	
}
