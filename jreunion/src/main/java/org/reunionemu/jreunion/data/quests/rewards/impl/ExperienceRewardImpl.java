package org.reunionemu.jreunion.data.quests.rewards.impl;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.reunionemu.jreunion.data.quests.rewards.ExperienceReward;

@XmlType(name="experience")
public class ExperienceRewardImpl extends RewardImpl implements ExperienceReward {
	
	@XmlValue
	protected Integer experience;

	@Override
	public Integer getExperience() {
		return experience;
	}

}
