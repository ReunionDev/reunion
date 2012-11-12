package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;

import netty.Packet;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Client;
import org.reunionemu.jreunion.protocol.packets.client.StartGamePacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class StartGameParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^start_game$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new StartGamePacket();
	}
}
