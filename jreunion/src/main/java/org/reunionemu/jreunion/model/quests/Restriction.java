package org.reunionemu.jreunion.model.quests;

import org.reunionemu.jreunion.game.Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public interface Restriction {

	boolean isAllowed(Player player);

}
