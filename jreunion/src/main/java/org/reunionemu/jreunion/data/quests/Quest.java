package org.reunionemu.jreunion.data.quests;

import java.util.List;

import javax.xml.bind.annotation.XmlType;


@XmlType
public interface Quest {

	List<Reward> getRewards();
	List<Objective> getObjectives();
	int getId();
	String getDescription();
	String getName();

}
