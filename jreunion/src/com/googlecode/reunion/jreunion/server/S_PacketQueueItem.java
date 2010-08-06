package com.googlecode.reunion.jreunion.server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_PacketQueueItem {
	int networkId;

	char[] packetData;

	byte[] packetBytes;

	String packetString;

	public S_PacketQueueItem(int networkId, String data) {
		super();
		this.networkId = networkId;
		packetString = data;
		packetBytes = null;

	}

	public void addData(String append) {
		if (packetString.length() == 0) {
			packetString = new String(append);
		}
		if (!packetString.endsWith("\n")) {
			packetString += "\n";
		}
		packetString += append;
		if (!packetString.endsWith("\n")) {
			packetString += "\n";
		}

	}

	public void Encrypt() {
		packetBytes = S_Crypt.getInstance().S2Cencrypt(
				packetString.toCharArray());
	}

	public byte[] getBytes() {
		if (packetBytes == null) {
			Encrypt();
		}
		return packetBytes;
	}

	public String getData() {
		return packetString;
	}

	/**
	 * @return Returns the networkId.
	 * @uml.property name="networkId"
	 */
	public int getNetworkId() {
		return networkId;
	}

}
