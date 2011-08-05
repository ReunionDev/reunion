package com.googlecode.reunion.jreunion.server.packets;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StubPacket extends Packet implements ClientSerializator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StubPacket(String packet){
		this.packet = packet;
	}
	
	String packet;
	
	@Override
	public List<String> readClientPacket() {
		return new LinkedList<String>(Arrays.asList(new String []{packet}));}

}
