package org.reunionemu.jreunion.data.quests.rewards;

import org.reunionemu.jreunion.data.quests.Reward;

public interface ItemReward extends Reward {
	public Integer getAmount();

	Integer getType();
}
