package org.reunionemu.jreunion.data.quests.objectives.impl;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.reunionemu.jreunion.data.quests.objectives.ObjectiveImpl;
import org.reunionemu.jreunion.data.quests.objectives.PointsObjective;

@XmlType(name="points")
public class PointsObjectiveImpl extends ObjectiveImpl implements PointsObjective {
	
	@XmlValue
	protected Integer points;

	@Override
	public Integer getPoints() {
		return points;
	}

}
