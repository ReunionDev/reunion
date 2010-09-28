package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.Sword;


public class SwordMastery extends WeaponMastery {

	public SwordMastery(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Class getWeaponType() {
		return Sword.class;
	}

}
