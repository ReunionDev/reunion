package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.EventPacket;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class EventParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^event (.+)$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new EventPacket(match.group(1));
	}

}
