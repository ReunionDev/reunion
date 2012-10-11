package org.reunionemu.jreunion.data.quests.objectives;

import org.reunionemu.jreunion.data.quests.Objective;

public interface MobObjective extends Objective {
	public Integer getAmount();

	Integer getType();
}
