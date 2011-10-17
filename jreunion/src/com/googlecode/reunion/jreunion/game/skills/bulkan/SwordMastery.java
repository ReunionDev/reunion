package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.items.equipment.Sword;
import com.googlecode.reunion.jreunion.server.SkillManager;


public class SwordMastery extends WeaponMastery {

	public SwordMastery(SkillManager skillManager,int id) {
		super(skillManager,id);
		// TODO Auto-generated constructor stub
	}
	@Override
	public Class<?> getWeaponType() {
		return Sword.class;
	}
}
