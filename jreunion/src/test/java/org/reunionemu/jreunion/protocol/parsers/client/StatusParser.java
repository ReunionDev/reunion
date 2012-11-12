package org.reunionemu.jreunion.protocol.parsers.client;

import java.util.regex.*;


import org.reunionemu.jreunion.game.Player.Status;
import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.packets.server.StatusPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class StatusParser implements PacketParser {

	static final Pattern regex = Pattern
			.compile("^status (\\d+) (\\d+) (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		StatusPacket packet = new StatusPacket();
		int n = 0;
		packet.setStatusType(Status.byValue(Integer.parseInt(match.group(++n))));
		packet.setValue(Long.parseLong(match.group(++n)));
		packet.setMax(Long.parseLong(match.group(++n)));
		return packet;
	}

}
