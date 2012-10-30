package org.reunionemu.jreunion.model.quests.objectives;

import org.reunionemu.jreunion.model.quests.Objective;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public interface MobObjective extends Objective {
	public Integer getAmount();

	Integer getType();
}
