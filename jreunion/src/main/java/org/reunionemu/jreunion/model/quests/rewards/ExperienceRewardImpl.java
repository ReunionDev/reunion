package org.reunionemu.jreunion.model.quests.rewards;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.reunionemu.jreunion.model.quests.RewardImpl;

@XmlType(name="experience")
public class ExperienceRewardImpl extends RewardImpl implements ExperienceReward {
	
	@XmlValue
	protected Integer experience;

	@Override
	public Integer getExperience() {
		return experience;
	}

}
