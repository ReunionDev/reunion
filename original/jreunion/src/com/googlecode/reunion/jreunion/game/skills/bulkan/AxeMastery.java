package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.items.equipment.Axe;
import com.googlecode.reunion.jreunion.server.SkillManager;
public class AxeMastery extends WeaponMastery{

	public AxeMastery(SkillManager skillManager,int id)  {
		super(skillManager,id);
		
	}

	@Override
	public Class<?> getWeaponType() {
		return Axe.class;
	}


	

}
