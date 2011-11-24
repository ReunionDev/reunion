package com.googlecode.reunion.jreunion.game.skills;

import java.util.List;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Skill;

public interface Modifier {
	
	public enum ValueType{
		DAMAGE,
		FIRE,
		LIGHT,
		EARTH
	}
	public enum ModifierType{
		ADDITIVE,
		MULTIPLICATIVE
	}
	
	public boolean getCondition(LivingObject owner);
	
	public ValueType getValueType();
	
	public ModifierType getModifierType();
	
	public List<Skill> getAffectedSkills();
	
	public float getModifier(LivingObject owner);

}
