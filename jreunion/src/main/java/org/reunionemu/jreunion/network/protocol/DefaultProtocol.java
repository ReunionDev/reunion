package org.reunionemu.jreunion.network.protocol;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.reunionemu.jreunion.protocol.OtherProtocol;
import org.reunionemu.jreunion.server.Client;

public class DefaultProtocol extends Protocol 
{
	public DefaultProtocol() {
		super();
	}

	public List<String> decryptServer(byte data[]) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte)(data[i] - 15);
		}
		return new LinkedList<String>(Arrays.asList(new String(data).split("\n")));
	}
	
	public String combine(Iterable<?> input){
		StringBuffer sb = new StringBuffer();
		Iterator<?> iter = input.iterator();
		while(iter.hasNext()){
			sb.append(String.valueOf(iter.next()));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public byte[] encryptServer(List<String> packets) {
		
		String packet = combine(packets);
		
		byte [] buffer = new byte[packet.length()];
		for (int i = 0; i < packet.length(); i++) {
			buffer[i] = (byte) ((packet.charAt(i) ^ 0xc3) + 0x0f);
		}
		return buffer;
	}
}
