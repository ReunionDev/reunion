package org.reunionemu.jreunion.data.quests.restrictions;

import org.reunionemu.jreunion.data.quests.Restriction;

public interface LevelRestriction extends Restriction {
	public Integer getMax();
	
	public Integer getMin();

}
