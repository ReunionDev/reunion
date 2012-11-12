package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;

import netty.Packet;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Client;
import org.reunionemu.jreunion.protocol.packets.client.CharDeletePacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class CharDeleteParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^char_del (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new CharDeletePacket(Integer.parseInt(match.group(1)));
	}

}
