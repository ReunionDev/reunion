package org.reunionemu.jreunion.data.quests.rewards.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.data.quests.rewards.ExperienceReward;

@XmlType(name="experience")
public class ExperienceRewardImpl extends RewardImpl implements ExperienceReward {
	
	@XmlAttribute(required=true)
	protected Integer amount;

	@Override
	public Integer getAmount() {
		return amount;
	}

}
