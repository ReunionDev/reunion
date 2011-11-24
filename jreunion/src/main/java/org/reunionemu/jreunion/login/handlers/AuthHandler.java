package org.reunionemu.jreunion.login.handlers;

import java.util.LinkedList;
import java.util.List;

import org.reunionemu.jreunion.server.packets.Packet;
import org.reunionemu.jreunion.server.packets.login.LoginPacket;
import org.reunionemu.jreunion.server.packets.login.PlayPacket;

public class AuthHandler extends LoginHandler {

	@Override
	List<Class<? extends Packet>> register() {
		List<Class<? extends Packet>> types = new LinkedList<Class<? extends Packet>>();
		types.add(LoginPacket.class);
		types.add(PlayPacket.class);
		
		return types;
	}
}
