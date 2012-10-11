package org.reunionemu.jreunion.model.quests.restrictions;

import org.reunionemu.jreunion.model.quests.Restriction;

public interface LevelRestriction extends Restriction {
	public Integer getMax();
	
	public Integer getMin();

}
