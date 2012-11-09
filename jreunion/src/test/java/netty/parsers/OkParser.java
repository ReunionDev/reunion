package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.OkPacket;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class OkParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^OK$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new OkPacket();
	}
}
