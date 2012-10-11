package org.reunionemu.jreunion.data.quests.restrictions.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.data.quests.restrictions.LevelRestriction;

@XmlType(name="level")
public class LevelRestrictionImpl extends RestrictionImpl implements LevelRestriction {
	
	@XmlAttribute(required=false)
	@XmlSchemaType(name = "positiveInteger")
	protected Integer min;
	
	@XmlAttribute(required=false)
	@XmlSchemaType(name = "positiveInteger")
	protected Integer max;

	@Override
	public Integer getMax() {
		return min;
	}

	@Override
	public Integer getMin() {
		return max;
	}

}
