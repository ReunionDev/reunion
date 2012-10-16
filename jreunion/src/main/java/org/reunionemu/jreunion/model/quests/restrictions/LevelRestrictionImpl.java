package org.reunionemu.jreunion.model.quests.restrictions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.quests.RestrictionImpl;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@XmlType(name = "level")
public class LevelRestrictionImpl extends RestrictionImpl implements
		LevelRestriction {

	@XmlAttribute(required = false)
	@XmlSchemaType(name = "positiveInteger")
	protected Integer min;

	@XmlAttribute(required = false)
	@XmlSchemaType(name = "positiveInteger")
	protected Integer max;

	@Override
	public Integer getMax() {
		return max;
	}

	@Override
	public Integer getMin() {
		return min;
	}

	@Override
	public boolean isAllowed(Player player) {
		Integer max = getMax();
		int level = player.getLevel();
		if (max != null && max < level) {
			return false;
		}
		Integer min = getMin();

		if (min != null && min > level) {
			return false;
		}
		return true;
	}

}
