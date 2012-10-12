package org.reunionemu.jreunion.model.quests.rewards;

import org.reunionemu.jreunion.model.quests.Reward;

public interface ItemReward extends Reward {
	public Integer getAmount();

	Integer getType();

	Long getExtraStats();
}
