package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.PartyDisbandPacket;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class PartyDisbandParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^party disband$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new PartyDisbandPacket();
	}
}
