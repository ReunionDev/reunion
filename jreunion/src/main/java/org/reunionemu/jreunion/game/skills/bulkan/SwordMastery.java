package org.reunionemu.jreunion.game.skills.bulkan;

import org.reunionemu.jreunion.game.items.equipment.Sword;
import org.reunionemu.jreunion.server.SkillManager;


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
