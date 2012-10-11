package org.reunionemu.jreunion.data.quests.rewards.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.data.quests.rewards.ItemReward;

@XmlType(name="item")
public class ItemRewardImpl extends RewardImpl implements ItemReward {
	
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
