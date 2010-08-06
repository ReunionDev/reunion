package com.googlecode.reunion.jreunion.server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_RemoteAdmin {
	private class AdminThread extends Thread {
		private S_RemoteAdmin base;

		AdminThread(S_RemoteAdmin base) {
			this.base = base;
		}

		@Override
		public void run() {
			// TODO: Write Remote Admin Code
		}

	}

	private static S_RemoteAdmin remoteAdmin;

	static void enableRemoteAdmin() {
		if (remoteAdmin == null) {
			remoteAdmin = new S_RemoteAdmin();
			System.out.println("Started Remote Admin");

		} else {
			System.out.println("Remote Admin already running");

		}

	}

	private AdminThread adminThread;

	private S_RemoteAdmin() {
		super();
		adminThread = new AdminThread(this);
		adminThread.start();

	}

}
