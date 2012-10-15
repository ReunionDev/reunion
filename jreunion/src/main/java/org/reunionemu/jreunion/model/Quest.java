package org.reunionemu.jreunion.model;

import java.util.List;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.quests.Objective;
import org.reunionemu.jreunion.model.quests.Restriction;
import org.reunionemu.jreunion.model.quests.Reward;

public interface Quest {
	
	int getId();
	String getDescription();
	String getName();
	List<Restriction> getRestrictions();
	List<Reward> getRewards();
	List<Objective> getObjectives();
	boolean isAllowed(Player player);

}
