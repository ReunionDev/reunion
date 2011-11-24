package org.reunionemu.jreunion.game.skills.kailipton;

import org.reunionemu.jreunion.game.skills.Modifier;
import org.reunionemu.jreunion.server.SkillManager;

public class LightningMastery extends Mastery {

	public LightningMastery(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType(){
		return Modifier.ValueType.LIGHT;
	}
}