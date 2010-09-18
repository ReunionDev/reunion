package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jcommon.ParsedItem;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ServerSetings {

	private float xp;

	private float lime;

	public ServerSetings() {
		loadFromReference();
	}

	public float getLime() {
		return lime;
	}

	public float getXp() {
		return xp;
	}

	public void loadFromReference() {
		ParsedItem server = Reference.getInstance().getServerReference()
				.getItem("Server");

		if (server == null) {
			// cant find Item in the reference continue to load defaults:
			setXp(1);
			setLime(1);
		} else {

			if (server.checkMembers(new String[] { "xp" })) {
				// use member from file
				setXp(Float.parseFloat(server.getMemberValue("xp")));
			} else {
				// use default
				setXp(1);
			}
			if (server.checkMembers(new String[] { "lime" })) {
				// use member from file
				setLime(Float.parseFloat(server.getMemberValue("lime")));
			} else {
				// use default
				setLime(1);
			}
		}
	}

	public void setLime(float lime) {
		this.lime = lime;
	}

	public void setXp(float xp) {
		this.xp = xp;
	}
}
