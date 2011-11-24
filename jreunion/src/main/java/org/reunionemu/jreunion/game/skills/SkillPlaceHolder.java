package org.reunionemu.jreunion.game.skills;

import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;

public class SkillPlaceHolder extends Skill{

	public SkillPlaceHolder(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int level) {
		return 0;
	}

}
