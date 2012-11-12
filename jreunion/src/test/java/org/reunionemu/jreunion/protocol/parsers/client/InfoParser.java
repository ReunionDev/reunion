package org.reunionemu.jreunion.protocol.parsers.client;

import java.util.regex.*;

import netty.Packet;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.packets.server.InfoPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class InfoParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^info (.+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new InfoPacket(match.group(1));
	}

}
