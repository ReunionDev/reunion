package org.reunionemu.jreunion.protocol.parsers.server;

import java.util.regex.*;



import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.*;
import org.reunionemu.jreunion.protocol.old.*;
import org.reunionemu.jreunion.protocol.packets.client.CombatPacket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class CombatParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^combat (1|0)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		CombatPacket packet = new CombatPacket();
		int n = 0;
		packet.setInCombat(match.group(++n).equals("1"));

		return packet;
	}

}
