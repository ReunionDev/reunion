package org.reunionemu.jreunion.game.skills.kailipton;

import org.reunionemu.jreunion.game.skills.Modifier;
import org.reunionemu.jreunion.server.SkillManager;

public class StoneMastery extends Mastery {

	public StoneMastery(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType(){
		return Modifier.ValueType.EARTH;
	}
}