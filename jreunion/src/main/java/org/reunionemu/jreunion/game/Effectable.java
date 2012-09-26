package org.reunionemu.jreunion.game;

public interface Effectable {
	public void effect(LivingObject source, LivingObject target, String[] arguments);
	public int getEffectModifier();

}
