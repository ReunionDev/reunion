package org.reunionemu.jreunion.data.quests.rewards.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.data.quests.rewards.LimeReward;

@XmlType(name="lime")
public class LimeRewardImpl extends RewardImpl implements LimeReward {
	
	@XmlAttribute(required=true)
	protected Integer amount;

	@Override
	public Integer getAmount() {
		return amount;
	}

}
