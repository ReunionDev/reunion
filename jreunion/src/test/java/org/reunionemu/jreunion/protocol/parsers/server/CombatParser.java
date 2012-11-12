package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;

import netty.Packet;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Client;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.packets.server.CombatPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
@Client
public class CombatParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^combat(?: (\\d+))? (1|0)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		CombatPacket packet = new CombatPacket();
		int n = 0;
		String id = match.group(++n);
		if (id != null) {
			packet.setId(Long.parseLong(id));
		}
		packet.setInCombat(match.group(++n).equals("1"));

		return packet;
	}

}
