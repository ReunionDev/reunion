package org.reunionemu.jreunion.model.quests;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.quests.objectives.ObjectiveState;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public interface Objective {
	public ObjectiveState createObjectiveState(QuestState state);
}
