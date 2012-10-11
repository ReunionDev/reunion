package org.reunionemu.jreunion.data.quests.objectives.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.data.quests.objectives.ObjectiveImpl;
import org.reunionemu.jreunion.data.quests.objectives.PointsObjective;

@XmlType(name="points")
public class PointsObjectiveImpl extends ObjectiveImpl implements PointsObjective {
	
	@XmlAttribute(required=true)
	protected Integer amount;

	@Override
	public Integer getAmount() {
		return amount;
	}

}
