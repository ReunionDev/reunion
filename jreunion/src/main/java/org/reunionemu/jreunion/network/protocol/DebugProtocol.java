package org.reunionemu.jreunion.network.protocol;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import org.reunionemu.jreunion.protocol.OtherProtocol;
import org.reunionemu.jreunion.server.Client;

public class DebugProtocol extends Protocol 
{
	public DebugProtocol() {
		super();
	}

	public List<String> decryptServer(byte data[]) {
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
	
		return packet.getBytes();
	}
}
