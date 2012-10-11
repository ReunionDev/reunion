package org.reunionemu.jreunion.model.quests.objectives;

import org.reunionemu.jreunion.model.quests.Objective;

public interface MobObjective extends Objective {
	public Integer getAmount();

	Integer getType();
}
