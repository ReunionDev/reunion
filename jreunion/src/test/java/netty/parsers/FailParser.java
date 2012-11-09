package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.FailPacket;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class FailParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^fail(?: (.+))?$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new FailPacket(match.group(1));
	}

}
