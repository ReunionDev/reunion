package org.reunionemu.jreunion.model.quests.restrictions;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.quests.RestrictionImpl;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
@XmlType(name="race")
public class RaceRestrictionImpl extends RestrictionImpl implements RaceRestriction {
	
	@XmlValue()
	protected Integer id;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public boolean isAllowed(Player player) {
		Integer id = getId();
		if(id!=null && player.getRace().value() == id){
			return false;
		}
		return true;
	}

}
