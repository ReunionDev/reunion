package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.StatusPacket;
import netty.parsers.PacketParser.Server;

import org.reunionemu.jreunion.game.Player.Status;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class StatusParser implements PacketParser {

	static final Pattern regex = Pattern
			.compile("^status (\\d+) (\\d+) (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		StatusPacket packet = new StatusPacket();
		int n = 0;
		packet.setStatusType(Status.byValue(Integer.parseInt(match.group(++n))));
		packet.setValue(Long.parseLong(match.group(++n)));
		packet.setMax(Long.parseLong(match.group(++n)));
		return packet;
	}

}
