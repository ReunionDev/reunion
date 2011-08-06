package com.googlecode.reunion.jreunion.server.packets;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FailPacket extends SessionPacket implements ClientSerializator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FailPacket(String message){
		this.message = message;
	}
	
	String message;
	
	@Override
	public List<String> readClientPacket() {
		StringBuffer sb = new StringBuffer();
		sb.append("fail ");
		sb.append(message);
		return new LinkedList<String>(Arrays.asList(new String []{sb.toString()}));}

}
