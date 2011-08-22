package com.googlecode.reunion.jreunion.game.skills.aidia;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class BeastSummonSkill extends RingWeaponMastery {

	public BeastSummonSkill(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 44+skillLevel;
	}
}
