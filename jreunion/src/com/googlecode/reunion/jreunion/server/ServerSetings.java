package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jcommon.ParsedItem;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ServerSetings {

	private float xp;

	private float lime;
	
	private float startLime;
	
	private float defaultMapId;
	
	private float defaultVersion;
	
	private float sessionRadius;
	
	private float dropExclusivity;
	
	private String welcomeMessage;
	
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
		ParsedItem server = Reference.getInstance().getServerReference().getItem("Server");

		if (server == null) {
			// cant find Item in the reference continue to load defaults:
			setXp(1);
			setLime(1);
			setStartLime(100);
			setDefaultMapId(4);
			setDefaultVersion(2000);
			setSessionRadius(300);
			setDropExclusivity(10);
			setWelcomeMessage("Hey, welcome on the Reunion Testserver");
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
			if (server.checkMembers(new String[] { "DefaultMap" })) {
				// use member from file
				setDefaultMapId(Float.parseFloat(server.getMemberValue("DefaultMap")));
			} else {
				// use default
				setDefaultMapId(4);
			}
			if (server.checkMembers(new String[] { "StartLime" })) {
				// use member from file
				setStartLime(Float.parseFloat(server.getMemberValue("StartLime")));
			} else {
				// use default
				setStartLime(100);
			}
			if (server.checkMembers(new String[] { "Version" })) {
				// use member from file
				setDefaultVersion(Float.parseFloat(server.getMemberValue("Version")));
			} else {
				// use default
				setDefaultVersion(2000);
			}
			if (server.checkMembers(new String[] { "WelcomeMsg" })) {
				// use member from file
				setWelcomeMessage(server.getMemberValue("WelcomeMsg"));
			} else {
				// use default
				setWelcomeMessage("Hey, welcome on the Reunion Testserver");
			}
			if (server.checkMembers(new String[] { "SessionRadius" })) {
				// use member from file
				setSessionRadius(Float.parseFloat(server.getMemberValue("SessionRadius")));
			} else {
				// use default
				setSessionRadius(300);
			}
			if (server.checkMembers(new String[] { "DropExclusivity" })) {
				// use member from file
				setDropExclusivity(Float.parseFloat(server.getMemberValue("lime")));
			} else {
				// use default
				setDropExclusivity(10);
			}
		}
	}

	public void setLime(float lime) {
		this.lime = lime;
	}

	public void setXp(float xp) {
		this.xp = xp;
	}

	public float getStartLime() {
		return startLime;
	}

	public void setStartLime(float startLime) {
		this.startLime = startLime;
	}

	public float getDefaultMapId() {
		return defaultMapId;
	}

	public void setDefaultMapId(float defaultMapId) {
		this.defaultMapId = defaultMapId;
	}

	public float getDefaultVersion() {
		return defaultVersion;
	}

	public void setDefaultVersion(float defaultVersion) {
		this.defaultVersion = defaultVersion;
	}

	public float getSessionRadius() {
		return sessionRadius;
	}

	public void setSessionRadius(float sessionRadius) {
		this.sessionRadius = sessionRadius;
	}

	public float getDropExclusivity() {
		return dropExclusivity;
	}

	public void setDropExclusivity(float dropExclusivity) {
		this.dropExclusivity = dropExclusivity;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}
}
