package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class StoneMastery extends Mastery {

	public StoneMastery(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType(){
		return Modifier.ValueType.EARTH;
	}
}