package org.reunionemu.jreunion.model.quests.rewards;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.reunionemu.jreunion.model.quests.RewardImpl;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
@XmlType(name="lime")
public class LimeRewardImpl extends RewardImpl implements LimeReward {
	
	@XmlValue
	protected Integer lime;

	@Override
	public Integer getLime() {
		return lime;
	}

}
