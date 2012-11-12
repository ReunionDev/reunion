package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;



import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Client;
import org.reunionemu.jreunion.protocol.old.*;
import org.reunionemu.jreunion.protocol.packets.client.SkillUpPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class SkillUpParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^skillup (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new SkillUpPacket(Integer.parseInt(match.group(1)));
	}

}
