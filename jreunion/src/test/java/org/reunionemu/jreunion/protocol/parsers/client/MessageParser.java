package org.reunionemu.jreunion.protocol.parsers.client;

import java.util.regex.*;


import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.packets.server.MessagePacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class MessageParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^msg (.+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new MessagePacket(match.group(1));
	}

}
