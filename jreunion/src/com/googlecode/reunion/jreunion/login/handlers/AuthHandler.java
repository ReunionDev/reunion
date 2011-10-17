package com.googlecode.reunion.jreunion.login.handlers;

import java.util.LinkedList;
import java.util.List;

import com.googlecode.reunion.jreunion.server.packets.Packet;
import com.googlecode.reunion.jreunion.server.packets.login.LoginPacket;
import com.googlecode.reunion.jreunion.server.packets.login.PlayPacket;

public class AuthHandler extends LoginHandler {

	@Override
	List<Class<? extends Packet>> register() {
		List<Class<? extends Packet>> types = new LinkedList<Class<? extends Packet>>();
		types.add(LoginPacket.class);
		types.add(PlayPacket.class);
		
		return types;
	}
}
