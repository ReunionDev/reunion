package org.reunionemu.jreunion.model.quests.restrictions;

import org.reunionemu.jreunion.model.quests.Restriction;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public interface LevelRestriction extends Restriction {
	public Integer getMax();
	
	public Integer getMin();

}
