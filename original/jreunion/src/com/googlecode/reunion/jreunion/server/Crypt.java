package com.googlecode.reunion.jreunion.server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Crypt {
	private static Crypt _instance = null;

	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new Crypt();
		}
	}

	public static Crypt getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}

	public Crypt() {

	}

	public char[] C2Sdecrypt(byte encdata[]) {
		char decdata[] = new char[encdata.length];
		for (int i = 0; i < encdata.length; i++) {
			decdata[i] = (char) ((char) (encdata[i] - 15) % 256);
		}
		return decdata;

	}

	public byte[] S2Cencrypt(char decdata[]) {

		byte encdata[] = new byte[decdata.length];

		for (int i = 0; i < decdata.length; i++) {
			encdata[i] = (byte) ((decdata[i] ^ 0xc3) + 0x0f);
		}
		return encdata;
	}
}
