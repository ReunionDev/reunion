package org.reunionemu.jreunion.protocol.parsers.client;

import java.util.regex.*;


import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.packets.server.SkyPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class SkyParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^sky (\\d+) (1|0)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		SkyPacket packet = new SkyPacket();
		int n = 0;

		packet.setId(Integer.parseInt(match.group(++n)));

		packet.setFlyStatus(Integer.parseInt(match.group(++n)) == 1);

		return packet;
	}

}
