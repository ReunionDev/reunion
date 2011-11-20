package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jcommon.ParsedItem;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ServerSetings {

	private long xp;

	private long lime;
	
	private long startLime;
	
	private long defaultMapId;
	
	private long defaultVersion;
	
	private long sessionRadius;
	
	private long dropExclusivity;
	
	private String welcomeMessage;
	
	private long spawnAttempts;
	
	private long dropTimeOut;
	
	public ServerSetings() {
		loadFromReference();
	}

	public long getLime() {
		return lime;
	}

	public long getXp() {
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
			setSpawnAttempts(1);
			setDropTimeOut(600);
			setWelcomeMessage("Hey, welcome on the Reunion Testserver");
		} else {

			if (server.checkMembers(new String[] { "xp" })) {
				// use member from file
				setXp(Long.parseLong(server.getMemberValue("xp")));
			} else {
				// use default
				setXp(1);
			}
			if (server.checkMembers(new String[] { "lime" })) {
				// use member from file
				setLime(Long.parseLong(server.getMemberValue("lime")));
			} else {
				// use default
				setLime(1);
			}
			if (server.checkMembers(new String[] { "DefaultMap" })) {
				// use member from file
				setDefaultMapId(Long.parseLong(server.getMemberValue("DefaultMap")));
			} else {
				// use default
				setDefaultMapId(4);
			}
			if (server.checkMembers(new String[] { "StartLime" })) {
				// use member from file
				setStartLime(Long.parseLong(server.getMemberValue("StartLime")));
			} else {
				// use default
				setStartLime(100);
			}
			if (server.checkMembers(new String[] { "Version" })) {
				// use member from file
				setDefaultVersion(Long.parseLong(server.getMemberValue("Version")));
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
				setSessionRadius(Long.parseLong(server.getMemberValue("SessionRadius")));
			} else {
				// use default
				setSessionRadius(300);
			}
			if (server.checkMembers(new String[] { "DropExclusivity" })) {
				// use member from file
				setDropExclusivity(Long.parseLong(server.getMemberValue("DropExclusivity")));
			} else {
				// use default
				setDropExclusivity(10);
			}
			if (server.checkMembers(new String[] { "SpawnAttempts" })) {
				// use member from file
				setSpawnAttempts(Long.parseLong(server.getMemberValue("SpawnAttempts")));
			} else {
				// use default
				setSpawnAttempts(1);
			}
			if (server.checkMembers(new String[] { "DropTimeOut" })) {
				// use member from file
				setDropTimeOut(Long.parseLong(server.getMemberValue("DropTimeOut")));
			} else {
				// use default
				setDropTimeOut(600);
			}
		}
	}

	public void setLime(long lime) {
		this.lime = lime;
	}

	public void setXp(long xp) {
		this.xp = xp;
	}

	public long getStartLime() {
		return startLime;
	}

	public void setStartLime(long startLime) {
		this.startLime = startLime;
	}

	public long getDefaultMapId() {
		return defaultMapId;
	}

	public void setDefaultMapId(long defaultMapId) {
		this.defaultMapId = defaultMapId;
	}

	public long getDefaultVersion() {
		return defaultVersion;
	}

	public void setDefaultVersion(long defaultVersion) {
		this.defaultVersion = defaultVersion;
	}

	public long getSessionRadius() {
		return sessionRadius;
	}

	public void setSessionRadius(long sessionRadius) {
		this.sessionRadius = sessionRadius;
	}

	public long getDropExclusivity() {
		return dropExclusivity;
	}

	public void setDropExclusivity(long dropExclusivity) {
		this.dropExclusivity = dropExclusivity;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	public long getSpawnAttempts() {
		return spawnAttempts;
	}

	public void setSpawnAttempts(long spawnAttempts) {
		this.spawnAttempts = spawnAttempts;
	}

	public long getDropTimeOut() {
		return dropTimeOut;
	}

	public void setDropTimeOut(long dropTimeOut) {
		this.dropTimeOut = dropTimeOut;
	}
}
