package org.reunionemu.jreunion.game.skills;

import java.util.List;

import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Skill;

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
