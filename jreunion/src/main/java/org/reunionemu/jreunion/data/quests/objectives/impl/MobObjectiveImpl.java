package org.reunionemu.jreunion.data.quests.objectives.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.data.quests.objectives.MobObjective;
import org.reunionemu.jreunion.data.quests.objectives.ObjectiveImpl;

@XmlType(name="mob")
public class MobObjectiveImpl extends ObjectiveImpl implements MobObjective {
	
	@XmlAttribute(required=false)
	protected Integer amount;
	
	@XmlAttribute(required=true)
	protected Integer type;

	@Override
	public Integer getAmount() {
		return amount;
	}
	
	@Override
	public Integer getType() {
		return type;
	}

}
