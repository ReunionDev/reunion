package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.*;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class SkyParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^sky (\\d+) (1|0)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		SkyPacket packet = new SkyPacket();
		int n = 0;

		packet.setId(Integer.parseInt(match.group(++n)));

		packet.setFlyStatus(Integer.parseInt(match.group(++n)) == 1);

		return packet;
	}

}
