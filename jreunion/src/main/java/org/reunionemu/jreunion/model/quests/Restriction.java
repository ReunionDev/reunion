package org.reunionemu.jreunion.model.quests;

import org.reunionemu.jreunion.game.Player;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public interface Restriction {

	boolean isAllowed(Player player);

}
