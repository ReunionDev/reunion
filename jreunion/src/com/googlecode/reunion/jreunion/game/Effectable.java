package com.googlecode.reunion.jreunion.game;

public interface Effectable {
	public void effect(LivingObject source, LivingObject target);
	public int getEffectModifier();

}
