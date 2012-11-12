package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;

import netty.Packet;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Client;
import org.reunionemu.jreunion.protocol.packets.client.StartPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class StartParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^start (\\d+) (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		StartPacket packet = new StartPacket();
		int n = 0;
		packet.setSlot(Integer.parseInt(match.group(++n)));

		packet.setZone(Integer.parseInt(match.group(++n)));

		return packet;
	}

}
