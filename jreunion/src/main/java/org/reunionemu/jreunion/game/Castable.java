package org.reunionemu.jreunion.game;


public interface Castable {
	
	public boolean cast(LivingObject caster, LivingObject victim, String[] arguments);
}
