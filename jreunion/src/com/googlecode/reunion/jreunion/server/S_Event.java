package com.googlecode.reunion.jreunion.server;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_Entity;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Event {

	private int callTimes;
	private List<G_Entity> targetList = new Vector<G_Entity>();
	private G_Entity caller;

	public S_Event() {
		super();

	}

}
