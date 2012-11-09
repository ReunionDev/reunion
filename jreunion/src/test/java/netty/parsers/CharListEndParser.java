package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.CharListEndPacket;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class CharListEndParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^chars_end (\\d+) (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		CharListEndPacket packet = new CharListEndPacket();
		packet.setUnknown(Integer.parseInt(match.group(1)));
		packet.setAccountId(Integer.parseInt(match.group(2)));
		return packet;
	}

}
