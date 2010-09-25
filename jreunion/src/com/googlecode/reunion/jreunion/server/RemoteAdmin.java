package com.googlecode.reunion.jreunion.server;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class RemoteAdmin {
	private class AdminThread extends Thread {
		private RemoteAdmin base;

		AdminThread(RemoteAdmin base) {
			this.base = base;
		}

		@Override
		public void run() {
			// TODO: Write Remote Admin Code
		}

	}

	private static RemoteAdmin remoteAdmin;

	static void enableRemoteAdmin() {
		if (remoteAdmin == null) {
			remoteAdmin = new RemoteAdmin();
			Logger.getLogger(RemoteAdmin.class).info("Started Remote Admin");

		} else {
			Logger.getLogger(RemoteAdmin.class).info("Remote Admin already running");

		}

	}

	private AdminThread adminThread;

	private RemoteAdmin() {
		super();
		adminThread = new AdminThread(this);
		adminThread.start();

	}

}
