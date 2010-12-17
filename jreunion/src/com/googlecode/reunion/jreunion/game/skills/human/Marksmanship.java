package com.googlecode.reunion.jreunion.game.skills.human;
import com.googlecode.reunion.jreunion.game.Skill;
public class Marksmanship extends Skill {

	public Marksmanship(int id) {
		super(id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 49+skillLevel;
	}

}
