package com.googlecode.reunion.jreunion.server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_PacketFactory {

	public static final int PT_VERSION_ERROR = 1001;
	public static final int PT_OK = 1002;

	public static String createPacket(int packettype, Object... arg) {
		switch (packettype) {
		case PT_VERSION_ERROR: {
			if (arg.length == 1) {
				String clientVersion = (String) arg[0];
				String requiredVersion = String.valueOf(S_DatabaseUtils
						.getInstance().getVersion());
				return "fail Wrong clientversion: current version "
						+ clientVersion + " required version "
						+ requiredVersion + "\n";
			}

			return "fail Wrong clientversion.\n";

		}
		case PT_OK: {
			return "OK\n";
		}

		}
		return null;
	}

	public S_PacketFactory() {
		super();

	}

}
