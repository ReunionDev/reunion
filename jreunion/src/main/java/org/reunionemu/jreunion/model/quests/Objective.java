package org.reunionemu.jreunion.model.quests;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.quests.objectives.ObjectiveState;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public interface Objective {
	public ObjectiveState createObjectiveState(QuestState state);
}
