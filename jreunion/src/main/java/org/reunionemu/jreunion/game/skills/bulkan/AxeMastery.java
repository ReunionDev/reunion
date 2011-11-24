package org.reunionemu.jreunion.game.skills.bulkan;

import org.reunionemu.jreunion.game.items.equipment.Axe;
import org.reunionemu.jreunion.server.SkillManager;
public class AxeMastery extends WeaponMastery{

	public AxeMastery(SkillManager skillManager,int id)  {
		super(skillManager,id);
		
	}

	@Override
	public Class<?> getWeaponType() {
		return Axe.class;
	}


	

}
