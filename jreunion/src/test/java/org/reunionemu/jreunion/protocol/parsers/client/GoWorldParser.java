package org.reunionemu.jreunion.protocol.parsers.client;

import java.net.*;
import java.util.regex.*;


import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.PacketParser.Server;
import org.reunionemu.jreunion.protocol.packets.server.GoWorldPacket;
import org.slf4j.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class GoWorldParser implements PacketParser {

	private static final Logger logger = LoggerFactory
			.getLogger(GoWorldParser.class);

	static final Pattern regex = Pattern
			.compile("^go_world (\\d+\\.\\d+\\.\\d+\\.\\d+) (\\d+) (\\d+) (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		GoWorldPacket packet = new GoWorldPacket();
		int n = 0;
		try {
			packet.setAddress((Inet4Address) Inet4Address.getByName(match
					.group(++n)));
		} catch (UnknownHostException e) {
			logger.warn(e.getMessage(), e);
		}

		packet.setPort(Integer.parseInt(match.group(++n)));

		packet.setMapId(Integer.parseInt(match.group(++n)));

		packet.setUnknown(Integer.parseInt(match.group(++n)));

		return packet;
	}

}
