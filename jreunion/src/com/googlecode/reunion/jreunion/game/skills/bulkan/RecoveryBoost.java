package com.googlecode.reunion.jreunion.game.skills.bulkan;
import com.googlecode.reunion.jreunion.game.Skill;
public class RecoveryBoost extends Skill {

	public RecoveryBoost(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 24+skillLevel;
	}

}
