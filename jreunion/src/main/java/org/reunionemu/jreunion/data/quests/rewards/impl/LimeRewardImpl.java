package org.reunionemu.jreunion.data.quests.rewards.impl;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.reunionemu.jreunion.data.quests.rewards.LimeReward;

@XmlType(name="lime")
public class LimeRewardImpl extends RewardImpl implements LimeReward {
	
	@XmlValue
	protected Integer lime;

	@Override
	public Integer getLime() {
		return lime;
	}

}
