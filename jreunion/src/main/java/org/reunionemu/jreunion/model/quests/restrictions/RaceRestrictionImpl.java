package org.reunionemu.jreunion.model.quests.restrictions;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.reunionemu.jreunion.model.quests.RestrictionImpl;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@XmlType(name="race")
public class RaceRestrictionImpl extends RestrictionImpl implements RaceRestriction {
	
	@XmlValue()
	protected Integer id;

	@Override
	public Integer getId() {
		return id;
	}


}
