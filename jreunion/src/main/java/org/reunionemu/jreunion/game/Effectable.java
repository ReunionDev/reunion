package org.reunionemu.jreunion.game;

public interface Effectable {
	public void effect(LivingObject source, LivingObject target, int castStep);
	public int getEffectModifier();

}
