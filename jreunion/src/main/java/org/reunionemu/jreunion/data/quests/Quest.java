package org.reunionemu.jreunion.data.quests;

import java.util.List;

import javax.xml.bind.annotation.XmlType;



public interface Quest {
	
	int getId();
	String getDescription();
	String getName();
	List<Restriction> getRestrictions();
	List<Reward> getRewards();
	List<Objective> getObjectives();

}
