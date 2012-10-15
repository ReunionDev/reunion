package org.reunionemu.jreunion.model.quests.objectives;

import org.reunionemu.jreunion.model.quests.Objective;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public interface ObjectiveState {
	
	public Objective getObjective();
	
	public boolean isComplete();

}
