package com.googlecode.reunion.jreunion.server;

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
			System.out.println("Started Remote Admin");

		} else {
			System.out.println("Remote Admin already running");

		}

	}

	private AdminThread adminThread;

	private RemoteAdmin() {
		super();
		adminThread = new AdminThread(this);
		adminThread.start();

	}

}
