package com.googlecode.reunion.jreunion.game.skills.human;
import com.googlecode.reunion.jreunion.game.Skill;
public class SemiAutomatic extends Skill {

	public SemiAutomatic(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 4+skillLevel;
	}

}
