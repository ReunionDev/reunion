package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;


import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Client;
import org.reunionemu.jreunion.protocol.packets.client.CharExistPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class CharExistParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^char_exist ([^ ]+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new CharExistPacket(match.group(1));
	}

}
