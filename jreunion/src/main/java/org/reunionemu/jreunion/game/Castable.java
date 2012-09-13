package org.reunionemu.jreunion.game;

import java.util.List;

public interface Castable {
	
	public boolean cast(LivingObject caster, List<LivingObject> targets, int castStep);
	
}
