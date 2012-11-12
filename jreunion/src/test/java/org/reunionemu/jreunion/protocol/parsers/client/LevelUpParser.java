package org.reunionemu.jreunion.protocol.parsers.client;

import java.util.regex.*;


import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.old.*;
import org.reunionemu.jreunion.protocol.packets.server.LevelUpPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class LevelUpParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^levelup (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		LevelUpPacket packet = new LevelUpPacket();
		int n = 0;

		packet.setId(Integer.parseInt(match.group(++n)));

		return packet;
	}

}
