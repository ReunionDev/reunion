package org.reunionemu.jreunion.model.quests.rewards;

import org.reunionemu.jreunion.model.quests.Reward;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public interface ItemReward extends Reward {
	public Integer getAmount();

	Integer getType();

	Long getExtraStats();
}
