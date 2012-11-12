package org.reunionemu.jreunion.protocol.old;

import org.reunionemu.jreunion.server.Client;

public class DefaultProtocol extends Protocol 
{
	public DefaultProtocol(Client client) {
		super(client);
	}

	public String decryptServer(byte data[]) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte)(data[i] - 15);
		}
		return new String(data);
	}
	
	public byte[] encryptServer(String data) {

		byte [] buffer = new byte[data.length()];
		for (int i = 0; i < data.length(); i++) {
			buffer[i] = (byte) ((data.charAt(i) ^ 0xc3) + 0x0f);
		}
		return buffer;
	}
}
