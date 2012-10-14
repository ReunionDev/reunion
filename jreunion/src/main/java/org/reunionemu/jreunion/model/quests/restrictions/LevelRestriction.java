package org.reunionemu.jreunion.model.quests.restrictions;

import org.reunionemu.jreunion.model.quests.Restriction;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public interface LevelRestriction extends Restriction {
	public Integer getMax();
	
	public Integer getMin();

}
