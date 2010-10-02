package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Axe;
public class AxeMastery extends WeaponMastery{

	public AxeMastery(int id)  {
		super(id);
		
	}

	@Override
	public Class getWeaponType() {
		return Axe.class;
	}

	

	

}
