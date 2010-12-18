package com.googlecode.reunion.jreunion.game.skills.aidia;
import com.googlecode.reunion.jreunion.game.Skill;
public class Teleport extends Skill {

	public Teleport(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 10;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 54+skillLevel;
	}

}
