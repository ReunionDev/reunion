package org.reunionemu.jreunion.model.quests.restrictions;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.quests.RestrictionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@Configurable
@XmlType(name="norepeat")
public class RepeatRestrictionImpl extends RestrictionImpl implements RepeatRestriction {
	
	@XmlTransient
	@Autowired
	QuestDao questDao;
	
	@Override
	public boolean isAllowed(Player player) {
		//TODO: Implement repeatable
		return true;
	}

}
