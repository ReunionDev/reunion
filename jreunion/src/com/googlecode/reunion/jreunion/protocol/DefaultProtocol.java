package com.googlecode.reunion.jreunion.protocol;

import com.googlecode.reunion.jreunion.server.Client;

public class DefaultProtocol extends Protocol 
{
	public String decryptServer(Client client, byte data[]) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte)(data[i] - 15);
		}
		return new String(data);
	}
	
	public byte[] encryptServer(Client client, String data) {

		byte [] buffer = new byte[data.length()];
		for (int i = 0; i < data.length(); i++) {
			buffer[i] = (byte) ((data.charAt(i) ^ 0xc3) + 0x0f);
		}
		return buffer;
	}
}
