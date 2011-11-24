package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class FireMastery extends Mastery {

	public FireMastery(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType() {
		return Modifier.ValueType.FIRE;
	}
}