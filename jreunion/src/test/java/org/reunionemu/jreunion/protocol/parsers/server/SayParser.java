package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.packets.client.SayPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.*;

@Component("serverSayParser")
@Scope("prototype")
@Server
public class SayParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^say (.*)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		SayPacket packet = new SayPacket();
		int n = 0;

		packet.setMessage(match.group(++n));

		return packet;
	}

}
